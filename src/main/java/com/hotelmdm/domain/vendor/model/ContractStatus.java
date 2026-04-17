package com.hotelmdm.domain.vendor.model;

public enum ContractStatus {
    DRAFT("Draft"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    TERMINATED("Terminated"),
    UNDER_NEGOTIATION("Under Negotiation");

    private final String label;

    ContractStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
