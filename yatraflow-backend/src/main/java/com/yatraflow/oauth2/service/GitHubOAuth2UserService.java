package com.yatraflow.oauth2.service;

import com.yatraflow.oauth2.enums.AuthProvider;
import com.yatraflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubOAuth2UserService extends DefaultOAuth2UserService {


    private final OAuth2AccountService oauth2AccountService;
    private final RestClient.Builder restClientBuilder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User githubUser = super.loadUser(userRequest);

        Map<String, Object> attributes = githubUser.getAttributes();

        // 1. Extract GitHub identity
        String providerUserId =
                String.valueOf(attributes.get("id"));

        String login = (String) attributes.get("login");
        String fullName = (String) attributes.get("name");

        // 2. Retrieve verified GitHub email
        String verifiedEmail = fetchVerifiedEmail(userRequest);

        // 3. Resolve profile name
        String firstName;
        String lastName = "";

        if (fullName != null && !fullName.isBlank()) {

            String[] nameParts = fullName.trim().split("\\s+", 2);

            firstName = nameParts[0];

            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }

        } else {
            firstName = login;
        }

        // 4. Find or create Yatraflow user
        User user = oauth2AccountService.findOrCreateUser(
                AuthProvider.GITHUB,
                providerUserId,
                verifiedEmail,
                true,
                firstName,
                lastName
        );

        log.info(
                "GitHub authentication completed. UserId: {}",
                user.getId()
        );

        // 5. Return authenticated OAuth2 principal
        return new DefaultOAuth2User(
                githubUser.getAuthorities(),
                attributes,
                userRequest.getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName()
        );
    }

    private String fetchVerifiedEmail(OAuth2UserRequest userRequest) {

        try {

            List<Map<String, Object>> emails =
                    restClientBuilder.build()
                            .get()
                            .uri("https://api.github.com/user/emails")
                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + userRequest.getAccessToken()
                                            .getTokenValue()
                            )
                            .header(
                                    HttpHeaders.ACCEPT,
                                    MediaType.APPLICATION_JSON_VALUE
                            )
                            .retrieve()
                            .body(
                                    new ParameterizedTypeReference<>() {}
                            );

            if (emails == null) {
                throw oauth2Exception(
                        "GitHub did not return email information."
                );
            }

            return emails.stream()
                    .filter(email ->
                            Boolean.TRUE.equals(email.get("verified"))
                    )
                    .filter(email ->
                            Boolean.TRUE.equals(email.get("primary"))
                    )
                    .map(email -> (String) email.get("email"))
                    .filter(email ->
                            email != null && !email.isBlank()
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            oauth2Exception(
                                    "No verified primary GitHub email found."
                            )
                    );

        } catch (OAuth2AuthenticationException ex) {
            throw ex;

        } catch (Exception ex) {

            log.error(
                    "Unable to retrieve verified GitHub email."
            );

            throw oauth2Exception(
                    "Unable to retrieve verified GitHub email."
            );
        }
    }

    private OAuth2AuthenticationException oauth2Exception(
            String message
    ) {

        return new OAuth2AuthenticationException(
                new OAuth2Error("github_email_error"),
                message
        );
    }
}
