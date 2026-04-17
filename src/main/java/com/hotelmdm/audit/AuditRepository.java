package com.hotelmdm.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, Long> {
    List<AuditEntry> findByEntityTypeAndEntityIdOrderByPerformedAtDesc(String entityType, Long entityId);
    Page<AuditEntry> findAllByOrderByPerformedAtDesc(Pageable pageable);
    List<AuditEntry> findTop10ByOrderByPerformedAtDesc();
    List<AuditEntry> findByEntityTypeOrderByPerformedAtDesc(String entityType);
    List<AuditEntry> findByPerformedByOrderByPerformedAtDesc(String performedBy);
}
