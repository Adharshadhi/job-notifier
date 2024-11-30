package com.example.jobnotifier.dao;

import com.example.jobnotifier.model.JobNotifierEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class JobNotifierDao {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean saveJobNotifierEntry(JobNotifierEntry jobNotifierEntry){
        entityManager.persist(jobNotifierEntry);
        return true;
    }

}
