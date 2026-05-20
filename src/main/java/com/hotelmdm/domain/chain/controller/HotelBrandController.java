package com.hotelmdm.domain.chain.controller;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.chain.model.BrandSegment;
import com.hotelmdm.domain.chain.model.BrandStandard;
import com.hotelmdm.domain.chain.model.BrandTier;
import com.hotelmdm.domain.chain.model.HotelBrand;
import com.hotelmdm.domain.chain.service.HotelBrandService;
import com.hotelmdm.domain.chain.service.HotelChainService;
import com.hotelmdm.domain.property.repository.HotelRepository;
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
@RequestMapping("/brand")
@RequiredArgsConstructor
public class HotelBrandController {

    private final HotelBrandService brandService;
    private final HotelChainService chainService;
    private final HotelRepository hotelRepository;
    private final AuditService auditService;

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        HotelBrand brand = brandService.findByIdWithStandards(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + id));
        model.addAttribute("brand", brand);
        model.addAttribute("standards", brandService.findStandards(id));
        model.addAttribute("properties", hotelRepository.findByBrandIdOrderByNameAsc(id));
        model.addAttribute("mandatoryCount", brandService.countMandatoryStandards(id));
        model.addAttribute("auditHistory", auditService.getHistoryFor("BRAND", id));
        model.addAttribute("standardCategories", com.hotelmdm.domain.chain.model.StandardCategory.values());
        model.addAttribute("newStandard", new BrandStandard());
        return "brand/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String newBrand(@RequestParam(required = false) Long chainId, Model model) {
        HotelBrand brand = new HotelBrand();
        if (chainId != null) {
            chainService.findById(chainId).ifPresent(brand::setChain);
        }
        model.addAttribute("brand", brand);
        model.addAttribute("chains", chainService.findAll());
        model.addAttribute("tiers", BrandTier.values());
        model.addAttribute("segments", BrandSegment.values());
        model.addAttribute("pageTitle", "New Brand");
        return "brand/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String createBrand(@Valid @ModelAttribute("brand") HotelBrand brand,
                              BindingResult result,
                              @RequestParam Long chainId,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("chains", chainService.findAll());
            model.addAttribute("tiers", BrandTier.values());
            model.addAttribute("segments", BrandSegment.values());
            model.addAttribute("pageTitle", "New Brand");
            return "brand/form";
        }
        chainService.findById(chainId).ifPresent(brand::setChain);
        HotelBrand saved = brandService.save(brand, auth.getName());
        ra.addFlashAttribute("successMessage", "Brand '" + saved.getName() + "' created as DRAFT.");
        return "redirect:/brand/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String editBrand(@PathVariable Long id, Model model) {
        HotelBrand brand = brandService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        model.addAttribute("brand", brand);
        model.addAttribute("chains", chainService.findAll());
        model.addAttribute("tiers", BrandTier.values());
        model.addAttribute("segments", BrandSegment.values());
        model.addAttribute("pageTitle", "Edit Brand: " + brand.getName());
        return "brand/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String updateBrand(@PathVariable Long id,
                              @Valid @ModelAttribute("brand") HotelBrand brand,
                              BindingResult result,
                              @RequestParam Long chainId,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        HotelBrand existing = brandService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        if (!existing.isEditable()) {
            ra.addFlashAttribute("errorMessage", "Only DRAFT or REJECTED brands can be edited.");
            return "redirect:/brand/" + id;
        }
        if (result.hasErrors()) {
            model.addAttribute("chains", chainService.findAll());
            model.addAttribute("tiers", BrandTier.values());
            model.addAttribute("segments", BrandSegment.values());
            model.addAttribute("pageTitle", "Edit Brand: " + existing.getName());
            return "brand/form";
        }
        brand.setId(id);
        brand.setWorkflowStatus(existing.getWorkflowStatus());
        chainService.findById(chainId).ifPresent(brand::setChain);
        brandService.save(brand, auth.getName());
        ra.addFlashAttribute("successMessage", "Brand updated.");
        return "redirect:/brand/" + id;
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String submit(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        brandService.submitForApproval(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Brand submitted for approval.");
        return "redirect:/brand/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        HotelBrand brand = brandService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        Long chainId = brand.getChain() != null ? brand.getChain().getId() : null;
        brandService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Brand deleted.");
        return chainId != null ? "redirect:/chain/" + chainId : "redirect:/chain";
    }

    @PostMapping("/{id}/standards")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String addStandard(@PathVariable Long id,
                              @ModelAttribute BrandStandard standard,
                              Authentication auth,
                              RedirectAttributes ra) {
        brandService.addStandard(id, standard, auth.getName());
        ra.addFlashAttribute("successMessage", "Brand standard added.");
        return "redirect:/brand/" + id;
    }

    @PostMapping("/standards/{standardId}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String deleteStandard(@PathVariable Long standardId,
                                 @RequestParam Long brandId,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        brandService.deleteStandard(standardId, auth.getName());
        ra.addFlashAttribute("successMessage", "Standard removed.");
        return "redirect:/brand/" + brandId;
    }
}
