package com.hotelmdm.domain.chain.controller;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.chain.model.HotelChain;
import com.hotelmdm.domain.chain.service.HotelBrandService;
import com.hotelmdm.domain.chain.service.HotelChainService;
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
@RequestMapping("/chain")
@RequiredArgsConstructor
public class HotelChainController {

    private final HotelChainService chainService;
    private final HotelBrandService brandService;
    private final AuditService auditService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("chains", chainService.findAll());
        return "chain/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        HotelChain chain = chainService.findByIdWithBrands(id)
                .orElseThrow(() -> new IllegalArgumentException("Chain not found: " + id));
        model.addAttribute("chain", chain);
        model.addAttribute("auditHistory", auditService.getHistoryFor("CHAIN", id));
        return "chain/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String newChain(Model model) {
        model.addAttribute("chain", new HotelChain());
        model.addAttribute("pageTitle", "New Hotel Chain");
        return "chain/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String createChain(@Valid @ModelAttribute("chain") HotelChain chain,
                              BindingResult result,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "New Hotel Chain");
            return "chain/form";
        }
        HotelChain saved = chainService.save(chain, auth.getName());
        ra.addFlashAttribute("successMessage", "Chain '" + saved.getName() + "' created as DRAFT.");
        return "redirect:/chain/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String editChain(@PathVariable Long id, Model model) {
        HotelChain chain = chainService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chain not found"));
        model.addAttribute("chain", chain);
        model.addAttribute("pageTitle", "Edit Chain: " + chain.getName());
        return "chain/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String updateChain(@PathVariable Long id,
                              @Valid @ModelAttribute("chain") HotelChain chain,
                              BindingResult result,
                              Authentication auth,
                              Model model,
                              RedirectAttributes ra) {
        HotelChain existing = chainService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chain not found"));
        if (!existing.isEditable()) {
            ra.addFlashAttribute("errorMessage", "Only DRAFT or REJECTED chains can be edited.");
            return "redirect:/chain/" + id;
        }
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Chain: " + existing.getName());
            return "chain/form";
        }
        chain.setId(id);
        chain.setWorkflowStatus(existing.getWorkflowStatus());
        chainService.save(chain, auth.getName());
        ra.addFlashAttribute("successMessage", "Chain updated.");
        return "redirect:/chain/" + id;
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String submit(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        chainService.submitForApproval(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Chain submitted for approval.");
        return "redirect:/chain/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        chainService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Chain deleted.");
        return "redirect:/chain";
    }
}
