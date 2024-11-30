package com.example.jobnotifier.model;

import jakarta.persistence.*;

@Entity
@Table(name = "userkeywords")
public class JobKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private JobNotifierEntry jobNotifierEntry;

    @Column(name = "keyword")
    private String keyword;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JobNotifierEntry getJobNotifierEntry() {
        return jobNotifierEntry;
    }

    public void setJobNotifierEntry(JobNotifierEntry jobNotifierEntry) {
        this.jobNotifierEntry = jobNotifierEntry;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "JobKeyword{" +
                "id=" + id +
                ", jobNotifierEntry=" + jobNotifierEntry +
                ", keyword='" + keyword + '\'' +
                '}';
    }

}
