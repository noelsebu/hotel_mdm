package com.hotelmdm.governance.repository;

import com.hotelmdm.governance.model.ApprovalRequest;
import com.hotelmdm.governance.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalRequest, Long> {
    List<ApprovalRequest> findByStatusOrderByRequestedAtDesc(ApprovalStatus status);
    List<ApprovalRequest> findAllByOrderByRequestedAtDesc();
    long countByStatus(ApprovalStatus status);
    Optional<ApprovalRequest> findByEntityTypeAndEntityIdAndStatus(
            String entityType, Long entityId, ApprovalStatus status);
}
