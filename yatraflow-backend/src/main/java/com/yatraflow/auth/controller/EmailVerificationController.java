package com.yatraflow.auth.controller;

import com.yatraflow.auth.dto.response.EmailVerificationResponse;
import com.yatraflow.auth.services.register.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponse> verifyEmail(
            @RequestParam("token") String token
    ) {

        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok(
                new EmailVerificationResponse(
                        "Email verified successfully. Your YatraFlow account is now active."
                )
        );
    }
}
