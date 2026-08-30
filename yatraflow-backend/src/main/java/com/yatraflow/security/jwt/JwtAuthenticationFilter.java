package com.yatraflow.security.jwt;

import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    private final JwtService jwtService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{

        String token = extractTokenFromCookie(request);

        if(token == null){

            filterChain.doFilter(request, response);

            return;
        }

        try{
            String username = jwtService.extractUsername(token);

            if(SecurityContextHolder.getContext().getAuthentication() == null){
                User user = userService.getUserByEmail(username);

                if(jwtService.isTokenValid(token,user)) {

                    List<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token)
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user,null,authorities);

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    log.debug("JWT authentication successful for user: {}" ,username);
                }
            }
        } catch (RuntimeException ex){

            log.debug("JWT authentication failed for request: {}",
                    request.getRequestURI(),ex);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write(" \"{\\\"error\\\":\\\"Unauthorized\\\",\\\"message\\\":\\\"Invalid or expired authentication token.\\\"}\"");

            return;
        }

        filterChain.doFilter(request,response);
    }



    // =========================================================
    // Extract JWT from Cookie
    // =========================================================

    private String extractTokenFromCookie(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
