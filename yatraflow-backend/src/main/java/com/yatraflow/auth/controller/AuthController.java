package com.yatraflow.auth.controller;

import com.yatraflow.auth.dto.request.LoginRequest;
import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.EmailVerificationResponse;
import com.yatraflow.auth.dto.response.LoginResponse;
import com.yatraflow.auth.dto.response.RegisterResponse;
import com.yatraflow.auth.services.login.LoginResult;
import com.yatraflow.auth.services.login.LoginService;
import com.yatraflow.auth.services.register.EmailVerificationService;
import com.yatraflow.auth.services.register.RegisterService;
import com.yatraflow.exception.ForbiddenException;
import com.yatraflow.exception.UnauthorizedException;
import com.yatraflow.security.jwt.JwtProperties;
import com.yatraflow.security.jwt.JwtService;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private  final RegisterService registerService;

    private final LoginService loginService;

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserService userService;

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
            ) {

        log.info("User registration initiated | email={}", request.getEmail());

        RegisterResponse response = registerService.register(request);

        log.info(
                "User registration completed | userId={} | email={}",
                response.email()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request){

        log.info("User login initiated | email={}", request.getEmail());

        LoginResult loginResult = loginService.login(request);

        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token",loginResult.accessToken())
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refresh_token", loginResult.refreshToken())
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(jwtProperties.getRefreshTokenExpiration() / 1000)
                .build();



        log.info(
                "User login completed | userId={} | email={}",
                loginResult.loginResponse().userId(),
                loginResult.loginResponse().email()
        );



        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResult.loginResponse());




    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false)
            String refreshToken
    ) {
        log.info("Refresh  token request received");

        if(refreshToken == null || refreshToken.isBlank()){
            log.warn("Refresh token request rejected | reason=REFRESH_TOKEN_MISSING");
            throw new UnauthorizedException("Refresh token is missing ");
        }

        // Validate token type
        String tokenType = jwtService.extractTokenType(refreshToken);

        if(!"REFRESH".equals(tokenType)){

            log.warn("Refresh token request rejected | reason=INVALID_ID_TOKEN_TYPE | tokenType={}",tokenType);

            throw new UnauthorizedException("Invalid refresh token");
        }


        // Extract user from refresh token
        String email = jwtService.extractUsername(refreshToken);

        log.debug("Refresh token validated | email={}", email);

        User user =  userService.getUserByEmail(email);

        // Validate account

        if(!user.getEnabled()){
            log.warn(
                    "Refresh token rejected | reason=ACCOUNT_DISABLED | userId={} | email={}",
                    user.getId(),
                    user.getEmail()
            );

            throw new ForbiddenException("Your Account is Disabled");
        }


        if (user.getAccountLocked()) {
            log.warn(
                    "Refresh token rejected | reason=ACCOUNT_LOCKED | userId={} | email={}",
                    user.getId(),
                    user.getEmail()
            );

            throw new ForbiddenException("Your account is locked.");
        }

        //Generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token",accessToken)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();

        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName().name())
                                .toList()
                )
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .refreshExpiresIn(jwtProperties.getRefreshTokenExpiration() / 1000)
                .build();

        log.info(
                "ACCESS_TOKEN_REFRESHED userId={} email={} roles={} expiresIn={}s",
                user.getId(),
                user.getEmail(),
                response.roles(),
                response.expiresIn()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,accessTokenCookie.toString()).body(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        log.info("User logout request received");

        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token", "")
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        log.info("User logout completed successfully");

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> getCsrfToken(CsrfToken csrfToken) {

        return ResponseEntity.ok(
                Map.of("token", csrfToken.getToken())
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponse> verifyEmail(
            @RequestParam("token") String token
    ){
        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok(
                new EmailVerificationResponse(
                        "Email verified successfully. Your YatraFlow account is now active."
        )
        );
    }



}
