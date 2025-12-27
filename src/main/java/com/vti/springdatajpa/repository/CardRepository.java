package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByUserId(Integer userId);
}
