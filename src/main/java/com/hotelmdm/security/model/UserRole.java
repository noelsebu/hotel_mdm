package com.hotelmdm.security.model;

public enum UserRole {
    ADMIN("Administrator"),
    DATA_MANAGER("Data Manager"),
    DATA_STEWARD("Data Steward"),
    VIEWER("Viewer");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
