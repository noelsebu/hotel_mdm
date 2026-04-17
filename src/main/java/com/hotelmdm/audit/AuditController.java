package com.hotelmdm.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public String history(@RequestParam(required = false) String entityType,
                          @RequestParam(required = false) String performedBy,
                          Model model) {
        List<AuditEntry> entries;

        if (entityType != null && !entityType.isBlank()) {
            entries = auditService.getEntriesByEntityType(entityType.toUpperCase());
            model.addAttribute("selectedEntityType", entityType.toUpperCase());
        } else {
            entries = auditService.getAllEntries();
        }

        model.addAttribute("entries", entries);
        model.addAttribute("entityTypes", List.of("HOTEL", "ROOM", "AMENITY", "GUEST", "SUPPLIER", "CONTRACT"));
        return "audit/history";
    }
}
