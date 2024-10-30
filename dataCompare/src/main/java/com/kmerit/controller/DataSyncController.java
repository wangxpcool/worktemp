package com.kmerit.controller;

import com.kmerit.Service.DataCompareService;
import com.kmerit.Service.DataSyncService;
import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataSyncController {


    @Autowired
    DataSyncService dataSyncService;
    @Autowired
    DataCompareService dataCompareService;

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
    public String compare() {
        //初始化 配置值
        DataSyncType dataSourceSyncA = new DataSyncType();
        dataSourceSyncA.setTableNameLocal("a_flow_a");
        dataSourceSyncA.setTableName("a_flow");
        dataSourceSyncA.setSourcType("dataReadFromDBService");
        dataSourceSyncA.setDb("icbc");
        dataSourceSyncA.setSql("SELECT * FROM a_flow");

        DataSyncType dataSourceSyncB = new DataSyncType();
        dataSourceSyncB.setTableNameLocal("a_flow_b");
        dataSourceSyncB.setTableName("a_flow");
        dataSourceSyncB.setSourcType("dataReadFromDBService");
        dataSourceSyncB.setDb("summit");
        dataSourceSyncB.setSql("SELECT * FROM a_flow");

        DataCompareType compareType = new DataCompareType();
        compareType.setDatasourceA(dataSourceSyncA);
        compareType.setDatasourceB(dataSourceSyncB);
        compareType.setPrimaryKey("id");
        return dataCompareService.compare(compareType);

    }
}
