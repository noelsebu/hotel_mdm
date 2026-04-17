package com.hotelmdm.governance.controller;

import com.hotelmdm.domain.guest.repository.GuestRepository;
import com.hotelmdm.domain.property.repository.HotelRepository;
import com.hotelmdm.domain.vendor.repository.SupplierRepository;
import com.hotelmdm.governance.model.ApprovalRequest;
import com.hotelmdm.governance.repository.ApprovalRepository;
import com.hotelmdm.governance.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/governance/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final WorkflowService workflowService;
    private final ApprovalRepository approvalRepository;
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final SupplierRepository supplierRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pendingRequests", workflowService.getPendingRequests());
        model.addAttribute("allRequests", workflowService.getAllRequests());
        return "governance/approvals/list";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String detail(@PathVariable Long id, Model model) {
        ApprovalRequest request = approvalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        model.addAttribute("request", request);

        // Load the governed entity for display
        switch (request.getEntityType()) {
            case "HOTEL" -> hotelRepository.findById(request.getEntityId())
                    .ifPresent(h -> model.addAttribute("entity", h));
            case "GUEST" -> guestRepository.findById(request.getEntityId())
                    .ifPresent(g -> model.addAttribute("entity", g));
            case "SUPPLIER" -> supplierRepository.findById(request.getEntityId())
                    .ifPresent(s -> model.addAttribute("entity", s));
        }
        return "governance/approvals/detail";
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String approve(@PathVariable Long id,
                          @RequestParam(required = false) String comment,
                          Authentication auth,
                          RedirectAttributes ra) {
        ApprovalRequest request = approvalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        switch (request.getEntityType()) {
            case "HOTEL" -> hotelRepository.findById(request.getEntityId()).ifPresent(h ->
                    workflowService.approve(id, auth.getName(), comment, h, hotelRepository));
            case "GUEST" -> guestRepository.findById(request.getEntityId()).ifPresent(g ->
                    workflowService.approve(id, auth.getName(), comment, g, guestRepository));
            case "SUPPLIER" -> supplierRepository.findById(request.getEntityId()).ifPresent(s ->
                    workflowService.approve(id, auth.getName(), comment, s, supplierRepository));
        }

        ra.addFlashAttribute("successMessage", "Record approved successfully.");
        return "redirect:/governance/approvals";
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','DATA_MANAGER')")
    public String reject(@PathVariable Long id,
                         @RequestParam String comment,
                         Authentication auth,
                         RedirectAttributes ra) {
        ApprovalRequest request = approvalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        switch (request.getEntityType()) {
            case "HOTEL" -> hotelRepository.findById(request.getEntityId()).ifPresent(h ->
                    workflowService.reject(id, auth.getName(), comment, h, hotelRepository));
            case "GUEST" -> guestRepository.findById(request.getEntityId()).ifPresent(g ->
                    workflowService.reject(id, auth.getName(), comment, g, guestRepository));
            case "SUPPLIER" -> supplierRepository.findById(request.getEntityId()).ifPresent(s ->
                    workflowService.reject(id, auth.getName(), comment, s, supplierRepository));
        }

        ra.addFlashAttribute("errorMessage", "Record rejected.");
        return "redirect:/governance/approvals";
    }
}
