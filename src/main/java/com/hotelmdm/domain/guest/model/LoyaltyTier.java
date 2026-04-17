package com.hotelmdm.domain.guest.model;

public enum LoyaltyTier {
    BRONZE("Bronze", 0, 4999),
    SILVER("Silver", 5000, 14999),
    GOLD("Gold", 15000, 49999),
    PLATINUM("Platinum", 50000, Integer.MAX_VALUE);

    private final String label;
    private final int minPoints;
    private final int maxPoints;

    LoyaltyTier(String label, int minPoints, int maxPoints) {
        this.label = label;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    public String getLabel() { return label; }
    public int getMinPoints() { return minPoints; }
    public int getMaxPoints() { return maxPoints; }

    public static LoyaltyTier forPoints(int points) {
        for (LoyaltyTier tier : values()) {
            if (points >= tier.minPoints && points <= tier.maxPoints) return tier;
        }
        return BRONZE;
    }
}
