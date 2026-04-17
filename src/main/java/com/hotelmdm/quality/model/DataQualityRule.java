package com.hotelmdm.quality.model;

import com.hotelmdm.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "data_quality_rules")
@Getter
@Setter
@NoArgsConstructor
public class DataQualityRule extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    /** The entity this rule applies to: HOTEL, GUEST, SUPPLIER, ROOM */
    @NotBlank
    @Column(nullable = false)
    private String entityType;

    /** The field name to validate (must match the key used in the field map) */
    @NotBlank
    @Column(nullable = false)
    private String fieldName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    /**
     * The constraint value interpreted based on ruleType:
     * - NOT_NULL: ignored
     * - MIN_LENGTH / MAX_LENGTH / MIN_VALUE / MAX_VALUE: numeric string
     * - REGEX / EMAIL_FORMAT / PHONE_FORMAT: regex pattern string
     */
    @Column(length = 500)
    private String ruleValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleSeverity severity = RuleSeverity.ERROR;

    /** Custom message shown when the rule fails */
    @NotBlank
    @Column(nullable = false)
    private String message;

    private boolean active = true;
}
