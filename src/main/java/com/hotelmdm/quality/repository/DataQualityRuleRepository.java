package com.hotelmdm.quality.repository;

import com.hotelmdm.quality.model.DataQualityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataQualityRuleRepository extends JpaRepository<DataQualityRule, Long> {
    List<DataQualityRule> findByEntityTypeAndActiveTrue(String entityType);
    List<DataQualityRule> findByEntityTypeOrderByFieldNameAsc(String entityType);
    List<DataQualityRule> findAllByOrderByEntityTypeAscFieldNameAsc();
}
