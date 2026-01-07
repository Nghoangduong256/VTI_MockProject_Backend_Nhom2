package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.CardWithdraw;
import com.vti.springdatajpa.entity.enums.CardWithdrawStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CardWithdrawRepository extends JpaRepository<CardWithdraw, Integer> {
    
    List<CardWithdraw> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    List<CardWithdraw> findByCardIdOrderByCreatedAtDesc(Integer cardId);
    
    List<CardWithdraw> findByUserIdAndStatusOrderByCreatedAtDesc(Integer userId, CardWithdrawStatus status);
    
    List<CardWithdraw> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Integer userId, LocalDateTime startDate, LocalDateTime endDate
    );
}
