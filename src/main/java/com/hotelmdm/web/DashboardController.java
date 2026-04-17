package com.hotelmdm.web;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.guest.repository.GuestRepository;
import com.hotelmdm.domain.property.repository.HotelRepository;
import com.hotelmdm.domain.property.repository.RoomRepository;
import com.hotelmdm.domain.vendor.repository.SupplierRepository;
import com.hotelmdm.governance.service.DataStewardTaskService;
import com.hotelmdm.governance.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final SupplierRepository supplierRepository;
    private final WorkflowService workflowService;
    private final DataStewardTaskService taskService;
    private final AuditService auditService;

    @GetMapping
    public String dashboard(Model model, Authentication auth) {
        // Counts
        model.addAttribute("totalHotels", hotelRepository.count());
        model.addAttribute("totalRooms", roomRepository.count());
        model.addAttribute("totalGuests", guestRepository.count());
        model.addAttribute("totalSuppliers", supplierRepository.count());

        // Governance
        model.addAttribute("pendingApprovals", workflowService.countPending());
        model.addAttribute("openTasks", taskService.countOpen());

        // Recent activity
        model.addAttribute("recentActivity", auditService.getRecentEntries());

        // Pending approvals list (first 5)
        model.addAttribute("recentApprovals", workflowService.getPendingRequests().stream()
                .limit(5).toList());

        // Open tasks (first 5)
        model.addAttribute("openTaskList", taskService.findAll().stream()
                .limit(5).toList());

        return "dashboard/index";
    }
}
