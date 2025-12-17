package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.JobDetails;
import com.vti.hibernate.helloworld.entities.Jobs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobsTest {

    static JobDao jobDao;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        jobDao = new JobDaoImpl();
    }

    @Test
    void testSave1() throws Exception {
        // Add Job
        // dùng contructor
        Jobs job = new Jobs("J02", "Java Dev1", 1000.01, 2000.01);

        // Add job detail
        JobDetails jobDetails = new JobDetails();
        // Không dùng contructor
        jobDetails.setJobDescription("java developer 1");
        jobDetails.setActiveDate(LocalDate.of(2025, 8, 18));

        job.setJobDetail(jobDetails);
        jobDetails.setJob(job);


        assertEquals(true, jobDao.save(job));
    }

    @Test
    void testSave2() throws Exception {
        Jobs job = new Jobs("J02", "Java Dev2", 1200, 2200);
        assertEquals(true, jobDao.save(job));
    }

}