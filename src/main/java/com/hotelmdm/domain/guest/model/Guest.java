package com.hotelmdm.domain.guest.model;

import com.hotelmdm.common.GovernedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guests")
@Getter
@Setter
@NoArgsConstructor
public class Guest extends GovernedEntity {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String nationality;

    @Column(unique = true)
    private String passportNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoyaltyTier loyaltyTier = LoyaltyTier.BRONZE;

    private int loyaltyPoints = 0;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GuestPreference> preferences = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /** Recalculates the loyalty tier based on current points */
    public void recalculateTier() {
        this.loyaltyTier = LoyaltyTier.forPoints(this.loyaltyPoints);
    }

    @Override
    public String toString() {
        return getFullName() + " <" + email + ">";
    }
}
