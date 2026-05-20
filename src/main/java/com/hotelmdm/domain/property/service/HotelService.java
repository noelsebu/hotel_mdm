package com.hotelmdm.domain.property.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.chain.repository.HotelBrandRepository;
import com.hotelmdm.domain.property.model.Amenity;
import com.hotelmdm.domain.property.model.Hotel;
import com.hotelmdm.domain.property.repository.AmenityRepository;
import com.hotelmdm.domain.property.repository.HotelRepository;
import com.hotelmdm.governance.service.WorkflowService;
import com.hotelmdm.quality.model.ValidationResult;
import com.hotelmdm.quality.service.DataQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelBrandRepository brandRepository;
    private final AuditService auditService;
    private final WorkflowService workflowService;
    private final DataQualityService dataQualityService;

    public List<Hotel> findAll() {
        return hotelRepository.findAllByOrderByNameAsc();
    }

    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }

    public Optional<Hotel> findByIdWithAmenities(Long id) {
        return hotelRepository.findByIdWithAmenities(id);
    }

    @Transactional
    public Hotel save(Hotel hotel, List<Long> amenityIds, Long brandId, String actor) {
        if (hotel.getId() == null) {
            hotel.setWorkflowStatus(WorkflowStatus.DRAFT);
        }
        if (amenityIds != null && !amenityIds.isEmpty()) {
            hotel.setAmenities(new HashSet<>(amenityRepository.findAllById(amenityIds)));
        } else {
            hotel.setAmenities(new HashSet<>());
        }
        if (brandId != null) {
            brandRepository.findById(brandId).ifPresent(hotel::setBrand);
        } else {
            hotel.setBrand(null);
        }
        boolean isNew = hotel.getId() == null;
        Hotel saved = hotelRepository.save(hotel);
        auditService.log(isNew ? "CREATE" : "UPDATE", "HOTEL", saved.getId(), actor,
                saved.getName() + " [" + saved.getCity() + ", " + saved.getCountry() + "]");
        return saved;
    }

    @Transactional
    public Hotel submitForApproval(Long id, String actor) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + id));
        hotel.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        Hotel saved = hotelRepository.save(hotel);
        workflowService.submitForApproval("HOTEL", id, hotel.getName(), actor);
        return saved;
    }

    public void delete(Long id, String actor) {
        hotelRepository.findById(id).ifPresent(h -> {
            auditService.log("DELETE", "HOTEL", id, actor, h.getName());
            hotelRepository.deleteById(id);
        });
    }

    public List<ValidationResult> validateQuality(Hotel hotel) {
        Map<String, Object> fields = Map.of(
                "name", hotel.getName() != null ? hotel.getName() : "",
                "code", hotel.getCode() != null ? hotel.getCode() : "",
                "email", hotel.getEmail() != null ? hotel.getEmail() : "",
                "phone", hotel.getPhone() != null ? hotel.getPhone() : "",
                "city", hotel.getCity() != null ? hotel.getCity() : "",
                "country", hotel.getCountry() != null ? hotel.getCountry() : ""
        );
        return dataQualityService.validate("HOTEL", fields);
    }
}
