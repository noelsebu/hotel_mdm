package com.hotelmdm.domain.guest.model;

public enum PreferenceCategory {
    ROOM_TYPE("Room Type"),
    BED_TYPE("Bed Type"),
    PILLOW_TYPE("Pillow Type"),
    FLOOR("Floor Preference"),
    FOOD_DIETARY("Food & Dietary"),
    BEVERAGE("Beverage"),
    NEWSPAPER("Newspaper/Magazine"),
    AMENITY("Amenity Preference"),
    TEMPERATURE("Room Temperature"),
    COMMUNICATION("Communication Method"),
    ACCESSIBILITY("Accessibility Need"),
    OTHER("Other");

    private final String label;

    PreferenceCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
