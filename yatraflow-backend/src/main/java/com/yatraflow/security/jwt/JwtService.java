package com.yatraflow.security.jwt;

import com.yatraflow.exception.UnauthorizedException;
import com.yatraflow.user.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties){

        this.jwtProperties=jwtProperties;
        this.secretKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    // =========================================================
    // Generate Access Token
    // =========================================================

    public String generateAccessToken(User user){

        return generateToken(user,jwtProperties.getAccessTokenExpiration(),"ACCESS");

    }


    // =========================================================
    // Generate Refresh Token
    // =========================================================

    public String generateRefreshToken(User user){

        return generateToken(user, jwtProperties.getRefreshTokenExpiration(),"REFRESH");
    }

    // =========================================================
    // Common Token Generator
    // =========================================================

    public String generateToken(User user,Long expirationMills, String tokenType){

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMills);

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .toList();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",user.getId())
                .claim("roles", roles)
                .claim("tokenType", tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }



    // =========================================================
    // Extract Username / Email
    // =========================================================

    public String extractUsername(String token){

        return parseClaims(token).getSubject();
    }

    // =========================================================
    // Extract User ID
    // =========================================================

    public Long extractUserId(String token){

        Object userId = parseClaims(token).get("userId");

        if(userId == null){
            throw new UnauthorizedException("Invalid authentication token.");
        }

        return ((Number) userId).longValue();
    }

    // =========================================================
    // Extract Roles
    // =========================================================

    public List<String> extractRoles(String token) {

        Object roles = parseClaims(token)
                .get("roles");

        if (!(roles instanceof List<?> roleList)) {
            throw new UnauthorizedException(
                    "Invalid authentication token."
            );
        }

        return roleList.stream()
                .map(String::valueOf)
                .toList();
    }

    // =========================================================
    // Validate Token
    // =========================================================

    public boolean isTokenValid(String token, User user) {

        String username = extractUsername(token);

        return username.equals(user.getEmail())
                && !isTokenExpired(token);
    }

    // =========================================================
    // Extract Token Type
    // =========================================================

    public String extractTokenType(String token){

        Object tokenType = parseClaims(token).get("tokenType");

        if(tokenType == null){
                throw new UnauthorizedException("Invalid authentication token");

        }

        return String.valueOf(tokenType);

    }

    // =========================================================
    // Check Token Expiration
    // =========================================================

    public boolean isTokenExpired(String token){

        Date expiration = parseClaims(token).getExpiration();

        return expiration.before(new java.util.Date());
    }



    // =========================================================
    // Parse & Validate JWT Claims
    // =========================================================

    private io.jsonwebtoken.Claims parseClaims(String token){

        if(token == null || token.isBlank()){
            throw new UnauthorizedException("Authentication token is missing");
        }

        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex){

            log.debug("JWT token expired.");

            throw new UnauthorizedException("Authentication token has expired.");

        } catch (JwtException | IllegalArgumentException ex){

            log.debug("JWT validation failed.");

            throw new UnauthorizedException("Invalid authentication token.");
        }

    }
}
