package com.yatraflow.auth.services.verification;

import com.yatraflow.auth.entity.EmailVerificationOtp;
import com.yatraflow.auth.entity.PendingRegistration;

public interface EmailVerificationOtpService {

    // Generate a new OTP and save its secure hash.
    String createOtp(PendingRegistration pendingRegistration);

    // Generate and replace an existing OTP after
    // validating resend cooldown and resend limits.
    String resendOtp(PendingRegistration pendingRegistration);

    // Retrieve OTP record using a database write lock.
    EmailVerificationOtp getOtpForVerification(
            PendingRegistration pendingRegistration
    );

    // Compare the submitted OTP with the stored hash.
    boolean matchesOtp(String rawOtp, String otpHash);

    // Record an incorrect OTP verification attempt.
    void recordFailedAttempt(EmailVerificationOtp otp);

    // Mark the OTP as used after successful verification.
    void markOtpUsed(EmailVerificationOtp otp);

    void deleteOtp(PendingRegistration pendingRegistration);
}
