package com.hotelmdm.domain.guest.controller;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.guest.model.Guest;
import com.hotelmdm.domain.guest.model.LoyaltyTier;
import com.hotelmdm.domain.guest.model.PreferenceCategory;
import com.hotelmdm.domain.guest.service.GuestService;
import com.hotelmdm.quality.model.ValidationResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;
    private final AuditService auditService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Guest> guests = (search != null && !search.isBlank())
                ? guestService.search(search)
                : guestService.findAll();
        model.addAttribute("guests", guests);
        model.addAttribute("search", search);
        return "guest/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Guest guest = guestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        model.addAttribute("guest", guest);
        model.addAttribute("preferenceCategories", PreferenceCategory.values());
        model.addAttribute("auditHistory", auditService.getHistoryFor("GUEST", id));
        model.addAttribute("qualityIssues", guestService.validateQuality(guest));
        return "guest/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String newGuest(Model model) {
        model.addAttribute("guest", new Guest());
        model.addAttribute("tiers", LoyaltyTier.values());
        return "guest/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String createGuest(@Valid @ModelAttribute("guest") Guest guest,
                              BindingResult result,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("tiers", LoyaltyTier.values());
            return "guest/form";
        }
        List<ValidationResult> errors = guestService.validateQuality(guest).stream()
                .filter(ValidationResult::isError).toList();
        if (!errors.isEmpty()) {
            model.addAttribute("qualityErrors", errors);
            model.addAttribute("tiers", LoyaltyTier.values());
            return "guest/form";
        }
        Guest saved = guestService.save(guest, auth.getName());
        ra.addFlashAttribute("successMessage", "Guest profile created.");
        return "redirect:/guest/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String editGuest(@PathVariable Long id, Model model) {
        Guest guest = guestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        model.addAttribute("guest", guest);
        model.addAttribute("tiers", LoyaltyTier.values());
        return "guest/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String updateGuest(@PathVariable Long id,
                              @Valid @ModelAttribute("guest") Guest guest,
                              BindingResult result,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        Guest existing = guestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        if (!existing.isEditable()) {
            ra.addFlashAttribute("errorMessage", "Only DRAFT or REJECTED records can be edited.");
            return "redirect:/guest/" + id;
        }
        if (result.hasErrors()) {
            model.addAttribute("tiers", LoyaltyTier.values());
            return "guest/form";
        }
        guest.setId(id);
        guest.setWorkflowStatus(existing.getWorkflowStatus());
        guestService.save(guest, auth.getName());
        ra.addFlashAttribute("successMessage", "Guest profile updated.");
        return "redirect:/guest/" + id;
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String submitForApproval(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        guestService.submitForApproval(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Guest submitted for approval.");
        return "redirect:/guest/" + id;
    }

    @PostMapping("/{id}/preferences/add")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String addPreference(@PathVariable Long id,
                                @RequestParam PreferenceCategory category,
                                @RequestParam String value,
                                @RequestParam(required = false) String notes,
                                Authentication auth,
                                RedirectAttributes ra) {
        guestService.addPreference(id, category, value, notes, auth.getName());
        ra.addFlashAttribute("successMessage", "Preference added.");
        return "redirect:/guest/" + id;
    }

    @PostMapping("/{id}/preferences/{prefId}/remove")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String removePreference(@PathVariable Long id,
                                   @PathVariable Long prefId,
                                   Authentication auth,
                                   RedirectAttributes ra) {
        guestService.removePreference(id, prefId, auth.getName());
        ra.addFlashAttribute("successMessage", "Preference removed.");
        return "redirect:/guest/" + id;
    }

    @PostMapping("/{id}/points")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String addPoints(@PathVariable Long id,
                            @RequestParam int points,
                            Authentication auth,
                            RedirectAttributes ra) {
        guestService.addPoints(id, points, auth.getName());
        ra.addFlashAttribute("successMessage", points + " loyalty points added.");
        return "redirect:/guest/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteGuest(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        guestService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Guest profile deleted.");
        return "redirect:/guest";
    }
}
