package com.example.nordeCoreTask.repositories;

import com.example.nordeCoreTask.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:role IS NULL OR EXISTS (SELECT r FROM u.roles r WHERE r.name = :role))")
    Page<User> findByFilter(@Param("username") String username,
                            @Param("role") String role,
                            Pageable pageable);
}