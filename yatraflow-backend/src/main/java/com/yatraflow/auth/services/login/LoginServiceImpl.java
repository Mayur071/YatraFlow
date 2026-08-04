package com.yatraflow.auth.services.login;

import com.yatraflow.auth.dto.request.LoginRequest;
import com.yatraflow.auth.dto.response.LoginResponse;
import com.yatraflow.exception.ForbiddenException;
import com.yatraflow.exception.UnauthorizedException;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoginServiceImpl implements LoginService {


    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        log.info("Login request received for email: {}",loginRequest.getEmail());

        User user = userService.getUserByEmail(loginRequest.getEmail());

        validateAccountStatus(user);

        validateLoginPassword(loginRequest.getPassword(),user.getPassword());

        log.info("User logged in succesfully: {}",user.getEmail());


        return buildLoginResponse(user);
    }

    // ---------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------

    private void validateAccountStatus(User user){

        if(!user.getEnabled()){
            log.warn("Login failed. Account disabled: {}",user.getEmail());

            throw new ForbiddenException("Your account is disabled.");
        }

        if (user.getAccountLocked()) {

            log.warn("Login failed. Account locked: {}", user.getEmail());

            throw new ForbiddenException("Your account is locked");
        }
    }

    private  void validateLoginPassword(String rawPassword, String encodePassword){

        if(!passwordEncoder.matches(rawPassword,encodePassword)) {

            log.warn("Login failed. Invalid credentials");

            throw new UnauthorizedException("Invalid username or password.");
        }

    }

    private LoginResponse buildLoginResponse(User user){

        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

}
