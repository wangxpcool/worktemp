package com.kmerit.Service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class YourService {

    @Async
    public void executeTask() {
        // 模拟耗时任务
        try {
            Thread.sleep(2000); // 2秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Task executed by: " + Thread.currentThread().getName());
    }

}
