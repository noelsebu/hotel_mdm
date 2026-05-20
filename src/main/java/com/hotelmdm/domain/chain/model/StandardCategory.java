package com.hotelmdm.domain.chain.model;

public enum StandardCategory {
    DESIGN("Design & Interiors"),
    SERVICE("Guest Service"),
    FOOD_BEVERAGE("Food & Beverage"),
    TECHNOLOGY("Technology"),
    SUSTAINABILITY("Sustainability"),
    SAFETY("Safety & Security");

    private final String label;

    StandardCategory(String label) { this.label = label; }

    public String getLabel() { return label; }
}
