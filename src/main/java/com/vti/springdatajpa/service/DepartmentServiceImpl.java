package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.Department;
import com.vti.springdatajpa.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    // Call repository
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(int id) {
        return departmentRepository.findById(id).get();
    }

    @Override
    public void createDepartment(Department department) {
        departmentRepository.save(department);

    }

    @Override
    public void updateDepartment(Department department) {
        departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(int id) {
        departmentRepository.deleteById(id);

    }

    @Override
    public Page<Department> findByName(String name, Pageable pageable) {
        return departmentRepository.findByName(name, pageable);
    }
}
