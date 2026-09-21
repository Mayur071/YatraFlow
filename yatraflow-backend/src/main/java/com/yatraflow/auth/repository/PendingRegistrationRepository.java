package com.yatraflow.auth.repository;

import com.yatraflow.auth.entity.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    Optional<PendingRegistration> findByEmail(String email);

    boolean  existsByEmail(String email);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
