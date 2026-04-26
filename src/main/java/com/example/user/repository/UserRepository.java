package com.example.user.repository;

import com.example.user.entity.User;
import com.example.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(long id);

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<User> findByStatus(UserStatus status);

    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    void updateStatus(Long id, UserStatus status);
}
