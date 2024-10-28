package com.kmerit.Service;

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
    DataReadService dataReadService;

    @Autowired
    QueryService queryService;

    @Autowired
    private ApplicationContext context;

    DataReadService getInstance(DataSyncType type){
        DataReadService dataReadService = null;
        if ("db".equals(type.get()) ){
            dataReadService = (DataReadService)context.getBean("DataReadFromCsvService");
        }else if("csv".equals(type.getSourcType()) ){
            dataReadService = (DataReadService)context.getBean("DataReadFromCsvService");
        }else{
            return null;
        }
        return dataReadService;
    }

    public Boolean sync(DataSyncType type) {
        DataReadService dataReadService = getInstance(type);
        if (dataReadService==null){
            return false;
        }
        List<Map<String, Object>> list = dataReadService.readData(type);

        return true;
    }


}
