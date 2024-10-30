package com.kmerit.Service;

import com.kmerit.Service.iml.DataReadFromCsvService;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import com.kmerit.util.SqlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataSyncService {

    @Autowired
    private Map<String, DataReadService> instances;
    @Autowired
    DataReadFromCsvService dataReadFromCsvService;
    @Autowired
    QueryService queryService;

    static Logger logger = LoggerFactory.getLogger(DataSyncService.class);

    public Boolean sync(DataSyncType type) {

        try{
            List<Map<String, Object>> list;
            DataReadService instance = instances.get(type.getSourcType());
            if (instance != null) {
                list = instance.readData(type);
            } else {
                throw new IllegalArgumentException("No such bean: " + type.getSourcType());
            }

            //建表语句
            Map<String, Object> objectMap = list.get(0);
            String sql = SqlGenerator.generateCreateTableSql(objectMap, type.getTableNameLocal());
            System.out.println(sql);
            queryService.createTable(sql,type.getTableNameLocal());
            list.stream().forEach(stringObjectMap -> {
                queryService.syncData(stringObjectMap, type.getTableNameLocal());
            });
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }


        return true;
    }


}
