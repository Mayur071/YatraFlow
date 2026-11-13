package com.yatraflow.auth.services.verification;

import com.yatraflow.auth.entity.EmailVerificationOtp;
import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.auth.repository.EmailVerificationOtpRepository;
import com.yatraflow.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationOtpServiceImpl implements EmailVerificationOtpService {


    private static final int OTP_LENGTH = 6;

//    private static final int OTP_EXPIRY_MINUTES = 5;

    private static final int MAX_ATTEMPTS = 5;

    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private static final int MAX_RESENDS_PER_HOUR = 5;

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final EmailVerificationOtpRepository otpRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.email-verification.otp-hmac-secret}")
    private String otpHmacSecret;

    @Value("${app.email-verification.otp-expiry-minutes:5}")
    private int otpExpiryMinutes;


    // =========================================================
    // CREATE OTP
    // =========================================================

    @Override
    public String createOtp(PendingRegistration pendingRegistration) {

        validatePendingRegistration(pendingRegistration);

        log.info(
                "Creating email verification OTP | pendingRegistrationId={}",
                pendingRegistration.getId()
        );

        String rawOtp = generateOtp();

        String otpHash = hashOtp(rawOtp);

        LocalDateTime now = LocalDateTime.now();

        Optional<EmailVerificationOtp> existingOtp =
                otpRepository.findByPendingRegistration_Id(
                        pendingRegistration.getId()
                );

        EmailVerificationOtp verificationOtp;

        if (existingOtp.isPresent()) {

            // Replace the existing OTP record.
            verificationOtp = existingOtp.get();

            verificationOtp.setOtpHash(otpHash);
            verificationOtp.setExpiresAt(
                    now.plusMinutes(otpExpiryMinutes)
            );
            verificationOtp.setLastSentAt(now);
            verificationOtp.setResendWindowStartedAt(now);
            verificationOtp.setAttemptCount(0);
            verificationOtp.setResendCount(0);
            verificationOtp.setUsed(false);

        } else {

            verificationOtp = EmailVerificationOtp.builder()
                    .pendingRegistration(pendingRegistration)
                    .otpHash(otpHash)
                    .expiresAt(now.plusMinutes(otpExpiryMinutes))
                    .lastSentAt(now)
                    .resendWindowStartedAt(now)
                    .attemptCount(0)
                    .resendCount(0)
                    .used(false)
                    .build();
        }

        otpRepository.save(verificationOtp);

        log.info(
                "Email verification OTP created successfully | pendingRegistrationId={}",
                pendingRegistration.getId()
        );

        // Return raw OTP only for sending through email.
        return rawOtp;
    }


    // =========================================================
    // RESEND OTP
    // =========================================================

    @Override
    public String resendOtp(PendingRegistration pendingRegistration) {

        validatePendingRegistration(pendingRegistration);

        EmailVerificationOtp verificationOtp =
                otpRepository.findByPendingRegistrationIdForUpdate(
                                pendingRegistration.getId()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "No verification OTP found. Please register again."
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        if (verificationOtp.isUsed()) {

            throw new BusinessException(
                    "Email has already been verified."
            );
        }

        // -----------------------------------------------------
        // Check resend cooldown
        // -----------------------------------------------------

        LocalDateTime nextAllowedResend =
                verificationOtp.getLastSentAt()
                        .plusSeconds(RESEND_COOLDOWN_SECONDS);

        if (now.isBefore(nextAllowedResend)) {

            throw new BusinessException(
                    "Please wait before requesting another OTP."
            );
        }

        // -----------------------------------------------------
        // Check hourly resend window
        // -----------------------------------------------------

        LocalDateTime windowStart =
                verificationOtp.getResendWindowStartedAt();

        if (!now.isBefore(windowStart.plusHours(1))) {

            // Start a fresh one-hour resend window.
            verificationOtp.setResendWindowStartedAt(now);
            verificationOtp.setResendCount(0);
        }

        if (verificationOtp.getResendCount() >= MAX_RESENDS_PER_HOUR) {

            throw new BusinessException(
                    "Maximum OTP resend limit reached. Please try again later."
            );
        }

        // -----------------------------------------------------
        // Generate fresh OTP
        // -----------------------------------------------------

        String rawOtp = generateOtp();

        verificationOtp.setOtpHash(hashOtp(rawOtp));

        verificationOtp.setExpiresAt(
                now.plusMinutes(otpExpiryMinutes)
        );

        verificationOtp.setLastSentAt(now);

        verificationOtp.setResendCount(
                verificationOtp.getResendCount() + 1
        );

        // A new OTP gets a fresh attempt counter.
        verificationOtp.setAttemptCount(0);

        verificationOtp.setUsed(false);

        otpRepository.save(verificationOtp);

        log.info(
                "Verification OTP regenerated | pendingRegistrationId={} | resendCount={}",
                pendingRegistration.getId(),
                verificationOtp.getResendCount()
        );

        return rawOtp;
    }


    // =========================================================
    // GET OTP FOR VERIFICATION
    // =========================================================

    @Override
    public EmailVerificationOtp getOtpForVerification(
            PendingRegistration pendingRegistration
    ) {

        validatePendingRegistration(pendingRegistration);

        EmailVerificationOtp verificationOtp =
                otpRepository.findByPendingRegistrationIdForUpdate(
                                pendingRegistration.getId()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Verification OTP not found. Please request a new OTP."
                                )
                        );

        if (verificationOtp.isUsed()) {

            throw new BusinessException(
                    "This verification OTP has already been used."
            );
        }

        if (!verificationOtp.getExpiresAt().isAfter(LocalDateTime.now())) {

            throw new BusinessException(
                    "Verification OTP has expired. Please request a new OTP."
            );
        }

        if (verificationOtp.getAttemptCount() >= MAX_ATTEMPTS) {

            throw new BusinessException(
                    "Maximum OTP verification attempts reached. Please request a new OTP."
            );
        }

        return verificationOtp;
    }


    // =========================================================
    // MATCH OTP
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public boolean matchesOtp(String rawOtp, String otpHash) {

        if (rawOtp == null
                || rawOtp.length() != OTP_LENGTH
                || !rawOtp.matches("\\d+")) {
            return false;
        }

        if (otpHash == null || otpHash.isBlank()) {
            return false;
        }

        String submittedOtpHash = hashOtp(rawOtp);

        return MessageDigest.isEqual(
                submittedOtpHash.getBytes(StandardCharsets.UTF_8),
                otpHash.getBytes(StandardCharsets.UTF_8)
        );
    }


    // =========================================================
    // RECORD FAILED ATTEMPT
    // =========================================================

    @Override
    public void recordFailedAttempt(EmailVerificationOtp otp) {

        if (otp == null || otp.getPendingRegistration() == null) {

            throw new BusinessException(
                    "Invalid OTP verification record."
            );
        }

        EmailVerificationOtp managedOtp =
                otpRepository.findByPendingRegistrationIdForUpdate(
                                otp.getPendingRegistration().getId()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Verification OTP not found."
                                )
                        );

        if (managedOtp.getAttemptCount() < MAX_ATTEMPTS) {

            managedOtp.setAttemptCount(
                    managedOtp.getAttemptCount() + 1
            );

            otpRepository.save(managedOtp);
        }

        log.warn(
                "Failed OTP verification attempt | pendingRegistrationId={} | attempts={}",
                managedOtp.getPendingRegistration().getId(),
                managedOtp.getAttemptCount()
        );
    }


    // =========================================================
    // MARK OTP USED
    // =========================================================

    @Override
    public void markOtpUsed(EmailVerificationOtp otp) {

        if (otp == null || otp.getPendingRegistration() == null) {

            throw new BusinessException(
                    "Invalid OTP verification record."
            );
        }

        EmailVerificationOtp managedOtp =
                otpRepository.findByPendingRegistrationIdForUpdate(
                                otp.getPendingRegistration().getId()
                        )
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Verification OTP not found."
                                )
                        );

        if (managedOtp.isUsed()) {

            throw new BusinessException(
                    "Verification OTP has already been used."
            );
        }

        managedOtp.setUsed(true);

        otpRepository.save(managedOtp);

        log.info(
                "Verification OTP marked as used | pendingRegistrationId={}",
                managedOtp.getPendingRegistration().getId()
        );
    }

    @Override
    public void deleteOtp(PendingRegistration pendingRegistration) {
        validatePendingRegistration(pendingRegistration);

        otpRepository.deleteByPendingRegistration_Id(pendingRegistration.getId());
    }


    // =========================================================
    // PRIVATE HELPER METHODS
    // =========================================================

    private String generateOtp() {

        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }

        return otp.toString();
    }


    private String hashOtp(String rawOtp) {

        if (otpHmacSecret == null
                || otpHmacSecret.getBytes(StandardCharsets.UTF_8).length < 32) {

            throw new IllegalStateException(
                    "OTP HMAC secret must contain at least 32 bytes."
            );
        }

        try {

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKey = new SecretKeySpec(
                    otpHmacSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(secretKey);

            byte[] hash = mac.doFinal(
                    rawOtp.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to securely hash verification OTP.",
                    exception
            );
        }
    }


    private void validatePendingRegistration(
            PendingRegistration pendingRegistration
    ) {

        if (pendingRegistration == null
                || pendingRegistration.getId() == null) {

            throw new BusinessException(
                    "Valid pending registration is required."
            );
        }
    }


}
