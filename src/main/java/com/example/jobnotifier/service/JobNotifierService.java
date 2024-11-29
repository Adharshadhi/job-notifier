package com.example.jobnotifier.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobNotifierService {

    private final JavaMailSender javaMailSender;

    private final String url;

    private final RestTemplate restTemplate;

    private final List<String> jobKeywords = List.of(
            "java", "software", "developer", "backend", "fullStack",
            "full stack", "engineer", "back end", "spring",
            "jee", "j2ee"
    );

    private long jobIdTracker = 0;

    public JobNotifierService(JavaMailSender javaMailSender,
                              @Value("${technopark.api.url}") String url,
                              RestTemplate restTemplate
                              )
    {
        this.javaMailSender = javaMailSender;
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public void fetchJobs() {
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonJobObj = new JSONObject(result);
        JSONArray jsonJobsArr = jsonJobObj.getJSONArray("data");
        List<String> stringList = new ArrayList<>();
        for(Object obj : jsonJobsArr){
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.getLong("id") > jobIdTracker){
                String jobTitle = (String) jsonObject.get("job_title");
                jobKeywords.stream()
                        .filter(job -> jobTitle.toLowerCase().contains(job))
                        .forEach(job -> stringList.add(jobTitle));
            }
        }
        sendSimpleMail("random123@gmail.com","Technopark: New Jobs Posted", prepareMailText(stringList));
        JSONObject jsonObject = (JSONObject) jsonJobsArr.get(0);
        jobIdTracker = (jobIdTracker != jsonObject.getLong("id")) ? jsonObject.getLong("id") : jobIdTracker;
    }

    public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("random@gmail.com");
        javaMailSender.send(message);
    }

    @Scheduled(fixedRate = 60000)
    public void scheduledTask(){
        System.out.println("**********Started Finding Suitable Jobs***********");
        fetchJobs();
        System.out.println("**********Ended Finding Suitable Jobs*************");
    }

    public String prepareMailText(List<String> stringList){
        StringBuilder mailText = new StringBuilder();

        for(int i=0; i<stringList.size(); i++){
            mailText.append(i+1)
                    .append(". ")
                    .append(stringList.get(i))
                    .append("\n");
        }

        return mailText.toString();
    }

}
