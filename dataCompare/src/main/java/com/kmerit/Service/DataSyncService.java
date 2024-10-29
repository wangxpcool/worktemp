package com.kmerit.Service;

import com.kmerit.Service.iml.DataReadFromCsvService;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataSyncService {

    @Autowired
    private Map<String, DataReadService> instances;
    @Autowired
    DataReadFromCsvService dataReadFromCsvService;
    @Autowired
    QueryService queryService;

    public Boolean sync(DataSyncType type) {
        List<Map<String, Object>> list;
        DataReadService instance = instances.get(type.getSourcType());
        if (instance != null) {
            list = instance.readData(type);
        } else {
            throw new IllegalArgumentException("No such bean: " + type.getSourcType());
        }
        queryService.delete(type.getTableNameLocal());
        list.stream().forEach(stringObjectMap -> {
            queryService.syncData(stringObjectMap,type.getTableNameLocal());
        });

        return true;
    }


}
