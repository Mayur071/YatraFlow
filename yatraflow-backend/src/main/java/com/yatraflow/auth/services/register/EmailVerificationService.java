package com.yatraflow.auth.services.register;

import com.yatraflow.auth.entity.PendingRegistration;

public interface EmailVerificationService {

    void createAndSendVerificationEmail(PendingRegistration pendingRegistration);

    void verifyEmail(String rawToken);
}
