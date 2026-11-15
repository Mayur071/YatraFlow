package com.yatraflow.oauth2.entity;

import com.yatraflow.oauth2.enums.AuthProvider;
import com.yatraflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name ="oauth2_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_oauth_provider_user",
                        columnNames = {"provider","provider_user_id"}
                )
        },
        indexes = {

                @Index(
                        name = "idx_oauth_user_id",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuth2Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_oauth_account_user")
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(
            name = "provider_user_id",
            nullable = false,
            length = 255
    )
    private String providerUserId;

    @Column(name = "provider_email", length = 150)
    private String providerEmail;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

}
