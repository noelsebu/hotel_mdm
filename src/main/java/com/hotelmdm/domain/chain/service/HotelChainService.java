package com.hotelmdm.domain.chain.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.chain.model.HotelChain;
import com.hotelmdm.domain.chain.repository.HotelChainRepository;
import com.hotelmdm.governance.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelChainService {

    private final HotelChainRepository chainRepository;
    private final AuditService auditService;
    private final WorkflowService workflowService;

    public List<HotelChain> findAll() {
        return chainRepository.findAllByOrderByNameAsc();
    }

    public Optional<HotelChain> findById(Long id) {
        return chainRepository.findById(id);
    }

    public Optional<HotelChain> findByIdWithBrands(Long id) {
        return chainRepository.findByIdWithBrands(id);
    }

    public long count() {
        return chainRepository.count();
    }

    @Transactional
    public HotelChain save(HotelChain chain, String actor) {
        boolean isNew = chain.getId() == null;
        if (isNew) chain.setWorkflowStatus(WorkflowStatus.DRAFT);
        HotelChain saved = chainRepository.save(chain);
        auditService.log(isNew ? "CREATE" : "UPDATE", "CHAIN", saved.getId(), actor,
                saved.getName() + " [HQ: " + saved.getHeadquarters() + "]");
        return saved;
    }

    @Transactional
    public HotelChain submitForApproval(Long id, String actor) {
        HotelChain chain = chainRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chain not found: " + id));
        chain.setWorkflowStatus(WorkflowStatus.PENDING_APPROVAL);
        HotelChain saved = chainRepository.save(chain);
        workflowService.submitForApproval("CHAIN", id, chain.getName(), actor);
        return saved;
    }

    @Transactional
    public void delete(Long id, String actor) {
        chainRepository.findById(id).ifPresent(c -> {
            auditService.log("DELETE", "CHAIN", id, actor, c.getName());
            chainRepository.deleteById(id);
        });
    }
}
