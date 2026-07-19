package com.yatraflow.role.entity;

import com.yatraflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name="roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_name",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "name",
            nullable = false,
            unique = true,
            length = 30
    )
    private RoleName name;


    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();




}
