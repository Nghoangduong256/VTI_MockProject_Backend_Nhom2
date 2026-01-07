package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.CardDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardDepositRepository extends JpaRepository<CardDeposit, Integer> {
    List<CardDeposit> findByUserIdOrderByTimestampDesc(Integer userId);
}
