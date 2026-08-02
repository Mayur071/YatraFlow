package com.yatraflow.auth.services;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.RegisterResponse;
import com.yatraflow.auth.mapper.AuthMapper;
import com.yatraflow.exception.BusinessException;
import com.yatraflow.exception.ResourceAlreadyExistsException;
import com.yatraflow.role.entity.Role;
import com.yatraflow.role.entity.RoleName;
import com.yatraflow.role.service.RoleService;
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
public class AuthServiceImpl implements AuthService {


    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final AuthMapper authMapper;


    @Override
    public RegisterResponse register(RegisterRequest request) {

        log.info("Registration request received for email: {}", request.getEmail());

        validateEmail(request.getEmail());

        validatePhoneNumber(request.getPhoneNumber());

        validatePassword(
                request.getPassword(),
                request.getConfirmPassword()
        );

        User user = authMapper.toUser(request);

        encodePassword(user);

        assignDefaultRole(user);

        User savedUser = userService.createUser(user);

        log.info("User registered successfully with email: {}", savedUser.getEmail());

        return authMapper.toRegisterResponse(savedUser);
    }


    private void validateEmail(String email){

        if(userService.existsByEmail(email)){

            log.warn("Registration failed. Email already exists: {}",email);

            throw new ResourceAlreadyExistsException("Email already registered");
        }
    }

    private void validatePhoneNumber(String phoneNumber){

        if(userService.existsByPhoneNumber(phoneNumber)){

            log.warn("Registration failed. Phone number is already registered: {}", phoneNumber);

            throw new ResourceAlreadyExistsException("phone number is already exists");
        }
    }

    private void validatePassword(String password, String confirmPassword){

        if(!password.equals(confirmPassword)) {

            log.warn("Registration failed. password mismatch");

            throw new BusinessException("\"Password and Confirm Password do not match.");
        }

    }

    private void encodePassword(User user){

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        log.debug("Password encoded successfully.");
    }


    private void assignDefaultRole(User user){

        Role role = roleService.getRoleByName(RoleName.ROLE_USER);
        user.getRoles().add(role);

        log.debug("ROLE_USER assigned successfully");
    }

}

