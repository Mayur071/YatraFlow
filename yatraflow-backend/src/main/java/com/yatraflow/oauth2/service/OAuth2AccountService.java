package com.yatraflow.oauth2.service;


import com.yatraflow.exception.ForbiddenException;
import com.yatraflow.exception.ResourceAlreadyExistsException;
import com.yatraflow.exception.ResourceNotFoundException;
import com.yatraflow.exception.UnauthorizedException;
import com.yatraflow.oauth2.entity.OAuth2Account;
import com.yatraflow.oauth2.enums.AuthProvider;
import com.yatraflow.oauth2.repository.OAuth2AccountRepository;
import com.yatraflow.role.entity.Role;
import com.yatraflow.role.entity.RoleName;
import com.yatraflow.role.repository.RoleRepository;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2AccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;

    /**
     * Finds an existing OAuth account or creates a new Yatraflow user.
     */
    @Transactional
    public User findOrCreateUser(
            AuthProvider provider,
            String providerUserId,
            String email,
            boolean emailVerified,
            String firstName,
            String lastName
    ) {

        log.info("Processing OAuth authentication. Provider: {}", provider);

        // 1. Validate provider identity
        validateProviderIdentity(provider, providerUserId);

        // 2. Check existing OAuth account
        Optional<OAuth2Account> existingAccount =
                oauth2AccountRepository.findByProviderAndProviderUserId(
                        provider,
                        providerUserId
                );

        if (existingAccount.isPresent()) {

            User existingUser = existingAccount.get().getUser();

            validateExistingUser(existingUser);

            log.info(
                    "Existing OAuth account authenticated. Provider: {}, UserId: {}",
                    provider,
                    existingUser.getId()
            );

            return existingUser;
        }

        // 3. Validate provider email
        validateEmail(email, emailVerified);

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        // 4. Prevent automatic linking with local accounts
        if (userRepository.existsByEmail(normalizedEmail)) {

            log.warn(
                    "OAuth registration rejected because an account already exists. Provider: {}",
                    provider
            );

            throw new ResourceAlreadyExistsException(
                    "An account with this email already exists. "
                            + "Please log in using your existing account."
            );
        }

        // 5. Validate profile information
        String normalizedFirstName = validateFirstName(firstName);

        String normalizedLastName =
                lastName == null ? "" : lastName.trim();

        validateProfileLength(
                normalizedFirstName,
                normalizedLastName,
                normalizedEmail
        );

        // 6. Fetch existing application role
        Role userRole = roleRepository
                .findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> {
                    log.error("ROLE_USER not found in database.");

                    return new ResourceNotFoundException(
                            "Default user role not found."
                    );
                });

        // 7. Build Yatraflow user
        User user = User.builder()
                .firstName(normalizedFirstName)
                .lastName(normalizedLastName)
                .email(normalizedEmail)
                .password(null)
                .phoneNumber(null)
                .enabled(true)
                .emailVerified(true)
                .accountLocked(false)
                .roles(new HashSet<>())
                .build();

        user.getRoles().add(userRole);

        // 8. Persist user
        User savedUser = userRepository.save(user);

        // 9. Build OAuth account mapping
        OAuth2Account oauth2Account = OAuth2Account.builder()
                .user(savedUser)
                .provider(provider)
                .providerUserId(providerUserId)
                .providerEmail(normalizedEmail)
                .build();

        oauth2AccountRepository.save(oauth2Account);

        log.info(
                "New OAuth user created successfully. Provider: {}, UserId: {}",
                provider,
                savedUser.getId()
        );

        return savedUser;
    }

    /**
     * Validates OAuth provider identity.
     */
    private void validateProviderIdentity(
            AuthProvider provider,
            String providerUserId
    ) {

        if (provider == null
                || providerUserId == null
                || providerUserId.isBlank()) {

            throw new UnauthorizedException(
                    "Invalid OAuth provider identity."
            );
        }
    }

    /**
     * Validates provider email and verification status.
     */
    private void validateEmail(
            String email,
            boolean emailVerified
    ) {

        if (email == null || email.isBlank()) {

            throw new UnauthorizedException(
                    "Email address is required for OAuth registration."
            );
        }

        if (!emailVerified) {

            log.warn("OAuth registration rejected due to unverified email.");

            throw new UnauthorizedException(
                    "Please use a verified email address."
            );
        }
    }

    /**
     * Validates first name.
     */
    private String validateFirstName(String firstName) {

        if (firstName == null || firstName.isBlank()) {

            throw new UnauthorizedException(
                    "First name is required for OAuth registration."
            );
        }

        return firstName.trim();
    }

    /**
     * Validates profile field lengths.
     */
    private void validateProfileLength(
            String firstName,
            String lastName,
            String email
    ) {

        if (firstName.length() > 50 || lastName.length() > 50) {

            throw new UnauthorizedException(
                    "Profile name exceeds the allowed length."
            );
        }

        if (email.length() > 150) {

            throw new UnauthorizedException(
                    "Email address exceeds the allowed length."
            );
        }
    }

    /**
     * Validates existing Yatraflow user status.
     */
    private void validateExistingUser(User user) {

        if (!Boolean.TRUE.equals(user.getEnabled())) {

            throw new ForbiddenException(
                    "Your account is disabled."
            );
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {

            throw new ForbiddenException(
                    "Your account is locked."
            );
        }
    }


}
