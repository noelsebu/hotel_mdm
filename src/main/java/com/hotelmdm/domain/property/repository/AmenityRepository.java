package com.hotelmdm.domain.property.repository;

import com.hotelmdm.domain.property.model.Amenity;
import com.hotelmdm.domain.property.model.AmenityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    List<Amenity> findByCategoryOrderByNameAsc(AmenityCategory category);
    List<Amenity> findAllByOrderByNameAsc();
}
