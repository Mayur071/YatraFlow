package com.yatraflow.admin.hotel.entity;

import com.yatraflow.admin.hotel.enums.RoomTypeCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "hotel_room_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hotel_room_type_code",
                        columnNames = {"room_type_id", "inventory_date"}
                )
        },
        indexes = {
                @Index(name = "idx_room_type_hotel", columnList = "hotel_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String roomTypeCode;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomTypeCategory category;

    @Min(1)
    @Column(nullable = false)
    private Integer maxOccupancy;

    @Min(1)
    @Column(nullable = false)
    private Integer bedCount;

    @Column(length = 100)
    private String bedDescription;

    @Min(0)
    @Column(nullable = false)
    private Integer totalRooms;

    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePricePerNight;

    @Column(nullable = false)
    @Builder.Default
    private Boolean breakfastIncluded = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean refundable = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany(
            mappedBy = "roomType",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private List<HotelRoomInventory> inventory = new ArrayList<>();
}
