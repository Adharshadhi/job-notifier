package com.example.jobnotifier.error;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex){
        System.out.println("Exception caught: -> " + ex.getMessage());
        return new ModelAndView("error");
    }

}
