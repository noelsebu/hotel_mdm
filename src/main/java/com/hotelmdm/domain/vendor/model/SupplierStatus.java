package com.hotelmdm.domain.vendor.model;

public enum SupplierStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    UNDER_REVIEW("Under Review"),
    BLACKLISTED("Blacklisted");

    private final String label;

    SupplierStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
