package com.yatraflow.auth.services.email;

import com.yatraflow.auth.entity.EmailVerificationToken;
import com.yatraflow.auth.entity.PendingRegistration;

public interface EmailVerificationTokenService {

    String createVerificationToken(PendingRegistration pendingRegistration);

    EmailVerificationToken getValidateToken(String rawToken);

    void markTokenUsed(EmailVerificationToken token);

    void deleteToken(PendingRegistration pendingRegistration);
}
