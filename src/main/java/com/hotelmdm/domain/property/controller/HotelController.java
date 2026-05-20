package com.hotelmdm.domain.property.controller;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.chain.repository.HotelBrandRepository;
import com.hotelmdm.domain.property.model.Hotel;
import com.hotelmdm.domain.property.repository.AmenityRepository;
import com.hotelmdm.domain.property.service.HotelService;
import com.hotelmdm.domain.property.service.RoomService;
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
@RequestMapping("/property/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final RoomService roomService;
    private final AmenityRepository amenityRepository;
    private final HotelBrandRepository brandRepository;
    private final AuditService auditService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("hotels", hotelService.findAll());
        return "property/hotels/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Hotel hotel = hotelService.findByIdWithAmenities(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + id));
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", roomService.findByHotelId(id));
        model.addAttribute("auditHistory", auditService.getHistoryFor("HOTEL", id));
        model.addAttribute("qualityIssues", hotelService.validateQuality(hotel));
        return "property/hotels/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String newHotel(Model model) {
        model.addAttribute("hotel", new Hotel());
        model.addAttribute("allAmenities", amenityRepository.findAllByOrderByNameAsc());
        model.addAttribute("allBrands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("pageTitle", "New Hotel");
        return "property/hotels/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String createHotel(@Valid @ModelAttribute("hotel") Hotel hotel,
                              BindingResult result,
                              @RequestParam(required = false) List<Long> amenityIds,
                              @RequestParam(required = false) Long brandId,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("allAmenities", amenityRepository.findAllByOrderByNameAsc());
            model.addAttribute("allBrands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("pageTitle", "New Hotel");
            return "property/hotels/form";
        }
        List<ValidationResult> errors = hotelService.validateQuality(hotel).stream()
                .filter(ValidationResult::isError).toList();
        if (!errors.isEmpty()) {
            model.addAttribute("qualityErrors", errors);
            model.addAttribute("allAmenities", amenityRepository.findAllByOrderByNameAsc());
            model.addAttribute("allBrands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("pageTitle", "New Hotel");
            return "property/hotels/form";
        }
        Hotel saved = hotelService.save(hotel, amenityIds, brandId, auth.getName());
        ra.addFlashAttribute("successMessage", "Hotel '" + saved.getName() + "' created as DRAFT.");
        return "redirect:/property/hotels/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String editHotel(@PathVariable Long id, Model model) {
        Hotel hotel = hotelService.findByIdWithAmenities(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        model.addAttribute("hotel", hotel);
        model.addAttribute("allAmenities", amenityRepository.findAllByOrderByNameAsc());
        model.addAttribute("allBrands", brandRepository.findAllByOrderByNameAsc());
        model.addAttribute("selectedAmenityIds", hotel.getAmenities().stream()
                .map(a -> a.getId()).toList());
        model.addAttribute("selectedBrandId", hotel.getBrand() != null ? hotel.getBrand().getId() : null);
        model.addAttribute("pageTitle", "Edit Hotel");
        return "property/hotels/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String updateHotel(@PathVariable Long id,
                              @Valid @ModelAttribute("hotel") Hotel hotel,
                              BindingResult result,
                              @RequestParam(required = false) List<Long> amenityIds,
                              @RequestParam(required = false) Long brandId,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        Hotel existing = hotelService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        if (!existing.isEditable()) {
            ra.addFlashAttribute("errorMessage", "Only DRAFT or REJECTED hotels can be edited.");
            return "redirect:/property/hotels/" + id;
        }
        if (result.hasErrors()) {
            model.addAttribute("allAmenities", amenityRepository.findAllByOrderByNameAsc());
            model.addAttribute("allBrands", brandRepository.findAllByOrderByNameAsc());
            model.addAttribute("pageTitle", "Edit Hotel");
            return "property/hotels/form";
        }
        hotel.setId(id);
        hotel.setWorkflowStatus(existing.getWorkflowStatus());
        hotelService.save(hotel, amenityIds, brandId, auth.getName());
        ra.addFlashAttribute("successMessage", "Hotel updated.");
        return "redirect:/property/hotels/" + id;
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String submitForApproval(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        hotelService.submitForApproval(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Hotel submitted for approval.");
        return "redirect:/property/hotels/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteHotel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        hotelService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Hotel deleted.");
        return "redirect:/property/hotels";
    }
}
