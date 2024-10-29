package com.kmerit.reponsitory;


import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import com.kmerit.util.SqlGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    private final JdbcTemplate primaryJdbcTemplate;
    private final JdbcTemplate secondaryJdbcTemplate;

    public QueryService(
            @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate,
            @Qualifier("secondaryJdbcTemplate") JdbcTemplate secondaryJdbcTemplate) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.secondaryJdbcTemplate = secondaryJdbcTemplate;
    }

    public List<Map<String, Object>> getPrimaryData(DataSyncType type) {
        //type.getsql
        return primaryJdbcTemplate.queryForList("SELECT * FROM a_flow");
    }

    public List<Map<String, Object>> getSecondaryData(DataSyncType type) {
        return secondaryJdbcTemplate.queryForList("SELECT * FROM secondary_table");
    }
    public int syncData(Map<String, Object> map) {
        String sql = SqlGenerator.generateInsertSql(map);
        System.out.println(sql);
        return secondaryJdbcTemplate.update(sql);
    }
}
