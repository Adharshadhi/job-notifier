package com.example.jobnotifier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "useralerts")
public class JobNotifierEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "jobtracker_id", nullable = false)
    private long jobTrackerId;

    @OneToMany(mappedBy = "jobNotifierEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobKeyword> keywords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getJobTrackerId() {
        return jobTrackerId;
    }

    public void setJobTrackerId(long jobTrackerId) {
        this.jobTrackerId = jobTrackerId;
    }

    public List<JobKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<JobKeyword> keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "JobNotifierEntry{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", jobTrackerId='" + jobTrackerId + '\'' +
                ", keywords=" + keywords +
                '}';
    }

}
