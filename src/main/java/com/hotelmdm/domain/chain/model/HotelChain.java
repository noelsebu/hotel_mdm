package com.hotelmdm.domain.chain.model;

import com.hotelmdm.common.GovernedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel_chains")
@Getter
@Setter
@NoArgsConstructor
public class HotelChain extends GovernedEntity {

    @NotBlank(message = "Chain name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @Column
    private String formerName;

    @NotBlank(message = "Chain code is required")
    @Size(min = 2, max = 10)
    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private Integer foundedYear;

    @Column
    private String headquarters;

    @Column
    private String country;

    @Column
    private String website;

    @Column
    private String ceoName;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "chain", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HotelBrand> brands = new ArrayList<>();

    public int getTotalBrands() {
        return brands.size();
    }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
