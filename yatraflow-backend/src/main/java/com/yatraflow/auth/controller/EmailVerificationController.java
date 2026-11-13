package com.yatraflow.auth.controller;

import com.yatraflow.auth.dto.request.EmailOtpVerificationRequest;
import com.yatraflow.auth.dto.response.EmailVerificationResponse;
import com.yatraflow.auth.services.register.EmailVerificationService;
import com.yatraflow.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // =========================================================
    // VERIFY EMAIL USING OTP
    // =========================================================

    @PostMapping("/verify-email-otp")
    public ResponseEntity<EmailVerificationResponse> verifyEmailOtp(
            @Valid @RequestBody EmailOtpVerificationRequest request
    ) {

        log.info(
                "Email OTP verification request received | email={}",
                request.getEmail()
        );

        boolean verified = emailVerificationService.verifyEmailOtp(
                request.getEmail(),
                request.getOtp()
        );

        if (!verified) {
            throw new BusinessException("Invalid verification OTP.");
        }

        log.info(
                "Email verification completed successfully | email={}",
                request.getEmail()
        );

        return ResponseEntity.ok(
                new EmailVerificationResponse(
                        "Email verified successfully. Your YatraFlow account is now active."
                )
        );
    }
}
