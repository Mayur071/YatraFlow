
package com.yatraflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "email_verification_otps",
        indexes = {
                @Index(
                        name = "idx_email_otp_pending_registration",
                        columnList = "pending_registration_id",
                        unique = true
                ),
                @Index(
                        name = "idx_email_otp_expiry",
                        columnList = "expires_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "pending_registration_id",
            nullable = false,
            unique = true
    )
    private PendingRegistration pendingRegistration;

    @Column(
            name = "otp_hash",
            nullable = false,
            length = 128
    )
    private String otpHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "last_sent_at",
            nullable = false
    )
    private LocalDateTime lastSentAt;

    @Column(
            name = "resend_window_started_at",
            nullable = false
    )
    private LocalDateTime resendWindowStartedAt;

    @Builder.Default
    @Column(
            name = "attempt_count",
            nullable = false
    )
    private int attemptCount = 0;

    @Builder.Default
    @Column(
            name = "resend_count",
            nullable = false
    )
    private int resendCount = 0;

    @Builder.Default
    @Column(
            name = "used",
            nullable = false
    )
    private boolean used = false;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
