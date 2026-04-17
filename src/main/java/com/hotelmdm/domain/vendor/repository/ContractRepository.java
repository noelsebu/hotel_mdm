package com.hotelmdm.domain.vendor.repository;

import com.hotelmdm.domain.vendor.model.Contract;
import com.hotelmdm.domain.vendor.model.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findBySupplierIdOrderByStartDateDesc(Long supplierId);
    List<Contract> findByStatus(ContractStatus status);
    List<Contract> findByEndDateBefore(LocalDate date);
    boolean existsByContractNumber(String contractNumber);
    boolean existsByContractNumberAndIdNot(String contractNumber, Long id);
}
