package com.hotelmdm.common;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for entities that participate in the MDM governance workflow.
 * Records move through: DRAFT → PENDING_APPROVAL → APPROVED / REJECTED
 */
@MappedSuperclass
@Getter
@Setter
public abstract class GovernedEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus workflowStatus = WorkflowStatus.DRAFT;

    @Column(length = 500)
    private String rejectionReason;

    public boolean isDraft() {
        return workflowStatus == WorkflowStatus.DRAFT;
    }

    public boolean isPendingApproval() {
        return workflowStatus == WorkflowStatus.PENDING_APPROVAL;
    }

    public boolean isApproved() {
        return workflowStatus == WorkflowStatus.APPROVED;
    }

    public boolean isRejected() {
        return workflowStatus == WorkflowStatus.REJECTED;
    }

    public boolean isEditable() {
        return workflowStatus == WorkflowStatus.DRAFT || workflowStatus == WorkflowStatus.REJECTED;
    }
}
