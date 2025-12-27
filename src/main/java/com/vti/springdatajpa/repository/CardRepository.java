package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findByUserId(Integer userId);
}
