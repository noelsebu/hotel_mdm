package com.hotelmdm.domain.vendor.repository;

import com.hotelmdm.domain.vendor.model.SupplierContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierContactRepository extends JpaRepository<SupplierContact, Long> {
    List<SupplierContact> findBySupplierIdOrderByPrimaryContactDesc(Long supplierId);
}
