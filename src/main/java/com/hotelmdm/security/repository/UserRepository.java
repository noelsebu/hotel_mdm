package com.hotelmdm.security.repository;

import com.hotelmdm.security.model.AppUser;
import com.hotelmdm.security.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<AppUser> findByRole(UserRole role);
}
