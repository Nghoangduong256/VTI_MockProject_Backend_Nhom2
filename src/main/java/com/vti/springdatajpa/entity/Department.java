package com.vti.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DepartmentID")
    private  int id;

    @Column(name = "DepartmentName")
    private String name;

    @Column(name = "TotalMember")
    private int totalMember;

    // get, setter => change by lombok


}
