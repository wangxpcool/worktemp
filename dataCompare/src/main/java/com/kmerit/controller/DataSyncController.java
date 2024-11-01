package com.kmerit.controller;

import com.alibaba.druid.util.StringUtils;
import com.kmerit.Service.DataCompareService;
import com.kmerit.Service.DataSyncService;
import com.kmerit.config.TaskStatusManager;
import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/data")
public class DataSyncController {


    @Autowired
    DataSyncService dataSyncService;
    @Autowired
    DataCompareService dataCompareService;

    @Autowired
    private TaskStatusManager taskStatusManager;

    /*
    数据同步接口 可从数据库 或者文件 读取数据入本地库
     */
    @GetMapping("/sync")
    public String sync() {
        DataSyncType type = new DataSyncType();
        type.setTableNameLocal("a_flow");
        type.setSourcType("dataReadFromDBService");
//        type.setSourcType("dataReadFromCsvService");
        Boolean result = dataSyncService.sync(type);

        return "data sync successfully";
    }

    /*
    数据比对
     */
    @GetMapping("/compare")
    public String compare(String jobName) {
        if (StringUtils.isEmpty(jobName)) {
            return "jobName不能为空";

        }
        if (taskStatusManager.isTaskRunning(jobName)) {
            return "Tasks are already running, please wait.";
        }
        taskStatusManager.markTaskRunning(jobName);

        //初始化 配置值 之后要根据jobName读取配置文件
        DataSyncType dataSourceSyncA = new DataSyncType();
        dataSourceSyncA.setTableNameLocal("trade_info_a");
        dataSourceSyncA.setTableName("trade_info");
        dataSourceSyncA.setSourcType("dataReadFromCsvService");
        dataSourceSyncA.setSourcPath("C:\\Users\\sharping\\Desktop\\trade_info.csv");
//        dataSourceSyncA.setDb("icbc");
//        dataSourceSyncA.setSql("SELECT * FROM a_flow");

        DataSyncType dataSourceSyncB = new DataSyncType();
        dataSourceSyncB.setTableNameLocal("trade_info_b");
        dataSourceSyncB.setTableName("trade_info");
        dataSourceSyncB.setSourcType("dataReadFromDBService");
        dataSourceSyncB.setDb("summit");
        dataSourceSyncB.setSql("SELECT * FROM trade_info");

        DataCompareType compareType = new DataCompareType();
        compareType.setDatasourceA(dataSourceSyncA);
        compareType.setDatasourceB(dataSourceSyncB);
        compareType.setPrimaryKey("id");
        compareType.setOutputFilePath("C:\\Users\\sharping\\Desktop\\tmp\\"+jobName+".txt");
        return dataCompareService.compare(compareType, jobName);

    }
}
