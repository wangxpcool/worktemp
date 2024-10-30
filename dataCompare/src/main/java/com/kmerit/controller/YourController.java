package com.kmerit.controller;

import com.kmerit.Service.YourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class YourController {

    @Autowired
    private YourService yourService;

    @GetMapping("/start-task")
    public String startTask() {

        yourService.executeTask();

        return "Tasks started!";
    }
}
