package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer id);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

}
