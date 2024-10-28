package com.kmerit.controller;

import com.kmerit.Service.DataSyncService;
import com.kmerit.entity.DataSyncType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/data/sync")
public class DataSyncController {


    @Autowired
    DataSyncService dataSyncService;


    /*
    数据同步接口 可从数据库 或者文件 读取数据入本地库
     */
    @GetMapping("/sync")
    public String sync(DataSyncType type) {

        dataSyncService.sync(type);

        return "data sync successfully";
    }
}
