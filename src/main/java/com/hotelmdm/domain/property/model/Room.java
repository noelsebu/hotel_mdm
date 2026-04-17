package com.hotelmdm.domain.property.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "room_number"})
})
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseEntity {

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Min(0) @Max(100)
    private int floor;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Min(1) @Max(20)
    private int maxOccupancy = 2;

    private boolean available = true;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + (roomType != null ? roomType.getLabel() : "") + ")";
    }
}
