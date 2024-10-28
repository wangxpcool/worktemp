package com.kmerit.controller;

import com.kmerit.Service.DataCompareService;
import com.kmerit.entity.DataCompareType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

@RestController("/")
public class RootController {


    @Autowired
    DataCompareService dataCompareService;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /*
        数据对比接口 对比两方数据 并将结果输出
     */
    @GetMapping("/")
    public String sendMessage(DataCompareType type) {

        threadPoolExecutor.execute(null);
        dataCompareService.compare(type);

        return "Message sent successfully";
    }
}
