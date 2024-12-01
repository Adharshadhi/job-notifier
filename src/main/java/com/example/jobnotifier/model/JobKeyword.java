package com.example.jobnotifier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "userkeywords")
public class JobKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private JobNotifierEntry jobNotifierEntry;

    @NotBlank(message = "Keyword cannot be blank")
    @Size(max = 100, message = "Keyword must not exceed 100 characters")
    @Column(name = "keyword", nullable = false)
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
