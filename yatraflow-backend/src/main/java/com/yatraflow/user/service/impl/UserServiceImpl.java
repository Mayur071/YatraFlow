package com.yatraflow.user.service.impl;

import com.yatraflow.exception.BusinessException;
import com.yatraflow.exception.ResourceNotFoundException;
import com.yatraflow.exception.UnauthorizedException;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.repository.UserRepository;
import com.yatraflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {

        log.info("Creating new user with email : {}", user.getEmail());

        return save(user);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public User getUserById(Long id) {

        log.info("Fetching user with id : {}", id);
        return userRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("User not found with id : {}", id);

                    return new ResourceNotFoundException("User not found with id : " + id);
                }
        );
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {

        log.info("Fetching user with email : {}", email);
        return userRepository.findByEmail(email).orElseThrow(

                () -> {
                    log.warn("User not found with email : {}",email);

                    return new UnauthorizedException("Invalid email or password.");
                }
        );
    }

    @Override
    @Transactional
    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            log.warn("Unauthenticated user attempted to access current user details.");

            throw new UnauthorizedException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User authenticatedUser)) {
            throw new UnauthorizedException("Invalid authentication principal.");
        }

        log.debug("Fetching current user with ID: {}", authenticatedUser.getId());

        return userRepository.findByEmailWithRoles(authenticatedUser.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Authenticated user not found.")
                );
    }

    @Override
    public User save(User user) {

        try{
            log.info("Saving user : {}",user.getEmail());

            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex){

            log.error("Database constraint violation while saving user.",ex);

            throw new BusinessException("Unable to constraint due to database constraint violation.",ex);


        } catch (Exception ex){
            log.error("Unexpected error while saving user.", ex);

            throw new BusinessException("Unable to save user.");
        }
    }
}
