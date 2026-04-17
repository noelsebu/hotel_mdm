package com.hotelmdm.domain.property.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.domain.property.model.Amenity;
import com.hotelmdm.domain.property.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final AuditService auditService;

    public List<Amenity> findAll() {
        return amenityRepository.findAllByOrderByNameAsc();
    }

    public Optional<Amenity> findById(Long id) {
        return amenityRepository.findById(id);
    }

    public Amenity save(Amenity amenity, String actor) {
        boolean isNew = amenity.getId() == null;
        if (!isNew && amenityRepository.existsByNameAndIdNot(amenity.getName(), amenity.getId())) {
            throw new IllegalArgumentException("Amenity name already exists: " + amenity.getName());
        }
        Amenity saved = amenityRepository.save(amenity);
        auditService.log(isNew ? "CREATE" : "UPDATE", "AMENITY", saved.getId(), actor,
                saved.getName() + " [" + saved.getCategory() + "]");
        return saved;
    }

    public void delete(Long id, String actor) {
        amenityRepository.findById(id).ifPresent(a -> {
            auditService.log("DELETE", "AMENITY", id, actor, a.getName());
            amenityRepository.deleteById(id);
        });
    }
}
