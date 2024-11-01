package com.kmerit.controller;

import com.alibaba.druid.util.StringUtils;
import com.kmerit.Service.YourService;
import com.kmerit.config.TaskStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/test")
public class YourController {

    @Autowired
    private YourService yourService;
    @Autowired
    private TaskStatusManager taskStatusManager;

    @GetMapping("/start-task")
    public String startTask(@Validated String jobName) {

        if (StringUtils.isEmpty(jobName)) {
            return "jobName不能为空";
        }
        if (taskStatusManager.isTaskRunning(jobName)) {
            return "Tasks are already running, please wait.";
        }

        taskStatusManager.markTaskRunning(jobName);
        yourService.executeTask(jobName);

        return "Tasks started!";
    }
}
