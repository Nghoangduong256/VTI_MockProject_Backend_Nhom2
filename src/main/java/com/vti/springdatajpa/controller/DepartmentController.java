package com.vti.springdatajpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/departments")
public class DepartmentController {

//    // Call service
//    @Autowired
//    private DepartmentService departmentService;
//
//    // get all department
//    @GetMapping
//    public List<Department> getAllDepartments() {
//        return departmentService.getAllDepartments();
//    }
//
//    // Create, update, delete, find by ID
//    // TODO, để cả lớp tập thêm, test = postman
//
//    // Paging
//    // url: localhost:8080/api/v1/category/search?name=test&sortField=id&sortDir=desc&page=0&size=5
//    // type get
//    @GetMapping("/search")
//    public Page<Department> getAllDepartmentsByName(
//            @RequestParam(name = "name", required = false) String queryName,
//            Pageable pageable) {
//        return departmentService.findByName(queryName, pageable);
//    }

}
