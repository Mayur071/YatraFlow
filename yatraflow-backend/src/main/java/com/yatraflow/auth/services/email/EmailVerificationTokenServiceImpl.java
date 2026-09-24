package com.yatraflow.auth.services.email;

import com.yatraflow.auth.entity.EmailVerificationToken;
import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.auth.repository.EmailVerificationTokenRepository;
import com.yatraflow.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository tokenRepository;

    private final SecureRandom secureRandom =  new SecureRandom();


    @Override
    public String createVerificationToken(PendingRegistration pendingRegistration) {

        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .pendingRegistration(pendingRegistration)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        tokenRepository.save(verificationToken);

        return rawToken;
    }

    @Override
    @Transactional(readOnly = true)
    public EmailVerificationToken getValidateToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        EmailVerificationToken token = tokenRepository.findByTokenHashAndUsedFalse(tokenHash)
                .orElseThrow(
                        () -> new BusinessException("Invalid or already used verification token")
                );

        if(token.getExpiresAt().isBefore(LocalDateTime.now())){

            throw new BusinessException("Verification token has expired");
        }

        return token;
    }

    @Override
    public void markTokenUsed(EmailVerificationToken token) {

        token.setUsed(true);

        tokenRepository.save(token);

    }

    @Override
    public void deleteToken(PendingRegistration pendingRegistration) {

        tokenRepository.deleteByPendingRegistrationId(pendingRegistration.getId());

    }

    @Override
    public Optional<EmailVerificationToken> getByPendingRegistration(PendingRegistration pendingRegistration) {
        return tokenRepository.findByPendingRegistrationId(pendingRegistration.getId());
    }

    private String generateSecureToken() {

        byte[]  randomBytes = new byte[32];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    private String hashToken(String rawToken) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            rawToken.getBytes(StandardCharsets.UTF_8)
                    );

            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    e
            );
        }
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {

            hexString.append(
                    String.format("%02x", b)
            );
        }

        return hexString.toString();
    }
}
