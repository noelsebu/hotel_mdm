package com.hotelmdm.governance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_requests")
@Getter
@Setter
@NoArgsConstructor
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** "HOTEL", "GUEST", "SUPPLIER" */
    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    /** Human-readable label (e.g. hotel name) for display */
    private String entityLabel;

    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private String reviewedBy;
    private LocalDateTime reviewedAt;

    @Column(length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    public ApprovalRequest(String entityType, Long entityId, String entityLabel, String requestedBy) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityLabel = entityLabel;
        this.requestedBy = requestedBy;
        this.requestedAt = LocalDateTime.now();
        this.status = ApprovalStatus.PENDING;
    }
}
