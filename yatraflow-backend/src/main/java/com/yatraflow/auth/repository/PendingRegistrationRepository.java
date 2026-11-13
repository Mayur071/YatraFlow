package com.yatraflow.auth.repository;

import com.yatraflow.auth.entity.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    Optional<PendingRegistration> findByEmail(String email);

    boolean  existsByEmail(String email);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
