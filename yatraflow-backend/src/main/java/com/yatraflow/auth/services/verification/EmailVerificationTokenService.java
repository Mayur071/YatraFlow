package com.yatraflow.auth.services.verification;

import com.yatraflow.auth.entity.EmailVerificationToken;
import com.yatraflow.auth.entity.PendingRegistration;

import java.util.Optional;

public interface EmailVerificationTokenService {

    String createVerificationToken(PendingRegistration pendingRegistration);

    EmailVerificationToken getValidateToken(String rawToken);

    void markTokenUsed(EmailVerificationToken token);

    void deleteToken(PendingRegistration pendingRegistration);

    Optional<EmailVerificationToken> getByPendingRegistration(
            PendingRegistration pendingRegistration
    );
}
