package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserManagerRepository extends JpaRepository<User, Integer> {
    List<User> findByRoleNot(Role role);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void lockUser(Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void unlockUser(Integer id);
}
