package com.kmerit.Service;

import com.kmerit.entity.DataSyncType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataSyncService {

    @Autowired
    DataReadService dataReadService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void sync(DataSyncType type) {
        jdbcTemplate.execute("select * from tem");
//        List<Map<String,Object>> list =  dataReadService.readData();


    }


}
