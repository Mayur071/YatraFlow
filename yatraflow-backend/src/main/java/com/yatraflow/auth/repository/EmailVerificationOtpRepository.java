package com.yatraflow.auth.repository;

import com.yatraflow.auth.entity.EmailVerificationOtp;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, Long> {


    // Find OTP using PendingRegistration ID
    Optional<EmailVerificationOtp> findByPendingRegistration_Id(Long pendingregistrationId);

    // Check whether an OTP record already exist
    boolean existsByPendingRegistration_Id(Long pendingRegistrationId);



    // Fetch OTP with a database write lock.
    // Prevents concurrent verification/resend operations
    // from modifying the same OTP record simultaneously.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT otp
        FROM EmailVerificationOtp otp
        WHERE otp.pendingRegistration.id = :pendingRegistrationId
        """)
    Optional<EmailVerificationOtp> findByPendingRegistrationIdForUpdate(
            @Param("pendingRegistrationId") Long pendingRegistrationId
    );


    // Delete OTP associated with a pending registration

    void deleteByPendingRegistration_Id(
            Long pendingRegistrationId
    );

}
