package com.hotelmdm.quality.model;

public record ValidationResult(
        String ruleName,
        String fieldName,
        String message,
        RuleSeverity severity
) {
    public boolean isError() {
        return severity == RuleSeverity.ERROR;
    }

    public boolean isWarning() {
        return severity == RuleSeverity.WARNING;
    }
}
