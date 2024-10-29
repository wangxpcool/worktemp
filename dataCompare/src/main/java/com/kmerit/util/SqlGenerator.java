package com.kmerit.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SqlGenerator {

    public static String generateInsertSql(Map<String, Object> data) {
        String tableName = "a_flow";
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO `").append(tableName).append("` (");

        // 添加字段
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 特殊处理 create_time 字段
            if ("create_time".equals(key) && value instanceof LocalDateTime) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                value = ((LocalDateTime) value).format(formatter); // 格式化 LocalDateTime
            }
            // 添加字段
            fields.append("`").append(key).append("`, ");

            // 添加值，注意对字符串的处理
            if (value instanceof String) {
                values.append("'").append(value).append("', ");
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
        String sql = generateInsertSql(data);
        System.out.println(sql);
    }
}
