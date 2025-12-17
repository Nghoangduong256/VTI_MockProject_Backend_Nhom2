package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {

    // CRUD
    public List<Department> getAllDepartments();

    public Department getDepartmentById(int id);

    public void createDepartment(Department department);

    public void updateDepartment(Department department);

    public void deleteDepartment(int id);

    Page<Department> findByName(String name, Pageable pageable);
}
