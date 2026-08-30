package com.yatraflow.security.config;

import com.yatraflow.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .csrf(csrf -> csrf.disable())

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
                                "/api/v1/auth/register",
                                "/api/v1/auth/login"
                        ).permitAll()

                        //Everything else requires  authentication
                        .anyRequest().authenticated()
                )

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
