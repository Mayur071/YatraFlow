package com.yatraflow.security.config;

import com.yatraflow.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
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


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

        // -------------------------------------------------
        // CSRF
        // -------------------------------------------------
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


        // -------------------------------------------------
        // Session Management
        // -------------------------------------------------

                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))

        // -------------------------------------------------
        // Authorization
        // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        //Public Authentication APIs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/auth/logout",
                                "/api/v1/test/**"
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
                        .hasAnyRole("USER","ADMIN")

                        //Everything else requires  authentication
                        .anyRequest().authenticated()
                )
//
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                )

                // -------------------------------------------------
                // Authentication / Authorization Exceptions
                // -------------------------------------------------

                .exceptionHandling(exception -> exception

                        // No authentication / invalid authentication
                        .authenticationEntryPoint(
                                (request, response, ex) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write(
                                            "{\"error\":\"Unauthorized\",\"message\":\"Authentication is required.\"}"
                                    );
                                }
                        )

                        // Authenticated but insufficient permission
                        .accessDeniedHandler(
                                (request, response, ex) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.getWriter().write(
                                            "{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource.\"}"
                                    );
                                }
                        )
                )

        // -------------------------------------------------
        // Disable default authentication mechanisms
        // -------------------------------------------------

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

        // -------------------------------------------------
        // JWT Filter
        // -------------------------------------------------

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return httpSecurity.build();
    }




}
