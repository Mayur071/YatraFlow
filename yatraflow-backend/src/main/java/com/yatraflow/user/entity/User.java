package com.yatraflow.user.entity;

import com.yatraflow.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name",nullable = false,length = 100)
    private String fullName;

    @Column(name = "email",nullable = false,unique = true,length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled",nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",

            joinColumns = @JoinColumn(
                    name = "user_id",
                    foreignKey = @ForeignKey(name = "fk_user_roles_user")
            ),

            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    foreignKey = @ForeignKey(name = "fk_user_roles_role")
            )
    )


    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

    }


    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
