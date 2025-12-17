package com.vti.hibernate.helloworld.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "job_details")
public class JobDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_detail_id")
    private int jobDetailId;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "active_date")
    private LocalDate activeDate;

    // config one to one with job
    @OneToOne
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    private Jobs job;

    // getter, setter

    public int getJobDetailId() {
        return jobDetailId;
    }

    public void setJobDetailId(int jobDetailId) {
        this.jobDetailId = jobDetailId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public LocalDate getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(LocalDate activeDate) {
        this.activeDate = activeDate;
    }

    public Jobs getJob() {
        return job;
    }

    public void setJob(Jobs job) {
        this.job = job;
    }
}
