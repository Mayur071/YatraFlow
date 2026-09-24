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

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class ResendVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/resend-verification")
    public ResponseEntity<ResendVerificationResponse> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {

        emailVerificationService.resendVerificationEmail(
                request.getEmail()
        );

        return ResponseEntity.ok(
                new ResendVerificationResponse(
                        "A new verification email has been sent successfully."
                )
        );
    }
}
