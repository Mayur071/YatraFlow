package com.yatraflow.auth.services.register;

import com.yatraflow.auth.dto.request.RegisterRequest;
import com.yatraflow.auth.dto.response.RegisterResponse;
import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.auth.services.verification.PendingRegistrationService;
import com.yatraflow.exception.BusinessException;
import com.yatraflow.exception.ResourceAlreadyExistsException;
import com.yatraflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegisterServiceImpl implements RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PendingRegistrationService pendingRegistrationService;
    private final EmailVerificationService emailVerificationService;


    @Override
    public RegisterResponse register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());
        String phoneNumber = normalizePhoneNumber(request.getPhoneNumber());

        log.info(
                "Registration request received | email={}",
                email
        );

        // ---------------------------------------------------------
        // Validation
        // ---------------------------------------------------------

        validateEmail(email);

        validatePhoneNumber(phoneNumber);

        validatePassword(
                request.getPassword(),
                request.getConfirmPassword()
        );

        // ---------------------------------------------------------
        // Password hashing
        // ---------------------------------------------------------

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        // ---------------------------------------------------------
        // Create temporary registration
        // ---------------------------------------------------------

        PendingRegistration pendingRegistration =
                pendingRegistrationService.createPendingRegistration(
                        email,
                        passwordHash,
                        request.getFirstName().trim(),
                        request.getLastName().trim(),
                        phoneNumber
                );

        // ---------------------------------------------------------
        // Create verification token + send verification email
        // ---------------------------------------------------------

        emailVerificationService.createAndSendVerificationEmail(
                pendingRegistration
        );

        log.info(
                "Registration initiated successfully | email={}",
                email
        );

        // User is NOT created yet.
        return RegisterResponse.builder()
                .message(
                        "Registration initiated. Please verify your email to complete registration."
                )
                .email(email)
                .build();
    }


    // ---------------------------------------------------------
    // Validation
    // ---------------------------------------------------------

    private void validateEmail(String email) {

        // Already registered user
        if (userService.existsByEmail(email)) {

            log.warn(
                    "Registration rejected | reason=EMAIL_ALREADY_REGISTERED | email={}",
                    email
            );

            throw new ResourceAlreadyExistsException(
                    "Email already registered"
            );
        }

        // Verification already pending
        if (pendingRegistrationService.existsByEmail(email)) {

            log.warn(
                    "Registration rejected | reason=EMAIL_VERIFICATION_PENDING | email={}",
                    email
            );

            throw new BusinessException(
                    "Email verification is already pending. Please verify your email."
            );
        }
    }


    private void validatePhoneNumber(String phoneNumber) {

        if (userService.existsByPhoneNumber(phoneNumber)) {

            log.warn(
                    "Registration rejected | reason=PHONE_ALREADY_REGISTERED | phone={}",
                    phoneNumber
            );

            throw new ResourceAlreadyExistsException(
                    "Phone number is already registered"
            );
        }
    }


    private void validatePassword(
            String password,
            String confirmPassword
    ) {

        if (password == null || confirmPassword == null) {

            throw new BusinessException(
                    "Password and Confirm Password are required."
            );
        }

        if (!password.equals(confirmPassword)) {

            log.warn(
                    "Registration rejected | reason=PASSWORD_MISMATCH"
            );

            throw new BusinessException(
                    "Password and Confirm Password do not match."
            );
        }
    }


    // ---------------------------------------------------------
    // Normalization
    // ---------------------------------------------------------

    private String normalizeEmail(String email) {

        return email.trim().toLowerCase(Locale.ROOT);
    }


    private String normalizePhoneNumber(String phoneNumber) {

        return phoneNumber.trim();
    }
}

