package com.hotelmdm.domain.vendor.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.vendor.model.*;
import com.hotelmdm.domain.vendor.repository.ContractRepository;
import com.hotelmdm.domain.vendor.repository.SupplierContactRepository;
import com.hotelmdm.domain.vendor.repository.SupplierRepository;
import com.hotelmdm.governance.service.WorkflowService;
import com.hotelmdm.quality.model.ValidationResult;
import com.hotelmdm.quality.service.DataQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ContractRepository contractRepository;
    private final SupplierContactRepository contactRepository;
    private final AuditService auditService;
    private final WorkflowService workflowService;
    private final DataQualityService dataQualityService;

    public List<Supplier> findAll() {
        return supplierRepository.findAllByOrderByNameAsc();
    }

    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public Supplier save(Supplier supplier, String actor) {
        boolean isNew = supplier.getId() == null;
        if (isNew) supplier.setWorkflowStatus(WorkflowStatus.DRAFT);
        Supplier saved = supplierRepository.save(supplier);
        auditService.log(isNew ? "CREATE" : "UPDATE", "SUPPLIER", saved.getId(), actor,
                saved.getName() + " [" + saved.getCategory() + "]");
        return saved;
    }

    @Transactional
    public Supplier submitForApproval(Long id, String actor) {
        Supplier s = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        s.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        Supplier saved = supplierRepository.save(s);
        workflowService.submitForApproval("SUPPLIER", id, s.getName(), actor);
        return saved;
    }

    @Transactional
    public Contract saveContract(Contract contract, Long supplierId, String actor) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        contract.setSupplier(supplier);
        boolean isNew = contract.getId() == null;
        Contract saved = contractRepository.save(contract);
        auditService.log(isNew ? "CREATE" : "UPDATE", "CONTRACT", saved.getId(), actor,
                saved.getContractNumber() + " for " + supplier.getName());
        return saved;
    }

    @Transactional
    public void deleteContract(Long contractId, String actor) {
        contractRepository.findById(contractId).ifPresent(c -> {
            auditService.log("DELETE", "CONTRACT", contractId, actor, c.getContractNumber());
            contractRepository.deleteById(contractId);
        });
    }

    @Transactional
    public SupplierContact saveContact(SupplierContact contact, Long supplierId, String actor) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        contact.setSupplier(supplier);
        return contactRepository.save(contact);
    }

    @Transactional
    public void deleteContact(Long contactId, String actor) {
        contactRepository.deleteById(contactId);
    }

    public void delete(Long id, String actor) {
        supplierRepository.findById(id).ifPresent(s -> {
            auditService.log("DELETE", "SUPPLIER", id, actor, s.getName());
            supplierRepository.deleteById(id);
        });
    }

    public List<ValidationResult> validateQuality(Supplier supplier) {
        Map<String, Object> fields = Map.of(
                "name", supplier.getName() != null ? supplier.getName() : "",
                "code", supplier.getCode() != null ? supplier.getCode() : "",
                "email", supplier.getEmail() != null ? supplier.getEmail() : "",
                "phone", supplier.getPhone() != null ? supplier.getPhone() : ""
        );
        return dataQualityService.validate("SUPPLIER", fields);
    }

    public List<Contract> findContractsBySupplierId(Long supplierId) {
        return contractRepository.findBySupplierIdOrderByStartDateDesc(supplierId);
    }

    public List<SupplierContact> findContactsBySupplierId(Long supplierId) {
        return contactRepository.findBySupplierIdOrderByPrimaryContactDesc(supplierId);
    }
}
