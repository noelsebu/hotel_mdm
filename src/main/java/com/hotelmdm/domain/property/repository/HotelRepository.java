package com.hotelmdm.domain.property.repository;

import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.property.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Optional<Hotel> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    List<Hotel> findByWorkflowStatusOrderByNameAsc(WorkflowStatus status);
    List<Hotel> findAllByOrderByNameAsc();
    List<Hotel> findByCityContainingIgnoreCaseOrNameContainingIgnoreCase(String city, String name);

    @Query("SELECT h FROM Hotel h LEFT JOIN FETCH h.amenities WHERE h.id = :id")
    Optional<Hotel> findByIdWithAmenities(Long id);

    List<Hotel> findByBrandIdOrderByNameAsc(Long brandId);
}
