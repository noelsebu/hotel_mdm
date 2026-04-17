package com.hotelmdm.domain.vendor.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
public class Contract extends BaseEntity {

    @NotBlank(message = "Contract number is required")
    @Column(nullable = false, unique = true)
    private String contractNumber;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @DecimalMin("0.0")
    @Column(precision = 15, scale = 2)
    private BigDecimal totalValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal annualValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(length = 500)
    private String terms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }
}
