package com.kmerit.Service;

import com.kmerit.config.TaskStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class YourService {

    @Autowired
    private TaskStatusManager taskStatusManager;

    @Async
    public void executeTask(String jobName) {
        // 模拟耗时任务
        try {
            Thread.sleep(2000); // 2秒
            System.out.println("xxx ");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        taskStatusManager.markTaskCompleted(jobName);

        System.out.println("Task executed by: " + Thread.currentThread().getName());
    }

}
