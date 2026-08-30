package com.yatraflow.auth.controller;

import com.yatraflow.auth.dto.request.LoginRequest;
import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.LoginResponse;
import com.yatraflow.auth.dto.response.RegisterResponse;
import com.yatraflow.auth.services.login.LoginResult;
import com.yatraflow.auth.services.login.LoginService;
import com.yatraflow.auth.services.register.RegisterService;
import com.yatraflow.security.jwt.JwtProperties;
import com.yatraflow.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    private  final RegisterService registerService;

    private final LoginService loginService;

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
            ) {

        log.info("Registration request received for email: {}",request.getEmail());

        RegisterResponse response = registerService.register(request);

        log.info("Registration completed successfully for email : {}",response.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request){

        log.info("Login request received for email: {}",request.getEmail());

        LoginResult loginResult = loginService.login(request);

        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token",loginResult.accessToken())
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();

        log.info("Login Completed Successfully for email: {}",request.getEmail());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,accessTokenCookie.toString())
                .body(loginResult.loginResponse());



    }
}
