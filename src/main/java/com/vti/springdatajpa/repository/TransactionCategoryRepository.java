package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Integer> {
    Optional<TransactionCategory> findByName(String name);

}
