package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Contact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findByUserId(Integer userId, Pageable pageable);
}
