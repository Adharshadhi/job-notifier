package com.example.jobnotifier.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "useralerts")
public class JobNotifierEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

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
                ", keywords=" + keywords +
                '}';
    }

}
