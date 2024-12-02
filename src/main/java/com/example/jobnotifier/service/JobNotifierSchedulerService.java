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

    @Value("${technopark.api.jobapplyurl}")
    private String jobApplyUrl;

    private long jobIdTracker = 0;

    public JobNotifierSchedulerService(JavaMailSender javaMailSender,
                                       @Value("${technopark.api.joblistingurl}") String jobListingUrl,
                                       RestTemplate restTemplate,
                                       JobNotifierDao jobNotifierDao)
    {
        this.javaMailSender = javaMailSender;
        this.jobListingUrl = jobListingUrl;
        this.restTemplate = restTemplate;
        this.jobNotifierDao = jobNotifierDao;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void scheduledTask(){
        System.out.println("**********Started Finding Suitable Jobs***********");
        fetchJobs();
        System.out.println("**********Ended Finding Suitable Jobs*************");
    }

    public void fetchJobs() {
        StringBuilder stringBuilder = new StringBuilder();
        String result = restTemplate.getForObject(jobListingUrl, String.class);
        JSONObject jsonJobObj = new JSONObject(result);
        JSONArray jsonJobsArr = jsonJobObj.getJSONArray("data");
        Map<String,String> jobDetailsMap = new LinkedHashMap<>();
        List<String> keywordsList = new ArrayList<>();
        List<JobNotifierEntry> jobNotifierEntryList = jobNotifierDao.getAllJobNotifierEntry();
        for(JobNotifierEntry jobNotifierEntry : jobNotifierEntryList){
            String userEmail = jobNotifierEntry.getEmail();
            for(int i=0; i<jobNotifierEntry.getKeywords().size(); i++){
                keywordsList.add(jobNotifierEntry.getKeywords().get(i).getKeyword());
            }
            for(Object obj : jsonJobsArr){
                JSONObject jsonObject = (JSONObject) obj;
                long currentJobId = jsonObject.getLong("id");
                if(currentJobId > jobIdTracker){
                    String jobTitle = (String) jsonObject.get("job_title");
                    JSONObject subJsonObj = (JSONObject) jsonObject.get("company");
                    String companyName = (String) subJsonObj.get("company");
                    String closingDate = (String) jsonObject.get("closing_date");
                    String applyLink = jobApplyUrl + currentJobId;
                    keywordsList.stream()
                            .filter(keyword -> jobTitle.toLowerCase().contains(keyword.toLowerCase()))
                            .forEach(keyword -> {
                                jobDetailsMap.put("Job Title",jobTitle);
                                jobDetailsMap.put("Company Name",companyName);
                                jobDetailsMap.put("Closing Date",closingDate);
                                jobDetailsMap.put("Apply Now => ",applyLink);
                                stringBuilder.append("*) ").append(prepareMailText(jobDetailsMap)).append("\n");
                            });
                }
            }
            if(stringBuilder.length()>0){
                sendSimpleMail(userEmail,"Technopark: New Matching Jobs Posted", stringBuilder.toString());
            }
            keywordsList.clear();
            stringBuilder.setLength(0);
        }
        JSONObject jsonObject = (JSONObject) jsonJobsArr.get(0);
        jobIdTracker = (jobIdTracker != jsonObject.getLong("id")) ? jsonObject.getLong("id") : jobIdTracker;
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
