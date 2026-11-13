package com.yatraflow.auth.controller;

import com.yatraflow.auth.dto.request.ResendVerificationRequest;
import com.yatraflow.auth.dto.response.ResendVerificationResponse;
import com.yatraflow.auth.services.register.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class ResendVerificationController {


    private final EmailVerificationService emailVerificationService;

    // =========================================================
    // RESEND EMAIL VERIFICATION OTP
    // =========================================================

    @PostMapping("/resend-verification-otp")
    public ResponseEntity<ResendVerificationResponse> resendVerificationOtp(
            @Valid @RequestBody ResendVerificationRequest request
    ) {

        log.info(
                "Resend verification OTP request received | email={}",
                request.getEmail()
        );

        emailVerificationService.resendVerificationOtp(
                request.getEmail()
        );

        return ResponseEntity.ok(
                new ResendVerificationResponse(
                        "A new verification OTP has been sent successfully."
                )
        );
    }
}
