package com.example.jobnotifier.controller;

import com.example.jobnotifier.model.JobNotifierEntry;
import com.example.jobnotifier.service.JobNotifierService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                       @RequestParam("userKeywords") String userKeywords,
                                       RedirectAttributes redirectAttributes
                                       ){
        JobNotifierEntry jobNotifierEntry = jobNotifierService.checkExistingEntry(userEmail);
        if(jobNotifierEntry == null){
            boolean isSuccess = jobNotifierService.saveJobNotifierEntry(userEmail,userKeywords);
            String message = (isSuccess) ? "Email registered successfully!" : "Failed to register email. Please try again.";
            redirectAttributes.addFlashAttribute("isSuccess", isSuccess);
            redirectAttributes.addFlashAttribute("message", message);
        }else{
            redirectAttributes.addFlashAttribute("isSuccess", false);
            redirectAttributes.addFlashAttribute("message", "Email already registered. Please try with another mail. ");
        }
        return "redirect:/";
    }

    @PostMapping("/deletejobnotifierentry")
    public String deleteJobNotifierEntry(@RequestParam("userEmail") String userEmail){
        jobNotifierService.deleteJobNotifierEntry(userEmail);
        return "redirect:/";
    }

    @GetMapping("/error")
    public String showErrorPage(){
        return "error";
    }
}
