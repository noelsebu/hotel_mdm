package com.hotelmdm.domain.chain.model;

public enum BrandSegment {
    BUSINESS("Business"),
    LEISURE("Leisure"),
    BOUTIQUE("Boutique"),
    EXTENDED_STAY("Extended Stay"),
    RESORT("Resort"),
    MIXED("Mixed Business & Leisure");

    private final String label;

    BrandSegment(String label) { this.label = label; }

    public String getLabel() { return label; }
}
