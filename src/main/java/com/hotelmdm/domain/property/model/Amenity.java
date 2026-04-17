package com.hotelmdm.domain.property.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
public class Amenity extends BaseEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AmenityCategory category;

    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    private Set<Hotel> hotels = new HashSet<>();

    @Override
    public String toString() {
        return name;
    }
}
