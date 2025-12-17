package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.Employees;
import com.vti.hibernate.helloworld.entities.Jobs;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeDaoTest {

    @Test
    void testSave() {
        EmployeeDao employeeDao = new EmployeeDaoImpl();

        // Get job id J02 đã tạo truuốc đó
        Jobs job = new Jobs("J02", "Java Dev1", 1000.01, 2000.01);

        // Create employee
        // sủ dụng contructor
        Employees employee = new Employees("Nguyen",
                "Quang Annh", "quanganh@gmail.com", "098111111",
                LocalDate.of(2025, 8, 18), 1000, 1.1,
                job
        );


        // test
        assertTrue(employeeDao.save(employee));

    }
}