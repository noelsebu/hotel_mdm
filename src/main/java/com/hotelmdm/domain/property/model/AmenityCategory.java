package com.hotelmdm.domain.property.model;

public enum AmenityCategory {
    SPA("Spa & Wellness"),
    POOL("Swimming Pool"),
    GYM("Fitness Center"),
    RESTAURANT("Restaurant & Bar"),
    BUSINESS("Business Center"),
    CONFERENCE("Conference Facilities"),
    PARKING("Parking"),
    TRANSPORT("Transportation"),
    KIDS("Kids Facilities"),
    ENTERTAINMENT("Entertainment"),
    OTHER("Other");

    private final String label;

    AmenityCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
