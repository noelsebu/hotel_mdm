package com.hotelmdm.domain.vendor.repository;

import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.vendor.model.Supplier;
import com.hotelmdm.domain.vendor.model.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    List<Supplier> findByStatusOrderByNameAsc(SupplierStatus status);
    List<Supplier> findByWorkflowStatusOrderByNameAsc(WorkflowStatus wfStatus);
    List<Supplier> findAllByOrderByNameAsc();
}
