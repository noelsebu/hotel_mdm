package com.hotelmdm.domain.vendor.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_contacts")
@Getter
@Setter
@NoArgsConstructor
public class SupplierContact extends BaseEntity {

    @NotBlank(message = "Contact name is required")
    @Column(nullable = false)
    private String name;

    private String title;

    @Email
    private String email;

    private String phone;

    private boolean primaryContact = false;

    @Column(length = 300)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
}
