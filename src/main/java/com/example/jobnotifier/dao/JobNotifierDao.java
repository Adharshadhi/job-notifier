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
        try{
            entityManager.persist(jobNotifierEntry);
            return true;
        }catch (Exception ex){
            System.out.println("Exception :: Persisting object failed :: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteJobNotifierEntry(String userEmail){
        JobNotifierEntry jobNotifierEntry = checkExistingEntry(userEmail);
        if(jobNotifierEntry!=null){
            entityManager.remove(jobNotifierEntry);
            return true;
        }
        return false;
    }

    public List<JobNotifierEntry> getAllJobNotifierEntry(){
        String queryToExecute = "SELECT j FROM JobNotifierEntry j";
        TypedQuery<JobNotifierEntry> query = entityManager.createQuery(queryToExecute, JobNotifierEntry.class);
        return query.getResultList();
    }

    public JobNotifierEntry checkExistingEntry(String userEmail){
        String queryToExecute = "SELECT j FROM JobNotifierEntry j WHERE j.email = :email";
        TypedQuery<JobNotifierEntry> query = entityManager.createQuery(queryToExecute, JobNotifierEntry.class);
        query.setParameter("email", userEmail);
        List<JobNotifierEntry> results = query.getResultList();
        return (results.size() > 0) ? results.get(0) : null;
    }

    public int checkEmailCount(){
        String queryToExecute = "SELECT j FROM JobNotifierEntry j";
        TypedQuery<JobNotifierEntry> query = entityManager.createQuery(queryToExecute, JobNotifierEntry.class);
        return query.getResultList().size();
    }

}
