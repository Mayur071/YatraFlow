package com.yatraflow.admin.entity;


import com.yatraflow.admin.enums.LocationCodeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "location_identifiers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_location_identifier_type_code",
                        columnNames = {"code_type", "code"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_location_identifier_location",
                        columnList = "location_id"
                ),
                @Index(
                        name = "idx_location_identifier_code",
                        columnList = "code"
                )
        }
)
public class LocationIdentifier {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", nullable = false, length = 40)
    private LocationCodeType codeType;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
