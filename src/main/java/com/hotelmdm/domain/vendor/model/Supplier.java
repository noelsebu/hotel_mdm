package com.hotelmdm.domain.vendor.model;

import com.hotelmdm.common.GovernedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
public class Supplier extends GovernedEntity {

    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Supplier code is required")
    @Size(min = 2, max = 15)
    @Column(nullable = false, unique = true)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status = SupplierStatus.ACTIVE;

    private String address;
    private String city;
    private String country;

    @Email
    private String email;

    private String phone;
    private String website;

    /** 1–5 star rating */
    @Min(1) @Max(5)
    private int rating = 3;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SupplierContact> contacts = new ArrayList<>();

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
