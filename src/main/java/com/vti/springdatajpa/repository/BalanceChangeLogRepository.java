package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.BalanceChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BalanceChangeLogRepository extends JpaRepository<BalanceChangeLog, Integer> {}