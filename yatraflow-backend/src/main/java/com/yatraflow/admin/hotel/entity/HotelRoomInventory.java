package com.yatraflow.admin.hotel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "hotel_room_inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_room_type_inventory_date",
                        columnNames = {"room_type_id", "inventory_date"}
                )
        },
        indexes = {
                @Index(name = "idx_inventory_date", columnList = "inventory_date"),
                @Index(name = "idx_inventory_room_date", columnList = "room_type_id, inventory_date")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private HotelRoomType roomType;

    @Column(nullable = false)
    private LocalDate inventoryDate;

    @Min(0)
    @Column(nullable = false)
    private Integer totalRooms;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer bookedRooms = 0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer reservedRooms = 0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer blockedRooms = 0;

    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerNight;

    @Version
    private Long version;

    public int getAvailableRooms() {
        return totalRooms - bookedRooms - reservedRooms - blockedRooms;
    }
}
