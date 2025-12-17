package com.vti.hibernate.helloworld.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(schema = "TestingSystem", name = "Jobs")
public class Jobs {

    @Id
    @Column(name = "job_id", length = 10)
    private String jobId;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "min_salary")
    private double minSalary;

    @Column(name = "max_salary")
    private double maxSalary;

    // Lâý  tất cả thông tin của job detail
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL) // apply for all CRUD
    private JobDetails jobDetail;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL) // apply for all CRUD
    // Lấy thông tin tất cả các employee tham gia vào job
    private Set<Employees> employees;

    public Jobs(String jobId) {
        this.jobId = jobId;
    }

    public Jobs(String jobId, String jobTitle, double minSalary, double maxSalary) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(double minSalary) {
        this.minSalary = minSalary;
    }

    public double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(double maxSalary) {
        this.maxSalary = maxSalary;
    }

    public JobDetails getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(JobDetails jobDetail) {
        this.jobDetail = jobDetail;
    }

    public Set<Employees> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employees> employees) {
        this.employees = employees;
    }
}
