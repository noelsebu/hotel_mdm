package com.hotelmdm.domain.vendor.controller;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.vendor.model.*;
import com.hotelmdm.domain.vendor.service.SupplierService;
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
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final AuditService auditService;

    // ─── Supplier CRUD ───────────────────────────────────────────────────────

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        return "vendor/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        model.addAttribute("supplier", supplier);
        model.addAttribute("contracts", supplierService.findContractsBySupplierId(id));
        model.addAttribute("contacts", supplierService.findContactsBySupplierId(id));
        model.addAttribute("auditHistory", auditService.getHistoryFor("SUPPLIER", id));
        model.addAttribute("qualityIssues", supplierService.validateQuality(supplier));
        model.addAttribute("newContact", new SupplierContact());
        model.addAttribute("newContract", new Contract());
        model.addAttribute("contractStatuses", ContractStatus.values());
        return "vendor/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String newSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("categories", SupplierCategory.values());
        model.addAttribute("statuses", SupplierStatus.values());
        return "vendor/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String createSupplier(@Valid @ModelAttribute("supplier") Supplier supplier,
                                 BindingResult result,
                                 Authentication auth,
                                 Model model,
                                 RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", SupplierCategory.values());
            model.addAttribute("statuses", SupplierStatus.values());
            return "vendor/form";
        }
        List<ValidationResult> errors = supplierService.validateQuality(supplier).stream()
                .filter(ValidationResult::isError).toList();
        if (!errors.isEmpty()) {
            model.addAttribute("qualityErrors", errors);
            model.addAttribute("categories", SupplierCategory.values());
            model.addAttribute("statuses", SupplierStatus.values());
            return "vendor/form";
        }
        Supplier saved = supplierService.save(supplier, auth.getName());
        ra.addFlashAttribute("successMessage", "Supplier '" + saved.getName() + "' created as DRAFT.");
        return "redirect:/vendor/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String editSupplier(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        model.addAttribute("supplier", supplier);
        model.addAttribute("categories", SupplierCategory.values());
        model.addAttribute("statuses", SupplierStatus.values());
        return "vendor/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String updateSupplier(@PathVariable Long id,
                                 @Valid @ModelAttribute("supplier") Supplier supplier,
                                 BindingResult result,
                                 Authentication auth,
                                 Model model,
                                 RedirectAttributes ra) {
        Supplier existing = supplierService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        if (!existing.isEditable()) {
            ra.addFlashAttribute("errorMessage", "Only DRAFT or REJECTED suppliers can be edited.");
            return "redirect:/vendor/" + id;
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", SupplierCategory.values());
            model.addAttribute("statuses", SupplierStatus.values());
            return "vendor/form";
        }
        supplier.setId(id);
        supplier.setWorkflowStatus(existing.getWorkflowStatus());
        supplierService.save(supplier, auth.getName());
        ra.addFlashAttribute("successMessage", "Supplier updated.");
        return "redirect:/vendor/" + id;
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String submitForApproval(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        supplierService.submitForApproval(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Supplier submitted for approval.");
        return "redirect:/vendor/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteSupplier(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        supplierService.delete(id, auth.getName());
        ra.addFlashAttribute("successMessage", "Supplier deleted.");
        return "redirect:/vendor";
    }

    // ─── Contracts ───────────────────────────────────────────────────────────

    @PostMapping("/{id}/contracts")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String addContract(@PathVariable Long id,
                              @Valid @ModelAttribute("newContract") Contract contract,
                              BindingResult result,
                              Authentication auth,
                              RedirectAttributes ra,
                              Model model) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("errorMessage", "Contract validation failed.");
            return "redirect:/vendor/" + id;
        }
        supplierService.saveContract(contract, id, auth.getName());
        ra.addFlashAttribute("successMessage", "Contract added.");
        return "redirect:/vendor/" + id;
    }

    @PostMapping("/{id}/contracts/{cid}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteContract(@PathVariable Long id, @PathVariable Long cid,
                                 Authentication auth, RedirectAttributes ra) {
        supplierService.deleteContract(cid, auth.getName());
        ra.addFlashAttribute("successMessage", "Contract deleted.");
        return "redirect:/vendor/" + id;
    }

    // ─── Contacts ────────────────────────────────────────────────────────────

    @PostMapping("/{id}/contacts")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER','DATA_STEWARD')")
    public String addContact(@PathVariable Long id,
                             @Valid @ModelAttribute("newContact") SupplierContact contact,
                             BindingResult result,
                             Authentication auth,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("errorMessage", "Contact validation failed.");
            return "redirect:/vendor/" + id;
        }
        supplierService.saveContact(contact, id, auth.getName());
        ra.addFlashAttribute("successMessage", "Contact added.");
        return "redirect:/vendor/" + id;
    }

    @PostMapping("/{id}/contacts/{cid}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String deleteContact(@PathVariable Long id, @PathVariable Long cid,
                                Authentication auth, RedirectAttributes ra) {
        supplierService.deleteContact(cid, auth.getName());
        ra.addFlashAttribute("successMessage", "Contact deleted.");
        return "redirect:/vendor/" + id;
    }
}
