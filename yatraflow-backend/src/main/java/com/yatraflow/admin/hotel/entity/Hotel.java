package com.yatraflow.admin.hotel.entity;

import com.yatraflow.admin.hotel.enums.HotelAmenity;
import com.yatraflow.admin.hotel.enums.HotelCategory;
import com.yatraflow.admin.hotel.enums.HotelStatus;
import com.yatraflow.admin.location.entity.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "hotels",
        indexes = {
                @Index(name = "idx_hotel_location", columnList = "location_id"),
                @Index(name = "idx_hotel_status", columnList = "status"),
                @Index(name = "idx_hotel_name", columnList = "name"),
                @Index(name = "idx_hotel_category", columnList = "category")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String hotelCode;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HotelCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private HotelStatus status = HotelStatus.UNDER_REVIEW;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 100)
    private String postalCode;

    @Column(length = 100)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 255)
    private String websiteUrl;

    @Min(1)
    @Max(5)
    private Integer starRating;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    @Builder.Default
    private LocalTime checkInTime = LocalTime.of(14, 0);

    @Column(nullable = false)
    @Builder.Default
    private LocalTime checkOutTime = LocalTime.of(11, 0);

    @ElementCollection
    @CollectionTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "amenity", nullable = false, length = 50)
    @Builder.Default
    private Set<HotelAmenity> amenities = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<HotelRoomType> roomTypes = new ArrayList<>();


}
