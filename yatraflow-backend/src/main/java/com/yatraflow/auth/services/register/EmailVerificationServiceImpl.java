package com.yatraflow.auth.services.register;

import com.yatraflow.auth.entity.EmailVerificationOtp;
import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.auth.mapper.AuthMapper;
import com.yatraflow.auth.services.verification.EmailVerificationOtpService;
import com.yatraflow.email.service.EmailService;
import com.yatraflow.auth.services.verification.PendingRegistrationService;
import com.yatraflow.exception.BusinessException;
import com.yatraflow.exception.ResourceAlreadyExistsException;
import com.yatraflow.role.entity.Role;
import com.yatraflow.role.entity.RoleName;
import com.yatraflow.role.service.RoleService;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.Locale;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService  {

    private final EmailVerificationOtpService otpService;

    private final EmailService emailService;

    private final AuthMapper authMapper;

    private final RoleService roleService;

    private final UserService userService;

    private final TemplateEngine templateEngine;

    private final PendingRegistrationService pendingRegistrationService;

    @Value("${app.email-verification.otp-expiry-minutes:5}")
    private int otpExpiryMinutes;


    // =========================================================
    // CREATE AND SEND OTP
    // =========================================================

    @Override
    public void createAndSendVerificationOtp(
            PendingRegistration pendingRegistration
    ) {

        if (pendingRegistration == null) {
            throw new BusinessException(
                    "Pending registration is required."
            );
        }

        log.info(
                "Creating email verification OTP | pendingRegistrationId={}",
                pendingRegistration.getId()
        );

        String rawOtp = otpService.createOtp(pendingRegistration);

        sendVerificationOtpEmail(pendingRegistration, rawOtp);

        log.info(
                "Verification OTP email sent | pendingRegistrationId={}",
                pendingRegistration.getId()
        );
    }


    // =========================================================
    // VERIFY EMAIL OTP
    // =========================================================

    @Override
    public boolean verifyEmailOtp(String email, String otp) {

        String normalizedEmail = normalizeEmail(email);

        if (normalizedEmail.isBlank()) {
            throw new BusinessException("Email is required.");
        }

        if (otp == null || !otp.matches("\\d{6}")) {
            throw new BusinessException(
                    "A valid 6-digit verification OTP is required."
            );
        }

        log.info(
                "Email OTP verification initiated | email={}",
                normalizedEmail
        );

        // -----------------------------------------------------
        // 1. Check existing user
        // -----------------------------------------------------

        if (userService.existsByEmail(normalizedEmail)) {

            throw new ResourceAlreadyExistsException(
                    "Email is already registered."
            );
        }

        // -----------------------------------------------------
        // 2. Retrieve pending registration
        // -----------------------------------------------------

        PendingRegistration pendingRegistration =
                pendingRegistrationService.getByEmail(normalizedEmail);

        // -----------------------------------------------------
        // 3. Retrieve and validate OTP record
        // -----------------------------------------------------

        EmailVerificationOtp verificationOtp =
                otpService.getOtpForVerification(pendingRegistration);

        // -----------------------------------------------------
        // 4. Compare submitted OTP with stored hash
        // -----------------------------------------------------

        boolean validOtp = otpService.matchesOtp(
                otp,
                verificationOtp.getOtpHash()
        );

        if (!validOtp) {

            otpService.recordFailedAttempt(verificationOtp);

            log.warn(
                    "Email OTP verification failed | pendingRegistrationId={}",
                    pendingRegistration.getId()
            );

            // Returning false allows the transaction to commit
            // the failed-attempt counter.
            return false;
        }

        // -----------------------------------------------------
        // 5. Defensive duplicate check
        // -----------------------------------------------------

        if (userService.existsByEmail(normalizedEmail)) {

            throw new ResourceAlreadyExistsException(
                    "Email is already registered."
            );
        }

        // -----------------------------------------------------
        // 6. Convert PendingRegistration into User
        // -----------------------------------------------------

        User user = authMapper.toUser(pendingRegistration);

        if (user == null) {
            throw new BusinessException(
                    "Unable to complete registration."
            );
        }

        // -----------------------------------------------------
        // 7. Assign default USER role
        // -----------------------------------------------------

        Role userRole = roleService.getRoleByName(
                RoleName.ROLE_USER
        );

        user.getRoles().add(userRole);

        // -----------------------------------------------------
        // 8. Persist verified User
        // -----------------------------------------------------

        User savedUser = userService.createUser(user);

        // -----------------------------------------------------
        // 9. Invalidate and clean up OTP records
        // -----------------------------------------------------

        otpService.markOtpUsed(verificationOtp);

        otpService.deleteOtp(pendingRegistration);

        // -----------------------------------------------------
        // 10. Delete temporary registration
        // -----------------------------------------------------

        pendingRegistrationService.delete(pendingRegistration);

        log.info(
                "Email verification completed successfully | userId={}",
                savedUser.getId()
        );

        return true;
    }


    // =========================================================
    // RESEND VERIFICATION OTP
    // =========================================================

    @Override
    public void resendVerificationOtp(String email) {

        String normalizedEmail = normalizeEmail(email);

        if (normalizedEmail.isBlank()) {
            throw new BusinessException("Email is required.");
        }

        log.info(
                "Resend verification OTP requested | email={}",
                normalizedEmail
        );

        // -----------------------------------------------------
        // 1. Check whether email is already registered
        // -----------------------------------------------------

        if (userService.existsByEmail(normalizedEmail)) {

            throw new BusinessException(
                    "Email is already verified."
            );
        }

        // -----------------------------------------------------
        // 2. Retrieve pending registration
        // -----------------------------------------------------

        PendingRegistration pendingRegistration =
                pendingRegistrationService.getByEmail(normalizedEmail);

        // -----------------------------------------------------
        // 3. Generate fresh OTP with cooldown and rate limits
        // -----------------------------------------------------

        String rawOtp = otpService.resendOtp(pendingRegistration);

        // -----------------------------------------------------
        // 4. Send fresh OTP through SMTP
        // -----------------------------------------------------

        sendVerificationOtpEmail(pendingRegistration, rawOtp);

        log.info(
                "Verification OTP resent successfully | pendingRegistrationId={}",
                pendingRegistration.getId()
        );
    }



    // =========================================================
    // SEND OTP EMAIL
    // =========================================================

    private void sendVerificationOtpEmail(
            PendingRegistration pendingRegistration,
            String rawOtp
    ) {

        Context context = new Context();

        context.setVariable(
                "firstName",
                pendingRegistration.getFirstName()
        );

        context.setVariable("otp", rawOtp);

        context.setVariable(
                "expiryMinutes",
                otpExpiryMinutes
        );

        final String htmlContent;

        try {

            htmlContent = templateEngine.process(
                    "email/email-verification",
                    context
            );

        } catch (TemplateProcessingException exception) {

            log.error(
                    "Failed to render OTP email template | pendingRegistrationId={}",
                    pendingRegistration.getId(),
                    exception
            );

            throw new BusinessException(
                    "Unable to prepare verification email. Please try again later."
            );
        }

        emailService.sendEmail(
                pendingRegistration.getEmail(),
                "Your YatraFlow Email Verification Code",
                htmlContent
        );
    }


    // =========================================================
    // NORMALIZATION
    // =========================================================

    private String normalizeEmail(String email) {

        if (email == null) {
            return "";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

}


