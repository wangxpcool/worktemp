package com.kmerit.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class CreateNewTableUtil {
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//172.16.10.66:1521/ORCL", "kmipcloud", "kmipcloud");
        List<Map<String, String>> dataMapList = new ArrayList<>();
        dataMapList.add(createDataMap("1"));
        dataMapList.add(createDataMap("2"));
        dataMapList.add(createDataMap("3"));
        dataMapList.add(createDataMap("4"));
        dataMapList.add(createDataMap("5"));

        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream("F:/calypso17.23.12.1/custom-extensions/custom-projects/tools/src/resource/setting.property");
        properties.load(fis);

        dropAndCreateTable(connection, dataMapList, properties);

    }

    /**
     * 创建数据做好格式化的数据库表
     *
     * @param dataMapList
     * @param properties
     */
    private static void dropAndCreateTable(Connection connection, List<Map<String, String>> dataMapList, Properties properties) {
        //获取配置
        String oldTableName = properties.getProperty("target.table.old_name");
        String newTableName = properties.getProperty("target.table.new_name");
        String columnNameSort = properties.getProperty("target.column_name.sort");
        String primaryKey = properties.getProperty("target.column_name.primary_key");
        String[] columnNameSortArr = columnNameSort.split(",");
        String[] primaryKeyArr = primaryKey.split(",");
        //计算每个字段需要的长度
        Map<String, Integer> maxColumnLengthMap = countMaxColumnLength(dataMapList);
        //删表
        if(!dropTable(connection, oldTableName)){
            System.out.println("删表失败，程序终止");
            return;
        }
        //获取建表语句
        String createTableSQL = buildSql(newTableName, primaryKeyArr, columnNameSortArr, maxColumnLengthMap);

        //建表
        if(!createTable(connection, createTableSQL)){
            System.out.println("建表失败，程序终止");
            return;
        }

    }

    /**
     * 测试 出创建数据List的工具方法，可忽略
     *
     * @param diff
     * @return
     */
    private static Map<String, String> createDataMap(String diff) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("trade_id", "tradeid" + diff);
        dataMap.put("product_id", "productid" + diff);
        dataMap.put("trade_type", "tradetype" + diff);
        dataMap.put("user", "user" + diff);
        dataMap.put("date_time", "datetime" + diff);
        return dataMap;
    }

    /**
     * 对数据库所有字段内容进行长度计数
     *
     * @param dataMapList 数据map
     * @return 字段最大长度的map
     */
    private static Map<String, Integer> countMaxColumnLength(List<Map<String, String>> dataMapList) {
        Map<String, Integer> maxColumnLengthMap = new HashMap<>();
        for (Map<String, String> dataMap : dataMapList) {
            for (String columnName : dataMap.keySet()) {
                if (maxColumnLengthMap.containsKey(columnName)) {
                    maxColumnLengthMap.put(columnName, Math.max(dataMap.get(columnName).length(), maxColumnLengthMap.get(columnName)));
                } else {
                    maxColumnLengthMap.put(columnName, dataMap.get(columnName).length());
                }
            }
        }
        return maxColumnLengthMap;
    }


    //返回建表语句
    private static String buildSql(String tableName, String[] primaryKeyArr, String[] columnNameSortArr, Map<String, Integer> maxColumnLengthMap) {
        StringBuffer columnNameSql = new StringBuffer();
        for (int i = 0; i < columnNameSortArr.length; i++) {
            columnNameSql.append(columnNameSortArr[i] + " VARCHAR(" + maxColumnLengthMap.get(columnNameSortArr[i]) + "),");
        }
        StringJoiner primaryKeySql = new StringJoiner(",");
        for (int i = 0; i < primaryKeyArr.length; i++) {
            primaryKeySql.add(primaryKeyArr[i]);
        }
        String createSql = "CREATE TABLE " + tableName + " (" + columnNameSql +
                " CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + primaryKeySql + "))";
        return createSql;
    }

    private static boolean dropTable(Connection connection, String tableName) {
        String checkQuery = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER(?)";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, tableName);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // 如果表存在，执行删除操作
                    String dropQuery = "DROP TABLE " + tableName;
                    try (PreparedStatement dropStmt = connection.prepareStatement(dropQuery)) {
                        dropStmt.executeUpdate();
                        System.out.println("表 " + tableName + " 已删除。");
                    }
                } else {
                    System.out.println("表 " + tableName + " 不存在。");
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean createTable(Connection connection, String createTableSql) {
        try (PreparedStatement createTableStmt = connection.prepareStatement(createTableSql)) {
            try (ResultSet resultSet = createTableStmt.executeQuery()) {
                System.out.println("成功建表");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

