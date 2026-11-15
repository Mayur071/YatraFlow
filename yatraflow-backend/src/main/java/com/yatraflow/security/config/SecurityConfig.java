
package com.yatraflow.security.config;

import com.yatraflow.oauth2.handler.OAuth2SuccessHandler;
import com.yatraflow.oauth2.service.GitHubOAuth2UserService;
import com.yatraflow.oauth2.service.GoogleOidcUserService;
import com.yatraflow.security.jwt.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final GoogleOidcUserService googleOidcUserService;

    private final GitHubOAuth2UserService gitHubOAuth2UserService;

    private final GoogleOidcUserService getGoogleOidcUserService;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
            throws Exception {

        httpSecurity

                // CSRF
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .csrfTokenRequestHandler(
                                new CsrfTokenRequestAttributeHandler()
                        )
                        .ignoringRequestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/test/**"
                        )
                )

                // Session Management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // Authorization
                .authorizeHttpRequests(auth -> auth

                        // OAuth2 endpoints
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()

                        // Public Authentication APIs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/test/**",
                                "/api/v1/auth/verify-email-otp",
                                "/api/v1/auth/resend-verification-otp"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/auth/csrf"
                        ).permitAll()

                        // Admin only
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // User + Admin
                        .requestMatchers("/api/v1/user/**")
                        .hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                // Exception Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized\",\"message\":\"Authentication is required.\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource.\"}"
                            );
                        })
                )

                // Disable default authentication
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(gitHubOAuth2UserService)
                                .oidcUserService(googleOidcUserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )

                // JWT Filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return httpSecurity.build();
    }
}
