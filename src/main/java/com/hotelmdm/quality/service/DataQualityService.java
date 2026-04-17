package com.hotelmdm.quality.service;

import com.hotelmdm.quality.model.DataQualityRule;
import com.hotelmdm.quality.model.RuleSeverity;
import com.hotelmdm.quality.model.ValidationResult;
import com.hotelmdm.quality.repository.DataQualityRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataQualityService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[+]?[0-9\\s\\-().]{7,20}$";

    private final DataQualityRuleRepository ruleRepository;

    /**
     * Validates a map of field values against all active rules for the given entity type.
     *
     * @param entityType e.g. "HOTEL", "GUEST", "SUPPLIER"
     * @param fields     map of fieldName → value (as String)
     * @return list of validation failures (empty = all passed)
     */
    public List<ValidationResult> validate(String entityType, Map<String, Object> fields) {
        List<DataQualityRule> rules = ruleRepository.findByEntityTypeAndActiveTrue(entityType);
        List<ValidationResult> failures = new ArrayList<>();

        for (DataQualityRule rule : rules) {
            Object rawValue = fields.get(rule.getFieldName());
            String value = rawValue != null ? rawValue.toString().trim() : null;
            ValidationResult failure = evaluate(rule, value);
            if (failure != null) {
                failures.add(failure);
            }
        }
        return failures;
    }

    public List<DataQualityRule> findAll() {
        return ruleRepository.findAllByOrderByEntityTypeAscFieldNameAsc();
    }

    public DataQualityRule save(DataQualityRule rule) {
        return ruleRepository.save(rule);
    }

    public java.util.Optional<DataQualityRule> findById(Long id) {
        return ruleRepository.findById(id);
    }

    public void delete(Long id) {
        ruleRepository.deleteById(id);
    }

    private ValidationResult evaluate(DataQualityRule rule, String value) {
        boolean failed = switch (rule.getRuleType()) {
            case NOT_NULL -> value == null || value.isBlank();
            case MIN_LENGTH -> {
                if (value == null || value.isBlank()) yield false; // NOT_NULL handles presence
                int min = Integer.parseInt(rule.getRuleValue());
                yield value.length() < min;
            }
            case MAX_LENGTH -> {
                if (value == null) yield false;
                int max = Integer.parseInt(rule.getRuleValue());
                yield value.length() > max;
            }
            case REGEX -> value == null || value.isBlank() || !value.matches(rule.getRuleValue());
            case MIN_VALUE -> {
                if (value == null || value.isBlank()) yield false;
                try {
                    yield Double.parseDouble(value) < Double.parseDouble(rule.getRuleValue());
                } catch (NumberFormatException e) { yield true; }
            }
            case MAX_VALUE -> {
                if (value == null || value.isBlank()) yield false;
                try {
                    yield Double.parseDouble(value) > Double.parseDouble(rule.getRuleValue());
                } catch (NumberFormatException e) { yield true; }
            }
            case EMAIL_FORMAT -> value != null && !value.isBlank() && !value.matches(EMAIL_REGEX);
            case PHONE_FORMAT -> value != null && !value.isBlank() && !value.matches(PHONE_REGEX);
        };

        return failed
                ? new ValidationResult(rule.getName(), rule.getFieldName(),
                        rule.getMessage(), rule.getSeverity())
                : null;
    }
}
