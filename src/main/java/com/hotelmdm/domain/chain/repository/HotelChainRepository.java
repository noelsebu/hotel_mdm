package com.hotelmdm.domain.chain.repository;

import com.hotelmdm.domain.chain.model.HotelChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelChainRepository extends JpaRepository<HotelChain, Long> {

    List<HotelChain> findAllByOrderByNameAsc();

    Optional<HotelChain> findByCode(String code);

    @Query("SELECT c FROM HotelChain c LEFT JOIN FETCH c.brands WHERE c.id = :id")
    Optional<HotelChain> findByIdWithBrands(@Param("id") Long id);
}
