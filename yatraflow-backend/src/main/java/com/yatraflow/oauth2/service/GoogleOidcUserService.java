package com.yatraflow.oauth2.service;

import com.yatraflow.oauth2.enums.AuthProvider;
import com.yatraflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleOidcUserService  extends OidcUserService {


    private final OAuth2AccountService oauth2AccountService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        // 1. Validate OIDC token and load Google user
        OidcUser googleUser = super.loadUser(userRequest);

        // 2. Extract verified claims
        String providerUserId = googleUser.getSubject();

        String email = googleUser.getEmail();

        Boolean emailVerified = googleUser.getEmailVerified();

        String firstName = googleUser.getGivenName();

        String lastName = googleUser.getFamilyName();

        // 3. Validate Google email verification
        if (!Boolean.TRUE.equals(emailVerified)) {

            log.warn("Google login rejected due to unverified email.");

            throw new OAuth2AuthenticationException(
                    new OAuth2Error("google_email_not_verified"),
                    "Please use a verified Google email address."
            );
        }

        // 4. Find or create Yatraflow user
        User user = oauth2AccountService.findOrCreateUser(
                AuthProvider.GOOGLE,
                providerUserId,
                email,
                true,
                firstName,
                lastName
        );

        log.info(
                "Google authentication completed. UserId: {}",
                user.getId()
        );

        // 5. Return authenticated OIDC principal
        return googleUser;
    }
}
