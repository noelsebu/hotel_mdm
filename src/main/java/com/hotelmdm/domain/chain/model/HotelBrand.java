package com.hotelmdm.domain.chain.model;

import com.hotelmdm.common.GovernedEntity;
import com.hotelmdm.domain.property.model.Hotel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel_brands")
@Getter
@Setter
@NoArgsConstructor
public class HotelBrand extends GovernedEntity {

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Brand code is required")
    @Size(min = 2, max = 10)
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull(message = "Brand tier is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandTier tier;

    @NotNull(message = "Brand segment is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandSegment segment;

    @Column(length = 1000)
    private String description;

    @Column
    private Integer minStarRating;

    @Column
    private Integer maxStarRating;

    @Column(length = 7)
    private String colorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chain_id", nullable = false)
    private HotelChain chain;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BrandStandard> standards = new ArrayList<>();

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Hotel> properties = new ArrayList<>();

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
