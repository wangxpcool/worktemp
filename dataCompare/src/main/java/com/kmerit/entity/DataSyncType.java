package com.kmerit.entity;

import lombok.Data;

@Data
public class DataSyncType {

    private String sourcType;//数据库 文件 csv

    private String sourcPath;//如果是 文件 csv 需要文件路径

    private String sql;// 如果是数据库 需要sql语句
    private String db;// 如果是数据库 需要选择数据库查询
    private String tableName;// 如果是数据库 需要表名
    private String tableNameLocal;// 如果是数据库 需要要落入的本地表名(新表名



}
