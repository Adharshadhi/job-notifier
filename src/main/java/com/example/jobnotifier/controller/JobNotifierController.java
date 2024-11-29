package com.example.jobnotifier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JobNotifierController {

    @GetMapping("/")
    public String showNotificationForm(){
        return "index";
    }

}
