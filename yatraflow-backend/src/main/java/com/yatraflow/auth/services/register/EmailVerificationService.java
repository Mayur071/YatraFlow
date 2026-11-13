package com.yatraflow.auth.services.register;

import com.yatraflow.auth.entity.PendingRegistration;

public interface EmailVerificationService {

    // Generate OTP and send verification email
    void createAndSendVerificationOtp(PendingRegistration pendingRegistration);

    // Verify submitted OTP and complete user registration.
    boolean verifyEmailOtp(String email, String otp);

    // Generate and send a new OTP.
    void resendVerificationOtp(String email);



}
