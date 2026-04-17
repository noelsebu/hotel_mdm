package com.hotelmdm.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public void log(String action, String entityType, Long entityId,
                    String performedBy, String details) {
        auditRepository.save(new AuditEntry(action, entityType, entityId, performedBy, details));
    }

    public List<AuditEntry> getHistoryFor(String entityType, Long entityId) {
        return auditRepository.findByEntityTypeAndEntityIdOrderByPerformedAtDesc(entityType, entityId);
    }

    public List<AuditEntry> getRecentEntries() {
        return auditRepository.findTop10ByOrderByPerformedAtDesc();
    }

    public List<AuditEntry> getAllEntries() {
        return auditRepository.findAllByOrderByPerformedAtDesc(
                org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public List<AuditEntry> getEntriesByEntityType(String entityType) {
        return auditRepository.findByEntityTypeOrderByPerformedAtDesc(entityType);
    }
}
