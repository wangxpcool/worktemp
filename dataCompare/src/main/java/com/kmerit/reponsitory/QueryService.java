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
    private final JdbcTemplate thirdJdbcTemplate;

    public QueryService(
            @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate,
            @Qualifier("secondaryJdbcTemplate") JdbcTemplate secondaryJdbcTemplate,
            @Qualifier("thirdJdbcTemplate") JdbcTemplate thirdJdbcTemplate) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.secondaryJdbcTemplate = secondaryJdbcTemplate;
        this.thirdJdbcTemplate = thirdJdbcTemplate;
    }

    public List<Map<String, Object>> getPrimaryData(DataSyncType type) {
        //type.getsql
        return primaryJdbcTemplate.queryForList(type.getSql());
    }

    public List<Map<String, Object>> getSecondaryData(DataSyncType type) {
        return secondaryJdbcTemplate.queryForList(type.getSql());
    }
    public List<Map<String, Object>> getThirdData(DataSyncType type) {
        return thirdJdbcTemplate.queryForList(type.getSql());
    }

    public int syncData(Map<String, Object> map, String tableName) {
        String sql = SqlGenerator.generateInsertSql(map,tableName);
        System.out.println(sql);
        return thirdJdbcTemplate.update(sql);
    }
    public void createTable( String sql,String tableName) {
        thirdJdbcTemplate.update("DROP TABLE IF EXISTS "+tableName);
        thirdJdbcTemplate.update(sql);
    }
    public int delete( String tableName) {
        return thirdJdbcTemplate.update("delete from " + tableName);
    }
}
