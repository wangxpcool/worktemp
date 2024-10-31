package com.kmerit.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SqlGenerator {


    public static String generateCreateTableSql(Map<String, Object> data, String tableName) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(tableName).append("(\n");
        for (String key : data.keySet()) {
            //todo 这里可能还要根据配置去判断
            String columnType = key.equals("create_time") ? "DATETIME" : "VARCHAR(255)";
            sql.append("    ").append(key).append(" ").append(columnType).append(",\n");
        }

        // Remove the last comma and newline
        sql.setLength(sql.length() - 2);
        sql.append("\n);");

        return sql.toString();
    }

    public static String generateInsertSql(Map<String, Object> data, String tableName) {

        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO `").append(tableName).append("` (");

        // 添加字段
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 特殊处理 create_time 字段
//            if ("create_time".equals(key) && value instanceof LocalDateTime) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                value = ((LocalDateTime) value).format(formatter); // 格式化 LocalDateTime
//            }
            // 添加字段
            fields.append("`").append(key).append("`, ");

            // 添加值，注意对字符串的处理
            if (value instanceof String) {
                values.append("'").append(value).append("', ");
            } else if (value instanceof BigDecimal) {
                values.append("'").append((value).toString()).append("', ");
            } else if (value instanceof Timestamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                values.append("'").append(sdf.format(value)).append("', ");
            } else if (value instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                values.append("'").append(sdf.format(value)).append("', ");
            } else {
                values.append(value).append(", ");
            }
        }

        // 去除最后的逗号和空格
        fields.setLength(fields.length() - 2);
        values.setLength(values.length() - 2);

        sql.append(fields).append(") VALUES (").append(values).append(");");

        return sql.toString();
    }

    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", "019");
        data.put("create_time", "2024-10-29T10:08:08");
        data.put("create_by", "user");
        data.put("amount", 1.00);
        data.put("sell", "xx");
        data.put("buy", "yy");
        System.out.println(data);
        // 这里可以调用之前的 generateInsertSql 方法
        String sql = generateInsertSql(data, "a_flow");
//        String sql = generateCreateTableSql(data,"a_flow");
        System.out.println(sql);
    }
}
