package com.vti.springdatajpa.repository;

import com.vti.springdatajpa.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository  extends JpaRepository<Department, Integer> {

    // Paging = method name
    Page<Department> findByName(String name, Pageable pageable);

}
