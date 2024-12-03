package com.example.jobnotifier.service;

import com.example.jobnotifier.dao.JobNotifierDao;
import com.example.jobnotifier.model.JobNotifierEntry;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class JobNotifierSchedulerService {

    private final RestTemplate restTemplate;

    private final JavaMailSender javaMailSender;

    private final JobNotifierDao jobNotifierDao;

    private final String jobListingUrl;

    @Value("${jobnotify.api.jobapplyurl}")
    private String jobApplyUrl;

    private long jobIdTracker = 0;

    private final String mailSubject;

    public JobNotifierSchedulerService(JavaMailSender javaMailSender,
                                       @Value("${jobnotify.api.joblistingurl}") String jobListingUrl,
                                       RestTemplate restTemplate,
                                       JobNotifierDao jobNotifierDao,@Value("${jobnotify.mail.subject}") String mailSubject)
    {
        this.javaMailSender = javaMailSender;
        this.jobListingUrl = jobListingUrl;
        this.restTemplate = restTemplate;
        this.jobNotifierDao = jobNotifierDao;
        this.mailSubject = mailSubject;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void scheduledTask(){
        System.out.println("**********Started Finding Suitable Jobs***********");
        executeJobNotify();
        System.out.println("**********Ended Finding Suitable Jobs*************");
    }

    private JSONArray fetchJobs(){
        String result = restTemplate.getForObject(jobListingUrl, String.class);
        JSONObject jsonJobObj = new JSONObject(result);
        return jsonJobObj.getJSONArray("data");
    }

    private Set<String> processSingleUserKeywords(JobNotifierEntry jobNotifierEntry){
        Set<String> keywordsList = new HashSet<>();
        for(int i=0; i<jobNotifierEntry.getKeywords().size(); i++){
            keywordsList.add(jobNotifierEntry.getKeywords().get(i).getKeyword());
        }
        return keywordsList;
    }

    private void processSingleJob(JSONObject currentJob, Set<String> keywordsListToMatch, StringBuilder stringBuilder){
        String jobTitle = (String) currentJob.get("job_title");
        String jobTitleLowerCase = jobTitle.toLowerCase();
        JSONObject subJsonObj = (JSONObject) currentJob.get("company");
        String companyName = (String) subJsonObj.get("company");
        String closingDate = (String) currentJob.get("closing_date");
        String applyLink = jobApplyUrl + currentJob.getLong("id");
        Map<String,String> jobDetailsMap = new LinkedHashMap<>();
        keywordsListToMatch.stream()
                .filter(keyword -> jobTitleLowerCase.contains(keyword.toLowerCase()))
                .forEach(keyword -> {
                    jobDetailsMap.put("Job Title",jobTitle);
                    jobDetailsMap.put("Company Name",companyName);
                    jobDetailsMap.put("Closing Date",closingDate);
                    jobDetailsMap.put("Apply Now => ",applyLink);
                    stringBuilder.append("*) ").append(prepareMailText(jobDetailsMap)).append("\n");
                });
    }

    private void processNotifyForEachUser(JobNotifierEntry jobNotifierEntry, JSONArray jsonJobsArr){
        String userEmail = jobNotifierEntry.getEmail();
        // using Set to avoid duplicate keywords
        Set<String> keywordsList = processSingleUserKeywords(jobNotifierEntry);
        StringBuilder stringBuilder = new StringBuilder();
        for(Object obj : jsonJobsArr){
            JSONObject jsonObject = (JSONObject) obj;
            long currentJobId = jsonObject.getLong("id");
            if(currentJobId > jobIdTracker){
                processSingleJob(jsonObject, keywordsList, stringBuilder);
            }
        }
        if(stringBuilder.length()>0){
            sendSimpleMail(userEmail,mailSubject, stringBuilder.toString());
        }
        keywordsList.clear();
        stringBuilder.setLength(0);
    }

    private void updateJobTracker(JSONArray jsonJobsArr){
        JSONObject jsonObject = (JSONObject) jsonJobsArr.get(0);
        jobIdTracker = (jobIdTracker != jsonObject.getLong("id")) ? jsonObject.getLong("id") : jobIdTracker;
    }

    private void executeJobNotify() {
        // Fetching latest jobs posted in Technopark website using API
        JSONArray jsonJobsArr = fetchJobs();
        // Fetching all the registered job notifier details from database
        List<JobNotifierEntry> jobNotifierEntryList = jobNotifierDao.getAllJobNotifierEntry();

        for(JobNotifierEntry jobNotifierEntry : jobNotifierEntryList){
            // Process notifying logic for each entry in the database
            processNotifyForEachUser(jobNotifierEntry,jsonJobsArr);
        }
        // updating job tracker to keep track of the latest posted job to avoid sending mail again
        // needs to implement job tracker id within the database for each user for data consistency
        updateJobTracker(jsonJobsArr);
    }


    private String prepareMailText(Map<String,String> detailsMap){
        StringBuilder mailText = new StringBuilder();
        for(Map.Entry<String,String> entry : detailsMap.entrySet()){
            mailText.append(entry.getKey())
                    .append(" - ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return mailText.toString();
    }

    private void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("random@gmail.com");
        javaMailSender.send(message);
    }

}
