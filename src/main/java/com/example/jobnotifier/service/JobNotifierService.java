package com.example.jobnotifier.service;

import com.example.jobnotifier.dao.JobNotifierDao;
import com.example.jobnotifier.model.JobKeyword;
import com.example.jobnotifier.model.JobNotifierEntry;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobNotifierService {

    private final JobNotifierDao jobNotifierDao;

    public JobNotifierService(JobNotifierDao jobNotifierDao){
        this.jobNotifierDao = jobNotifierDao;
    }

    @Transactional
    public boolean saveJobNotifierEntry(String userEmail, String userKeywords){
        String[] keywordsArr = userKeywords.split(",");
        JobNotifierEntry jobNotifierEntry = new JobNotifierEntry();
        jobNotifierEntry.setEmail(userEmail);
        List<JobKeyword> jobKeywordList = new ArrayList<>();
        for(String keyword : keywordsArr){
            JobKeyword jobKeyword = new JobKeyword();
            jobKeyword.setKeyword(keyword.trim());
            jobKeyword.setJobNotifierEntry(jobNotifierEntry);
            jobKeywordList.add(jobKeyword);
        }
        jobNotifierEntry.setKeywords(jobKeywordList);
        return jobNotifierDao.saveJobNotifierEntry(jobNotifierEntry);
    }

    @Transactional
    public boolean deleteJobNotifierEntry(String userEmail){
        jobNotifierDao.deleteJobNotifierEntry(userEmail);
        return true;
    }

    @Transactional
    public JobNotifierEntry checkExistingEntry(String userEmail){
        return jobNotifierDao.checkExistingEntry(userEmail);
    }

}
