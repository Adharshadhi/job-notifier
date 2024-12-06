package com.example.jobnotifier.controller;

import com.example.jobnotifier.model.JobNotifierEntry;
import com.example.jobnotifier.service.JobNotifierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JobNotifierController {

    private final JobNotifierService jobNotifierService;

    private final int maxEmailLimit;

    public JobNotifierController(JobNotifierService jobNotifierService,
                                 @Value("${jobnotify.mail.maxlimit}") int maxEmailLimit){
        this.jobNotifierService = jobNotifierService;
        this.maxEmailLimit = maxEmailLimit;
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

//        check if email id is already registered or not
        JobNotifierEntry jobNotifierEntry = jobNotifierService.checkExistingEntry(userEmail);
//        check if registered mails are less than the max limit or not
        int currentUsersCount = jobNotifierService.checkEmailCount();
        if((jobNotifierEntry == null) && (currentUsersCount < maxEmailLimit)){
            boolean isSuccess = jobNotifierService.saveJobNotifierEntry(userEmail,userKeywords);
            String message = (isSuccess) ? "Email registered successfully!" : "Failed to register email. Please try again.";
            redirectAttributes.addFlashAttribute("isSuccess", isSuccess);
            redirectAttributes.addFlashAttribute("message", message);
        }else{
            redirectAttributes.addFlashAttribute("isSuccess", false);
            if(jobNotifierEntry != null){
                redirectAttributes.addFlashAttribute("message", "Email already registered. Please try with another mail. ");
            } else if (currentUsersCount == maxEmailLimit) {
                redirectAttributes.addFlashAttribute("message", "Max limit reached for registering new users. Please try again later. ");
            }
        }
        return "redirect:/";
    }

    @PostMapping("/deletejobnotifierentry")
    public String deleteJobNotifierEntry(@RequestParam("userEmail") String userEmail, RedirectAttributes redirectAttributes){
        boolean isSuccess = jobNotifierService.deleteJobNotifierEntry(userEmail);
        String message = (isSuccess) ? "Email removed successfully!" : "Failed to remove email. Please try again.";
        redirectAttributes.addFlashAttribute("isSuccess", isSuccess);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/";
    }

    @GetMapping("/error")
    public String showErrorPage(){
        return "error";
    }
}
