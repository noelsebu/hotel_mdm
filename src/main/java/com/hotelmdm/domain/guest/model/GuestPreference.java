package com.hotelmdm.domain.guest.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guest_preferences")
@Getter
@Setter
@NoArgsConstructor
public class GuestPreference extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferenceCategory category;

    @NotBlank
    @Column(name = "preference_value", nullable = false)
    private String value;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    public GuestPreference(PreferenceCategory category, String value, Guest guest) {
        this.category = category;
        this.value = value;
        this.guest = guest;
    }
}
