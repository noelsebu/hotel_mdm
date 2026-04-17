package com.hotelmdm.domain.guest.repository;

import com.hotelmdm.common.WorkflowStatus;
import com.hotelmdm.domain.guest.model.Guest;
import com.hotelmdm.domain.guest.model.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    List<Guest> findByLoyaltyTierOrderByLastNameAsc(LoyaltyTier tier);
    List<Guest> findByWorkflowStatusOrderByLastNameAsc(WorkflowStatus status);
    List<Guest> findAllByOrderByLastNameAscFirstNameAsc();

    @Query("SELECT g FROM Guest g WHERE LOWER(g.firstName) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(g.lastName) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(g.email) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Guest> search(String q);
}
