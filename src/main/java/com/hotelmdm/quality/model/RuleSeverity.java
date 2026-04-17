package com.hotelmdm.quality.model;

public enum RuleSeverity {
    ERROR("Error"),
    WARNING("Warning"),
    INFO("Info");

    private final String label;

    RuleSeverity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
