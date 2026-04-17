package com.hotelmdm.domain.guest.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.guest.model.Guest;
import com.hotelmdm.domain.guest.model.GuestPreference;
import com.hotelmdm.domain.guest.model.PreferenceCategory;
import com.hotelmdm.domain.guest.repository.GuestRepository;
import com.hotelmdm.governance.service.WorkflowService;
import com.hotelmdm.quality.model.ValidationResult;
import com.hotelmdm.quality.service.DataQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final AuditService auditService;
    private final WorkflowService workflowService;
    private final DataQualityService dataQualityService;

    public List<Guest> findAll() {
        return guestRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    public Optional<Guest> findById(Long id) {
        return guestRepository.findById(id);
    }

    public List<Guest> search(String query) {
        return guestRepository.search(query);
    }

    @Transactional
    public Guest save(Guest guest, String actor) {
        boolean isNew = guest.getId() == null;
        guest.recalculateTier();
        Guest saved = guestRepository.save(guest);
        auditService.log(isNew ? "CREATE" : "UPDATE", "GUEST", saved.getId(), actor,
                saved.getFullName() + " <" + saved.getEmail() + ">");
        return saved;
    }

    @Transactional
    public Guest submitForApproval(Long id, String actor) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        guest.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        Guest saved = guestRepository.save(guest);
        workflowService.submitForApproval("GUEST", id, guest.getFullName(), actor);
        return saved;
    }

    @Transactional
    public void addPreference(Long guestId, PreferenceCategory category, String value, String notes, String actor) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        GuestPreference pref = new GuestPreference(category, value, guest);
        pref.setNotes(notes);
        guest.getPreferences().add(pref);
        guestRepository.save(guest);
        auditService.log("ADD_PREFERENCE", "GUEST", guestId, actor,
                category + ": " + value);
    }

    @Transactional
    public void removePreference(Long guestId, Long preferenceId, String actor) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        guest.getPreferences().removeIf(p -> p.getId().equals(preferenceId));
        guestRepository.save(guest);
        auditService.log("REMOVE_PREFERENCE", "GUEST", guestId, actor, "Preference id=" + preferenceId);
    }

    @Transactional
    public Guest addPoints(Long id, int points, String actor) {
        Guest guest = guestRepository.findById(id).orElseThrow();
        guest.setLoyaltyPoints(guest.getLoyaltyPoints() + points);
        guest.recalculateTier();
        Guest saved = guestRepository.save(guest);
        auditService.log("ADD_POINTS", "GUEST", id, actor, "+"+points+" points → "+saved.getLoyaltyTier().getLabel());
        return saved;
    }

    public void delete(Long id, String actor) {
        guestRepository.findById(id).ifPresent(g -> {
            auditService.log("DELETE", "GUEST", id, actor, g.getFullName());
            guestRepository.deleteById(id);
        });
    }

    public List<ValidationResult> validateQuality(Guest guest) {
        Map<String, Object> fields = Map.of(
                "firstName", guest.getFirstName() != null ? guest.getFirstName() : "",
                "lastName", guest.getLastName() != null ? guest.getLastName() : "",
                "email", guest.getEmail() != null ? guest.getEmail() : "",
                "phone", guest.getPhone() != null ? guest.getPhone() : "",
                "nationality", guest.getNationality() != null ? guest.getNationality() : ""
        );
        return dataQualityService.validate("GUEST", fields);
    }
}
