package com.yatraflow.auth.repository;

import com.yatraflow.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByTokenHashAndUsedFalse(String tokenHash);

    void deleteByPendingRegistrationId(Long pendingRegistrationId);

    Optional<EmailVerificationToken> findByPendingRegistrationId(Long pendingRegistrationId);


}
