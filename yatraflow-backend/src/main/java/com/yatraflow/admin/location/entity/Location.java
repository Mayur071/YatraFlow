package com.yatraflow.admin.location.entity;

import com.yatraflow.admin.location.enums.LocationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(

        name = "locations" ,
        indexes = {

                @Index(name = "idx_location_name", columnList = "name"),
                @Index(name = "idx_location_type", columnList = "type"),
                @Index(name = "idx_location_state", columnList = "state"),
                @Index(name = "idx_location_active", columnList = "active"),
                @Index(name = "idx_location_parent", columnList = "parent_location_id")

        }
)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 200)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private LocationType type;

    @Column(length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(length = 2)
    private String countryCode;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 50)
    private String timezone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_location_id")
    private Location parentLocation;

    @OneToMany(
            mappedBy = "location",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<LocationIdentifier> identifiers = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addIdentifier(LocationIdentifier identifier) {
        identifiers.add(identifier);
        identifier.setLocation(this);
    }

    public void removeIdentifier(LocationIdentifier identifier) {
        identifiers.remove(identifier);
        identifier.setLocation(null);
    }


}
