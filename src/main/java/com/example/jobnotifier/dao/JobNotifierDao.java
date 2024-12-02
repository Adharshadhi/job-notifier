package com.example.jobnotifier.dao;

import com.example.jobnotifier.model.JobNotifierEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobNotifierDao {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean saveJobNotifierEntry(JobNotifierEntry jobNotifierEntry){
        entityManager.persist(jobNotifierEntry);
        return true;
    }

    public boolean deleteJobNotifierEntry(String userEmail){
        String queryToExecute = "SELECT j FROM JobNotifierEntry j WHERE j.email = :email";
        TypedQuery<JobNotifierEntry> query = entityManager.createQuery(queryToExecute, JobNotifierEntry.class);
        query.setParameter("email", userEmail);
        entityManager.remove(query.getSingleResult());
        return true;
    }

    public List<JobNotifierEntry> getAllJobNotifierEntry(){
        String queryToExecute = "SELECT j FROM JobNotifierEntry j";
        TypedQuery<JobNotifierEntry> query = entityManager.createQuery(queryToExecute, JobNotifierEntry.class);
        return query.getResultList();
    }

}
