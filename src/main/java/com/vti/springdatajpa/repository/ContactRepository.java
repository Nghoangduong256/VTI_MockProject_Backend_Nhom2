package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Contact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByUserId(Integer userId, Pageable pageable);
}
