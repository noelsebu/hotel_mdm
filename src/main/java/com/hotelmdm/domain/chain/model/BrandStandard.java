package com.hotelmdm.domain.chain.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "brand_standards")
@Getter
@Setter
@NoArgsConstructor
public class BrandStandard extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StandardCategory category;

    @Column(nullable = false)
    private boolean mandatory = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private HotelBrand brand;
}
