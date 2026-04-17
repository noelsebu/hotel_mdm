package com.hotelmdm.governance.service;

import com.hotelmdm.audit.AuditService;
import com.hotelmdm.common.GovernedEntity;
import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.governance.model.ApprovalRequest;
import com.hotelmdm.governance.model.ApprovalStatus;
import com.hotelmdm.governance.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final ApprovalRepository approvalRepository;
    private final AuditService auditService;

    @Transactional
    public ApprovalRequest submitForApproval(String entityType, Long entityId,
                                              String entityLabel, String submittedBy) {
        // Cancel any existing pending request for this entity
        approvalRepository.findByEntityTypeAndEntityIdAndStatus(entityType, entityId, ApprovalStatus.PENDING)
                .ifPresent(r -> {
                    r.setStatus(ApprovalStatus.REJECTED);
                    r.setComment("Superseded by new submission");
                    r.setReviewedAt(LocalDateTime.now());
                    approvalRepository.save(r);
                });

        ApprovalRequest request = new ApprovalRequest(entityType, entityId, entityLabel, submittedBy);
        ApprovalRequest saved = approvalRepository.save(request);

        auditService.log("SUBMIT_FOR_APPROVAL", entityType, entityId, submittedBy,
                "Submitted for approval");
        return saved;
    }

    @Transactional
    public void approve(Long requestId, String reviewedBy, String comment,
                        GovernedEntity entity,
                        org.springframework.data.jpa.repository.JpaRepository<? extends GovernedEntity, Long> repo) {
        ApprovalRequest request = approvalRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found: " + requestId));

        request.setStatus(ApprovalStatus.APPROVED);
        request.setReviewedBy(reviewedBy);
        request.setReviewedAt(LocalDateTime.now());
        request.setComment(comment);
        approvalRepository.save(request);

        entity.setWorkflowStatus(WorkflowStatus.APPROVED);
        entity.setRejectionReason(null);
        saveEntity(entity, repo);

        auditService.log("APPROVE", request.getEntityType(), request.getEntityId(),
                reviewedBy, comment);
    }

    @Transactional
    public void reject(Long requestId, String reviewedBy, String comment,
                       GovernedEntity entity,
                       org.springframework.data.jpa.repository.JpaRepository<? extends GovernedEntity, Long> repo) {
        ApprovalRequest request = approvalRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found: " + requestId));

        request.setStatus(ApprovalStatus.REJECTED);
        request.setReviewedBy(reviewedBy);
        request.setReviewedAt(LocalDateTime.now());
        request.setComment(comment);
        approvalRepository.save(request);

        entity.setWorkflowStatus(WorkflowStatus.REJECTED);
        entity.setRejectionReason(comment);
        saveEntity(entity, repo);

        auditService.log("REJECT", request.getEntityType(), request.getEntityId(),
                reviewedBy, comment);
    }

    @SuppressWarnings("unchecked")
    private <T extends GovernedEntity> void saveEntity(
            GovernedEntity entity,
            org.springframework.data.jpa.repository.JpaRepository<? extends GovernedEntity, Long> repo) {
        ((org.springframework.data.jpa.repository.JpaRepository<T, Long>) repo).save((T) entity);
    }

    public List<ApprovalRequest> getPendingRequests() {
        return approvalRepository.findByStatusOrderByRequestedAtDesc(ApprovalStatus.PENDING);
    }

    public List<ApprovalRequest> getAllRequests() {
        return approvalRepository.findAllByOrderByRequestedAtDesc();
    }

    public long countPending() {
        return approvalRepository.countByStatus(ApprovalStatus.PENDING);
    }
}
