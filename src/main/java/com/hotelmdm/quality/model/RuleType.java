package com.hotelmdm.quality.model;

public enum RuleType {
    NOT_NULL("Not Null"),
    MIN_LENGTH("Minimum Length"),
    MAX_LENGTH("Maximum Length"),
    REGEX("Regex Pattern"),
    MIN_VALUE("Minimum Value"),
    MAX_VALUE("Maximum Value"),
    EMAIL_FORMAT("Email Format"),
    PHONE_FORMAT("Phone Format");

    private final String label;

    RuleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
