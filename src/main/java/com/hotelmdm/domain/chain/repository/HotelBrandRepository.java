package com.hotelmdm.domain.chain.repository;

import com.hotelmdm.domain.chain.model.BrandTier;
import com.hotelmdm.domain.chain.model.HotelBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelBrandRepository extends JpaRepository<HotelBrand, Long> {

    List<HotelBrand> findAllByOrderByNameAsc();

    List<HotelBrand> findByChainIdOrderByNameAsc(Long chainId);

    List<HotelBrand> findByTierOrderByNameAsc(BrandTier tier);

    @Query("SELECT b FROM HotelBrand b LEFT JOIN FETCH b.standards WHERE b.id = :id")
    Optional<HotelBrand> findByIdWithStandards(@Param("id") Long id);

    @Query("SELECT b FROM HotelBrand b LEFT JOIN FETCH b.properties WHERE b.id = :id")
    Optional<HotelBrand> findByIdWithProperties(@Param("id") Long id);
}
