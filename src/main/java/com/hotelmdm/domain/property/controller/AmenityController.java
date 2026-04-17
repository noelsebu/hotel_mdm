package com.hotelmdm.domain.property.controller;

import com.hotelmdm.domain.property.model.Amenity;
import com.hotelmdm.domain.property.model.AmenityCategory;
import com.hotelmdm.domain.property.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/property/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("amenities", amenityService.findAll());
        model.addAttribute("categories", AmenityCategory.values());
        return "property/amenities/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String newAmenity(Model model) {
        model.addAttribute("amenity", new Amenity());
        model.addAttribute("categories", AmenityCategory.values());
        return "property/amenities/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String createAmenity(@Valid @ModelAttribute("amenity") Amenity amenity,
                                BindingResult result,
                                Authentication auth,
                                Model model,
                                RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", AmenityCategory.values());
            return "property/amenities/form";
        }
        amenityService.save(amenity, auth.getName());
        ra.addFlashAttribute("successMessage", "Amenity '" + amenity.getName() + "' created.");
        return "redirect:/property/amenities";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String editAmenity(@PathVariable Long id, Model model) {
        Amenity amenity = amenityService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Amenity not found"));
        model.addAttribute("amenity", amenity);
        model.addAttribute("categories", AmenityCategory.values());
        return "property/amenities/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String updateAmenity(@PathVariable Long id,
                                @Valid @ModelAttribute("amenity") Amenity amenity,
                                BindingResult result,
                                Authentication auth,
                                Model model,
                                RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", AmenityCategory.values());
            return "property/amenities/form";
        }
        amenity.setId(id);
        amenityService.save(amenity, auth.getName());
        ra.addFlashAttribute("successMessage", "Amenity updated.");
        return "redirect:/property/amenities";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteAmenity(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        amenityService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Amenity deleted.");
        return "redirect:/property/amenities";
    }
}
