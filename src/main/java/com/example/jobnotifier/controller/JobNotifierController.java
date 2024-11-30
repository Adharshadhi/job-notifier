package com.example.jobnotifier.controller;

import com.example.jobnotifier.service.JobNotifierService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobNotifierController {

    private final JobNotifierService jobNotifierService;

    public JobNotifierController(JobNotifierService jobNotifierService){
        this.jobNotifierService = jobNotifierService;
    }

    @GetMapping("/")
    public String showNotificationForm(){
        return "index";
    }

    @PostMapping("/savejobnotifierentry")
    public String saveJobNotifierEntry(@RequestParam("userEmail") String userEmail,
                                       @RequestParam("userKeywords") String userKeywords){
        jobNotifierService.saveJobNotifierEntry(userEmail,userKeywords);
        return "redirect:/";
    }

    @PostMapping("/deletejobnotifierentry")
    public String deleteJobNotifierEntry(@RequestParam("userEmail") String userEmail){
        jobNotifierService.deleteJobNotifierEntry(userEmail);
        return "redirect:/";
    }

}
