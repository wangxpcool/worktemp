package com.kmerit.config;


import com.alibaba.druid.util.StringUtils;
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
        if(StringUtils.isEmpty(taskId)){
            return true;
        }
        return taskStatus.containsKey(taskId);
    }

    public void markTaskRunning(String taskId) {

        taskStatus.put(taskId, true);

    }

    public void markTaskCompleted(String taskId) {
        taskStatus.remove(taskId);
    }
}
