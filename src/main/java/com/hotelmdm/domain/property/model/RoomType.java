package com.hotelmdm.domain.property.model;

public enum RoomType {
    SINGLE("Single"),
    DOUBLE("Double"),
    TWIN("Twin"),
    SUITE("Suite"),
    DELUXE("Deluxe"),
    PRESIDENTIAL("Presidential Suite");

    private final String label;

    RoomType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
