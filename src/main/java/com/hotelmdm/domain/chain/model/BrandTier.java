package com.hotelmdm.domain.chain.model;

public enum BrandTier {
    LUXURY("Luxury"),
    UPPER_UPSCALE("Upper Upscale"),
    UPSCALE("Upscale"),
    MIDSCALE("Midscale"),
    ECONOMY("Economy");

    private final String label;

    BrandTier(String label) { this.label = label; }

    public String getLabel() { return label; }
}
