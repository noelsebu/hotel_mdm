package com.hotelmdm.domain.vendor.model;

public enum SupplierCategory {
    FOOD_BEVERAGE("Food & Beverage"),
    LINEN_LAUNDRY("Linen & Laundry"),
    TECHNOLOGY("Technology & IT"),
    FURNITURE("Furniture & Fixtures"),
    CLEANING("Cleaning & Housekeeping"),
    MAINTENANCE("Maintenance & Repairs"),
    SECURITY("Security Services"),
    TRANSPORT("Transportation"),
    ENTERTAINMENT("Entertainment"),
    MARKETING("Marketing & Media"),
    CONSULTING("Consulting"),
    OTHER("Other");

    private final String label;

    SupplierCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
