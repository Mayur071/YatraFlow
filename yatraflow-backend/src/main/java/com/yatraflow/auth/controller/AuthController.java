package com.yatraflow.auth.controller;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.RegisterResponse;
import com.yatraflow.auth.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private  final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
            ) {

        log.info("Registration request received for email: {}",request.getEmail());

        RegisterResponse response = authService.register(request);

        log.info("Registration completed successfully for email : {}",response.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
