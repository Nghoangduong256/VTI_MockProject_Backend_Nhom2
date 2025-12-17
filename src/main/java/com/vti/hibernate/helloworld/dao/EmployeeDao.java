package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.Employees;

public interface EmployeeDao {
    public boolean save(Employees employee);
}
