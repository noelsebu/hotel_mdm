package com.hotelmdm.domain.chain.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.chain.model.BrandStandard;
import com.hotelmdm.domain.chain.model.HotelBrand;
import com.hotelmdm.domain.chain.repository.BrandStandardRepository;
import com.hotelmdm.domain.chain.repository.HotelBrandRepository;
import com.hotelmdm.governance.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelBrandService {

    private final HotelBrandRepository brandRepository;
    private final BrandStandardRepository standardRepository;
    private final AuditService auditService;
    private final WorkflowService workflowService;

    public List<HotelBrand> findAll() {
        return brandRepository.findAllByOrderByNameAsc();
    }

    public List<HotelBrand> findByChain(Long chainId) {
        return brandRepository.findByChainIdOrderByNameAsc(chainId);
    }

    public Optional<HotelBrand> findById(Long id) {
        return brandRepository.findById(id);
    }

    public Optional<HotelBrand> findByIdWithStandards(Long id) {
        return brandRepository.findByIdWithStandards(id);
    }

    public Optional<HotelBrand> findByIdWithProperties(Long id) {
        return brandRepository.findByIdWithProperties(id);
    }

    public long count() {
        return brandRepository.count();
    }

    public List<BrandStandard> findStandards(Long brandId) {
        return standardRepository.findByBrandIdOrderByCategoryAscTitleAsc(brandId);
    }

    public long countMandatoryStandards(Long brandId) {
        return standardRepository.countByBrandIdAndMandatoryTrue(brandId);
    }

    @Transactional
    public HotelBrand save(HotelBrand brand, String actor) {
        boolean isNew = brand.getId() == null;
        if (isNew) brand.setWorkflowStatus(WorkflowStatus.DRAFT);
        HotelBrand saved = brandRepository.save(brand);
        auditService.log(isNew ? "CREATE" : "UPDATE", "BRAND", saved.getId(), actor,
                saved.getName() + " [" + saved.getTier().getLabel() + "] under " + saved.getChain().getName());
        return saved;
    }

    @Transactional
    public HotelBrand submitForApproval(Long id, String actor) {
        HotelBrand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + id));
        brand.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        HotelBrand saved = brandRepository.save(brand);
        workflowService.submitForApproval("BRAND", id, brand.getName(), actor);
        return saved;
    }

    @Transactional
    public void delete(Long id, String actor) {
        brandRepository.findById(id).ifPresent(b -> {
            auditService.log("DELETE", "BRAND", id, actor, b.getName());
            brandRepository.deleteById(id);
        });
    }

    @Transactional
    public BrandStandard addStandard(Long brandId, BrandStandard standard, String actor) {
        HotelBrand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
        standard.setBrand(brand);
        BrandStandard saved = standardRepository.save(standard);
        auditService.log("CREATE", "BRAND_STANDARD", saved.getId(), actor,
                "Standard '" + standard.getTitle() + "' added to " + brand.getName());
        return saved;
    }

    @Transactional
    public void deleteStandard(Long standardId, String actor) {
        standardRepository.findById(standardId).ifPresent(s -> {
            auditService.log("DELETE", "BRAND_STANDARD", standardId, actor,
                    "Standard '" + s.getTitle() + "' removed from " + s.getBrand().getName());
            standardRepository.deleteById(standardId);
        });
    }
}
