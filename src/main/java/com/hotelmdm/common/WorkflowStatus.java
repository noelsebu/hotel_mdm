package com.hotelmdm.common;

public enum WorkflowStatus {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String label;

    WorkflowStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
