package com.hotelmdm.domain.chain.repository;

import com.hotelmdm.domain.chain.model.BrandStandard;
import com.hotelmdm.domain.chain.model.StandardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandStandardRepository extends JpaRepository<BrandStandard, Long> {

    List<BrandStandard> findByBrandIdOrderByCategoryAscTitleAsc(Long brandId);

    List<BrandStandard> findByBrandIdAndCategoryOrderByTitleAsc(Long brandId, StandardCategory category);

    long countByBrandIdAndMandatoryTrue(Long brandId);
}
