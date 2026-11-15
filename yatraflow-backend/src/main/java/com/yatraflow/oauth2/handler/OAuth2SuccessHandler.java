package com.yatraflow.oauth2.handler;

import com.yatraflow.oauth2.entity.OAuth2Account;
import com.yatraflow.oauth2.enums.AuthProvider;
import com.yatraflow.oauth2.repository.OAuth2AccountRepository;
import com.yatraflow.security.jwt.JwtProperties;
import com.yatraflow.security.jwt.JwtService;
import com.yatraflow.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    private final OAuth2AccountRepository oauth2AccountRepository;

    @Value("${app.frontend.oauth2-success-url:http://localhost:5173/oauth2/success}")
    private String frontendSuccessUrl;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException{

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        String registrationId =
                oauthToken.getAuthorizedClientRegistrationId();

        String providerUserId =
                oauthToken.getPrincipal().getName();

        AuthProvider provider = AuthProvider.valueOf(
                registrationId.toUpperCase()
        );

        log.info("OAuth login successful | provider={}", provider);

        OAuth2Account oauth2Account = oauth2AccountRepository
                .findWithUserAndRolesByProviderAndProviderUserId(
                        provider,
                        providerUserId
                )
                .orElseThrow(() -> new IllegalStateException(
                        "OAuth account mapping not found."

                ));

        User user = oauth2Account.getUser();

        if (!Boolean.TRUE.equals(user.getEnabled())
                || Boolean.TRUE.equals(user.getAccountLocked())) {
            log.warn("OAuth login rejected | userId={}", user.getId());
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Your account is disabled or locked."
            );
            return;
        }

        // Generate Yatraflow JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Access token cookie
        ResponseCookie accessTokenCookie = ResponseCookie
                .from("access_token", accessToken)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();

        // Refresh token cookie
        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(jwtProperties.getRefreshTokenExpiration() / 1000)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                accessTokenCookie.toString()
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshTokenCookie.toString()
        );

        log.info(
                "OAuth login completed | provider={} | userId={}",
                provider,
                user.getId()
        );

        response.sendRedirect(frontendSuccessUrl);



    }
}
