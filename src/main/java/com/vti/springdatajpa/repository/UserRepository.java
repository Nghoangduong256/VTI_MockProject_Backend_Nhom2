package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
