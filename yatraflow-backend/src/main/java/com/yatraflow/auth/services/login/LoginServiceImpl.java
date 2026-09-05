package com.yatraflow.auth.services.login;

import com.yatraflow.auth.dto.request.LoginRequest;
import com.yatraflow.auth.dto.response.LoginResponse;
import com.yatraflow.exception.ForbiddenException;
import com.yatraflow.exception.UnauthorizedException;
import com.yatraflow.security.jwt.JwtProperties;
import com.yatraflow.security.jwt.JwtService;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoginServiceImpl implements LoginService {


    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;


    @Override
    public  LoginResult login(LoginRequest loginRequest) {

        log.debug("Authenticating user | email={}", loginRequest.getEmail());

        User user = userService.getUserByEmail(loginRequest.getEmail());

        validateAccountStatus(user);

        validateLoginPassword(loginRequest.getPassword(),user.getPassword());

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        LoginResponse loginResponse = buildLoginResponse(user);

        log.info(
                "LOGIN_SUCCESS userId={} email={} roles={}",
                user.getId(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .toList()
        );

        return new LoginResult(loginResponse, accessToken, refreshToken);
    }

    // ---------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------

    private void validateAccountStatus(User user){

        if(!user.getEnabled()){
            log.warn(
                    "Login rejected | reason=ACCOUNT_DISABLED | email={}",
                    user.getEmail()
            );

            throw new ForbiddenException("Your account is disabled.");
        }

        if (user.getAccountLocked()) {

            log.warn(
                    "Login rejected | reason=ACCOUNT_LOCKED | email={}",
                    user.getEmail()
            );

            throw new ForbiddenException("Your account is locked");
        }
    }

    private  void validateLoginPassword(String rawPassword, String encodePassword){

        if(!passwordEncoder.matches(rawPassword,encodePassword)) {

            log.warn(
                    "Login rejected | reason=INVALID_PASSWORD | email={}"

            );

            throw new UnauthorizedException("Invalid username or password.");
        }

    }

    private LoginResponse buildLoginResponse(User user){

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .toList();


        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(roles)
                .tokenType("Bearer")
                .expiresIn(
                        jwtProperties.getAccessTokenExpiration() / 1000
                )
                .refreshExpiresIn(
                        jwtProperties.getRefreshTokenExpiration() / 1000
                )
                .build();


    }

}
