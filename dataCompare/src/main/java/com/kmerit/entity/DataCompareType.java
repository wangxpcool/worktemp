package com.kmerit.entity;

import lombok.Data;

@Data
public class DataCompareType {

    private String sourceType;//数据库 文件 crv
    private Integer page;
    private Integer pageSize;
    private String compareName;
    private String compareConfigIndex;
    private String primaryKey;//后面可能变成多个
    private String outputFilePath;
    DataSyncType datasourceA;
    DataSyncType datasourceB;


}
