package com.hotelmdm.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_entries", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entityType, entityId"),
        @Index(name = "idx_audit_performed_at", columnList = "performedAt")
})
@Getter
@Setter
@NoArgsConstructor
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;   // HOTEL, ROOM, GUEST, SUPPLIER, etc.

    private Long entityId;

    @Column(nullable = false)
    private String action;        // CREATE, UPDATE, DELETE, SUBMIT, APPROVE, REJECT

    private String performedBy;

    @Column(length = 2000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    public AuditEntry(String action, String entityType, Long entityId,
                      String performedBy, String details) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.details = details;
        this.performedAt = LocalDateTime.now();
    }
}
