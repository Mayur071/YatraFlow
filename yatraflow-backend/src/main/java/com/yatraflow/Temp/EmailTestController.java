package com.yatraflow.Temp;

import com.yatraflow.notification.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test/email")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> testEmail(
            @RequestParam String email
    ) {

        emailService.sendEmail(
                email,
                "YatraFlow Email Test",
                "Hello! This is a test email from YatraFlow."
        );

        return ResponseEntity.ok("Email sent successfully");
    }
}
