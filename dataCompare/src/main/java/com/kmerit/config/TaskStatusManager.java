package com.kmerit.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskStatusManager {

    private final ConcurrentHashMap<String, Boolean> taskStatus;

    @Autowired
    public TaskStatusManager(ConcurrentHashMap<String, Boolean> taskStatus) {
        this.taskStatus = taskStatus;
    }

    public boolean isTaskRunning(String taskId) {
        System.out.println(taskStatus.size());
        return taskStatus.containsKey(taskId);
    }

    public void markTaskRunning(String taskId) {
        System.out.println(taskStatus.size());

        taskStatus.put(taskId, true);
        System.out.println(taskStatus.size());

    }

    public void markTaskCompleted(String taskId) {
        System.out.println(taskStatus.size());

        taskStatus.remove(taskId);
        System.out.println(taskStatus.size());

    }
}
