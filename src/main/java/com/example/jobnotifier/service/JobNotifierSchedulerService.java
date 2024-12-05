package com.example.jobnotifier.service;

import com.example.jobnotifier.dao.JobNotifierDao;
import com.example.jobnotifier.model.JobNotifierEntry;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class JobNotifierSchedulerService {

    private final RestTemplate restTemplate;

    private final JobNotifierDao jobNotifierDao;

    private final String jobListingUrl;

    @Value("${jobnotify.api.jobapplyurl}")
    private String jobApplyUrl;

    private final String mailSubject;

    private final String mailSender;

    private final String SENDGRID_API_KEY;

    public JobNotifierSchedulerService(@Value("${jobnotify.api.joblistingurl}") String jobListingUrl,
                                       RestTemplate restTemplate,
                                       JobNotifierDao jobNotifierDao,
                                       @Value("${jobnotify.mail.subject}") String mailSubject,
                                       @Value("${jobnotify.mail.sender}") String mailSender,
                                       @Value("${SENDGRID_API_KEY}") String SENDGRID_API_KEY)
    {
        this.jobListingUrl = jobListingUrl;
        this.restTemplate = restTemplate;
        this.jobNotifierDao = jobNotifierDao;
        this.mailSubject = mailSubject;
        this.mailSender = mailSender;
        this.SENDGRID_API_KEY = SENDGRID_API_KEY;
    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
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
                    stringBuilder.append("=> ").append(prepareMailText(jobDetailsMap)).append("\n");
                });
    }

    private void processNotifyForEachUser(JobNotifierEntry jobNotifierEntry, JSONArray jsonJobsArr){
        String userEmail = jobNotifierEntry.getEmail();
        // using Set to avoid duplicate keywords
        Set<String> keywordsList = processSingleUserKeywords(jobNotifierEntry);
        StringBuilder stringBuilder = new StringBuilder();

        // reverse iterating as the JSON response is coming in descending job id order
        for(int i = jsonJobsArr.length() - 1; i >= 0; i--){
            JSONObject jsonObject = jsonJobsArr.getJSONObject(i);
            long currentJobId = jsonObject.getLong("id");
            if(currentJobId > jobNotifierEntry.getJobTrackerId()){
                processSingleJob(jsonObject, keywordsList, stringBuilder);
                // updating job tracker to keep track of the latest posted job to avoid sending mail again for each user
                jobNotifierEntry.setJobTrackerId(currentJobId);
            }
        }

        if(stringBuilder.length()>0){
            // sending mail only once match is found
            sendSimpleMail(userEmail,mailSubject, stringBuilder.toString());
        }
        keywordsList.clear();
        stringBuilder.setLength(0);
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
        Email from = new Email(mailSender);
        Email toAddress = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toAddress, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("**********Started Printing Mail Stats***********");
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
            System.out.println("**********Ended Printing Mail Stats***********");
        } catch (IOException ex) {
            System.out.println("error caught");
        }
    }

}
