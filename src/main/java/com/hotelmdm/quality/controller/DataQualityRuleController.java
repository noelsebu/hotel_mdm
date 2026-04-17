package com.hotelmdm.quality.controller;

import com.hotelmdm.quality.model.DataQualityRule;
import com.hotelmdm.quality.model.RuleSeverity;
import com.hotelmdm.quality.model.RuleType;
import com.hotelmdm.quality.service.DataQualityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/quality/rules")
@RequiredArgsConstructor
public class DataQualityRuleController {

    private final DataQualityService qualityService;

    private static final List<String> ENTITY_TYPES = List.of("HOTEL", "ROOM", "AMENITY", "GUEST", "SUPPLIER", "CONTRACT");

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rules", qualityService.findAll());
        return "quality/rules/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String newRule(Model model) {
        model.addAttribute("rule", new DataQualityRule());
        model.addAttribute("entityTypes", ENTITY_TYPES);
        model.addAttribute("ruleTypes", RuleType.values());
        model.addAttribute("severities", RuleSeverity.values());
        return "quality/rules/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String createRule(@Valid @ModelAttribute("rule") DataQualityRule rule,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("entityTypes", ENTITY_TYPES);
            model.addAttribute("ruleTypes", RuleType.values());
            model.addAttribute("severities", RuleSeverity.values());
            return "quality/rules/form";
        }
        qualityService.save(rule);
        ra.addFlashAttribute("successMessage", "Data quality rule created.");
        return "redirect:/quality/rules";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String editRule(@PathVariable Long id, Model model) {
        DataQualityRule rule = qualityService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));
        model.addAttribute("rule", rule);
        model.addAttribute("entityTypes", ENTITY_TYPES);
        model.addAttribute("ruleTypes", RuleType.values());
        model.addAttribute("severities", RuleSeverity.values());
        return "quality/rules/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String updateRule(@PathVariable Long id,
                             @Valid @ModelAttribute("rule") DataQualityRule rule,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("entityTypes", ENTITY_TYPES);
            model.addAttribute("ruleTypes", RuleType.values());
            model.addAttribute("severities", RuleSeverity.values());
            return "quality/rules/form";
        }
        rule.setId(id);
        qualityService.save(rule);
        ra.addFlashAttribute("successMessage", "Rule updated.");
        return "redirect:/quality/rules";
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String toggleActive(@PathVariable Long id, RedirectAttributes ra) {
        qualityService.findById(id).ifPresent(rule -> {
            rule.setActive(!rule.isActive());
            qualityService.save(rule);
        });
        ra.addFlashAttribute("successMessage", "Rule status toggled.");
        return "redirect:/quality/rules";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRule(@PathVariable Long id, RedirectAttributes ra) {
        qualityService.delete(id);
        ra.addFlashAttribute("successMessage", "Rule deleted.");
        return "redirect:/quality/rules";
    }
}
