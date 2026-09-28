package com.yatraflow.auth.services.register;

import com.yatraflow.auth.entity.EmailVerificationToken;
import com.yatraflow.auth.entity.PendingRegistration;
import com.yatraflow.auth.mapper.AuthMapper;
import com.yatraflow.auth.notification.EmailService;
import com.yatraflow.auth.services.email.EmailVerificationTokenService;
import com.yatraflow.auth.services.email.PendingRegistrationService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService  {


    private static final String VERIFICATION_POINT = "/verify-email";

    private final EmailVerificationTokenService tokenService;

    private final EmailService emailService;

    private final AuthMapper authMapper;

    private final RoleService roleService;

    private final UserService userService;

    private final TemplateEngine templateEngine;

    private final PendingRegistrationService pendingRegistrationService;



    @Value("${app.email-verification.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.email-verification.expiry-minutes:15}")
    private int verificationExpiryMinutes;

    // =========================================================
    // CREATE + SEND VERIFICATION EMAIL
    // =========================================================

    @Override
    public void createAndSendVerificationEmail(PendingRegistration pendingRegistration) {

        if(pendingRegistration == null){
            throw new BusinessException("Pending Registration is required");
        }

        String email = pendingRegistration.getEmail();

        log.info("Creating email verification request | email={}", email);

        // -----------------------------------------------------
        // 1. Generate secure verification token
        // -----------------------------------------------------

        String rawToken = tokenService.createVerificationToken(pendingRegistration);

        // -----------------------------------------------------
        // 2. Build verification URL
        // -----------------------------------------------------

        String verificationUrl = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path(VERIFICATION_POINT)
                .queryParam("token",rawToken)
                .toUriString();

        // -----------------------------------------------------
        // 3. Prepare Thymeleaf context
        // -----------------------------------------------------


        Context context = new Context();

        context.setVariable("firstName",pendingRegistration.getFirstName());
        context.setVariable("verificationUrl",verificationUrl);
        context.setVariable("expiryMinutes",verificationExpiryMinutes);

        // -----------------------------------------------------
        // 4. Render HTML template
        // -----------------------------------------------------

        final String htmlContent;

        try {

            htmlContent =
                    templateEngine.process(
                            "email/email-verification",
                            context
                    );

        } catch (
                TemplateProcessingException e
        ) {

            log.error(
                    "Failed to render verification email template | email={}",
                    email,
                    e
            );

            throw new BusinessException(
                    "Unable to prepare verification email. Please try again later."
            );
        }

        // -----------------------------------------------------
        // 5. Send HTML email
        // -----------------------------------------------------

        emailService.sendEmail(
                email,
                "Verify your YatraFlow account",
                htmlContent
        );

        log.info(
                "Verification email initiated successfully | email={}",
                email
        );
    }

    // =========================================================
    // VERIFY EMAIL
    // =========================================================

    @Override
    public void verifyEmail(String rawToken) {

        log.info("Email verification request received.");

        // -----------------------------------------------------
        // 1. Validate input
        // -----------------------------------------------------

        if (rawToken == null || rawToken.isBlank()) {

            throw new BusinessException(
                    "Verification token is required."
            );
        }

        // -----------------------------------------------------
        // 2. Validate token
        //
        // TokenService handles:
        // - SHA-256 hashing
        // - token lookup
        // - used-token validation
        // - expiry validation
        // -----------------------------------------------------

        EmailVerificationToken verificationToken =
                tokenService.getValidateToken(rawToken);

        // -----------------------------------------------------
        // 3. Get pending registration
        // -----------------------------------------------------

        PendingRegistration pendingRegistration =
                verificationToken.getPendingRegistration();

        if (pendingRegistration == null) {

            log.error(
                    "No pending registration associated with verification token."
            );

            throw new BusinessException(
                    "Pending registration not found."
            );
        }

        String email = pendingRegistration.getEmail();

        // -----------------------------------------------------
        // 4. Defensive duplicate check
        // -----------------------------------------------------

        if (userService.existsByEmail(email)) {

            log.warn(
                    "Verification rejected. User already exists | email={}",
                    email
            );

            throw new ResourceAlreadyExistsException(
                    "Email already registered."
            );
        }

        // -----------------------------------------------------
        // 5. Convert PendingRegistration -> User
        // -----------------------------------------------------

        User user =
                authMapper.toUser(
                        pendingRegistration
                );

        if (user == null) {

            log.error(
                    "Failed to create User from PendingRegistration | email={}",
                    email
            );

            throw new BusinessException(
                    "Unable to complete registration."
            );
        }

        // -----------------------------------------------------
        // 6. Assign default role
        //
        // RoleService is intentionally used here because the
        // actual User is created only after email verification.
        // -----------------------------------------------------

        Role userRole =
                roleService.getRoleByName(
                        RoleName.ROLE_USER
                );

        user.getRoles().add(userRole);

        // -----------------------------------------------------
        // 7. Persist verified User
        //
        // AuthMapper sets emailVerified=true.
        // -----------------------------------------------------

        User savedUser =
                userService.createUser(user);

        // -----------------------------------------------------
        // 8. Remove verification token
        //
        // Successful verification means the token must never
        // be usable again.
        // -----------------------------------------------------

        tokenService.deleteToken(
                pendingRegistration
        );

        log.info(
                "Email verification completed successfully | email={} | userId={}",
                savedUser.getEmail(),
                savedUser.getId()
        );
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {

        log.info(
                "Resend verification request received | email={}",
                email
        );

        if (email == null || email.isBlank()) {
            throw new BusinessException(
                    "Email is required."
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase(java.util.Locale.ROOT);

        // ---------------------------------------------------------
        // 1. Check whether user is already registered
        // ---------------------------------------------------------

        if (userService.existsByEmail(normalizedEmail)) {

            log.warn(
                    "Resend verification rejected | user already registered | email={}",
                    normalizedEmail
            );

            throw new BusinessException(
                    "Email is already verified."
            );
        }

        // ---------------------------------------------------------
        // 2. Find pending registration
        // ---------------------------------------------------------

        PendingRegistration pendingRegistration =
                pendingRegistrationService.getByEmail(
                        normalizedEmail
                );

        // ---------------------------------------------------------
        // 3. Find existing verification token
        // ---------------------------------------------------------

       Optional<EmailVerificationToken> existingToken =
                tokenService.getByPendingRegistration(
                        pendingRegistration
                );

        // ---------------------------------------------------------
        // 4. If an existing token is still valid, don't issue
        //    another one.
        // ---------------------------------------------------------

        if (existingToken.isPresent()) {

            EmailVerificationToken token =
                    existingToken.get();

            if (token.getExpiresAt().isAfter(LocalDateTime.now())) {

                log.warn(
                        "Resend verification rejected | existing token still valid | email={}",
                        normalizedEmail
                );

                throw new BusinessException(
                        "Your current verification link is still valid."
                );
            }

            // Existing token has expired → remove it.
            tokenService.deleteToken(
                    pendingRegistration
            );
        }

        // ---------------------------------------------------------
        // 5. Create a fresh token and send a new verification email
        // ---------------------------------------------------------

        createAndSendVerificationEmail(
                pendingRegistration
        );

        log.info(
                "Verification email resent successfully | email={}",
                normalizedEmail
        );
    }



}


