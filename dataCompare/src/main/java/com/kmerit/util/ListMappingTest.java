//package com.kmerit.util;
//
//import oracle.sql.*;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.Date;
//
///**
// * @author: PYZ
// * @program: data_migration_JTYH
// * @create: 2024-10-28 18:03
// * @Description:
// */
///**
// * 1,接收List<Map<String,Object>>
// * 2,对List进行格式化，格式化具体内容支持可配置
// *  可配置：具体为可选择哪些字段保留小数点后几位，最后全部格式化为List<Map<String,String>>方便进行比对，列名全部小写
// * 3,最后需要有创建表，存库的过程
// *
// * csv文件
// * 1,存库是不是高斯库，关于拿connection的方法
// * 直接从数据库连接池拿，会有工具方法
// * 先drop表，再新建
// * 2，可配置传进来的参数
// * 固定传入的格式
// * 列名+保留几位小数
// *
// * 1，从配置中读取sql
// * 2，根据sql中的表名，字段及格式化要求，进行格式化
// * 3，最后将格式化后的数据存入库中（oracle为例）
// *
// *
// *4,还需要一个将sql中表都连接起来的方法
// */
//public class ListMappingTest {
//    private static String url ="jdbc:oracle:thin:@//172.16.10.66:1521/ORCL";
//    private static String username = "kmipcloud";
//    private static String password = "kmipcloud";
//
//
//    public static void main(String[] args) {
//
//        Properties props = new Properties();
//        try (InputStream inputStream = ListMappingTest.class.getClassLoader().getResourceAsStream("config.properties")) {
//            if (inputStream!= null) {
//                props.load(inputStream);
//
//                String tablePrefix = props.getProperty("source");
//                List<Map<String, Object>> icbcQueryResult = queryDataFromProperties(props);
//                List<Map<String, String>> icbcFormattedList = convertToMapWithStrings(icbcQueryResult,props,tablePrefix);
////                saveDataToOracle(icbcFormattedList, "SYS_ICBC");
//
//            } else {
//                throw new RuntimeException("无法找到数据库配置文件 database.properties");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("加载数据库配置文件失败");
//        }
//    }
//
//
//    /**
//     * 通过properties文件获取到sql和表名
//     * @param properties
//     * @return
//     */
//    private static List<Map<String, Object>> queryDataFromProperties(Properties properties) {
//        for (String key : properties.stringPropertyNames()) {
//            if (key.startsWith("icbc_") || key.startsWith("summit_")&&key.endsWith("_sql")) {
//                String sql = properties.getProperty(key);
//                //获取前缀
//                String tablePrefix = key.substring(0, key.indexOf('_')).toLowerCase();
//                String tableName = key.substring(key.indexOf('_') + 1);
//                System.out.println("tablePrefix="+tablePrefix+",tableName="+tableName+",sql="+sql);
//                List<Map<String, Object>> dataList = executeQuery(sql, tablePrefix, tableName);
//                return dataList;
//            }
//        }
//        return null;
//    }
//
//
//    /**
//     *  执行sql，将获取到的数据存到List<Map<String,Object>里
//     * @param sql
//     * @param tablePrefix
//     * @param tableName
//     * @return
//     */
//    private static List<Map<String, Object>> executeQuery(String sql, String tablePrefix, String tableName) {
//        List<Map<String, Object>> resultList = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//
//            try (ResultSet resultSet = statement.executeQuery()) {
//                int columnCount = resultSet.getMetaData().getColumnCount();
//                ResultSetMetaData metaData = resultSet.getMetaData();
//
//                while (resultSet.next()) {
//                    Map<String, Object> rowMap = new HashMap<>();
//                    for (int i = 1; i <= columnCount; i++) {
//                        String columnName = metaData.getColumnName(i);
//                        Object value = resultSet.getObject(columnName);
//                        rowMap.put(columnName, value);
//                        System.out.println(tableName+"column"+i+columnName+"="+value);
//                    }
//                    resultList.add(rowMap);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return resultList;
//    }
//
//    /**
//     * 将数据进行格式化储存
//     * 1,传入List<Map<String,Object>>
//     * 2,遍历List获取到字段名和值,字段名取到之后需要全部小写
//     * 3,根据获取到的字段名去properties里 取到对应的String
//     * 4,对String进行分割，对第一个参数类型进行判断，根据参数给的类型进行格式化
//     * 5,格式化之后存为List<Map<String,String>>,新存的字段名取自第二个参数（判断第二个参数是否存在 可以用三元表达式
//     * @param inputList
//     * @return
//     */
//    public static List<Map<String, String>> convertToMapWithStrings(List<Map<String, Object>> inputList,Properties properties,String tablePrefix) {
//
//        List<Map<String, String>> resultList = new ArrayList<>();
//        try {
//            for (Map<String, Object> map : inputList) {
//                Map<String, String> newMap = new HashMap<>();
//
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    String columnName = entry.getKey();
//                    //从List中拿到的值
//                    Object value = entry.getValue();
//                    String propertyValue = properties.getProperty("source." + tablePrefix + columnName);
//
//                    if (propertyValue != null) {
//                        switch (getDataType(propertyValue)) {
//                            case "CHAR":
//                                if (value instanceof CHAR) {
//                                    newMap.put(columnName, ((CHAR) value).stringValue());
//                                } else {
//                                    newMap.put(columnName, value == null ? null : value.toString());
//                                }
//                                break;
//                            case "NUMBER":
//                                if (value instanceof NUMBER) {
//                                    if (propertyValue.startsWith("DOUBLE(")) {
//                                        int precision = Integer.parseInt(propertyValue.substring(propertyValue.indexOf('(') + 1, propertyValue.indexOf(')')));
//                                        BigDecimal bigDecimalValue = new BigDecimal(((NUMBER) value).doubleValue());
//                                        newMap.put(columnName, bigDecimalValue.setScale(precision, BigDecimal.ROUND_HALF_UP).toString());
//                                    } else {
//                                        newMap.put(columnName, ((NUMBER) value).toString());
//                                    }
//                                } else {
//                                    newMap.put(columnName, value == null ? null : value.toString());
//                                }
//                                break;
//                            case "RAW":
//                                newMap.put(columnName, value == null ? null : value.toString());
//                                break;
//                            case "DATE":
//                                if (value instanceof DATE) {
//                                    SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat(propertyValue));
//                                    newMap.put(columnName, sdf.format(((DATE) value).dateValue()));
//                                } else {
//                                    newMap.put(columnName, value == null ? null : value.toString());
//                                }
//                                break;
//                            case "TIMESTAMP":
//                                if (value instanceof TIMESTAMP) {
//                                    SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat(propertyValue));
//                                    newMap.put(columnName, sdf.format(((TIMESTAMP) value).timestampValue()));
//                                } else {
//                                    newMap.put(columnName, value == null ? null : value.toString());
//                                }
//                                break;
//                            case "BLOB":
//                                newMap.put(columnName, value == null ? null : value.toString());
//                                break;
//                            default:
//                                newMap.put(columnName, value == null ? null : value.toString());
//                        }
//                    } else {
//                        newMap.put(columnName, value == null ? null : value.toString());
//                    }
//                }
//                resultList.add(newMap);
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return resultList;
//
//
//    }
//
//    /**
//     * 判断DataType种类
//     * @param propertyValue
//     * @return
//     */
//    private static String getDataType(String propertyValue) {
//        if (propertyValue.startsWith("CHAR")) {
//            return "CHAR";
//        }else if (propertyValue.startsWith("NUMBER")) {
//            return "NUMBER";
//        }else if (propertyValue.startsWith("RAW")) {
//            return "RAW";
//        }else if (propertyValue.startsWith("DATE")) {
//            return "DATE";
//        }else if (propertyValue.startsWith("TIMESTAMP")) {
//            return "TIMESTAMP";
//        }else if (propertyValue.startsWith("BLOB")) {
//            return "BLOB";
//        }
//        return null;
//    }
//
//    /**
//     * 进行日期类型format
//     * @param propertyValue
//     * @return
//     */
//    private static String getDateFormat(String propertyValue) {
//        if (propertyValue.startsWith("DATE(")) {
//            return propertyValue.substring(propertyValue.indexOf('(') + 1, propertyValue.indexOf(')'));
//        }
//        if (propertyValue.startsWith("TIMESTAMP(")) {
//            return propertyValue.substring(propertyValue.indexOf('(') + 1, propertyValue.indexOf(')'));
//        }
//        return null;
//    }
//
//}
//
//
//
////
////public class ListMappingTestt {
////    public static void main(String[] args) {
////        Properties properties = new Properties();
////        try (InputStream inputStream = ListMappingTestt.class.getClassLoader().getResourceAsStream("config.properties")) {
////            //一张表的配置文件
////            if (inputStream != null) {
////                properties.load(inputStream);
////                //
////
////                List<Map<String, Object>> icbcQueryResult = queryDataFromProperties(properties, "icbc");
////                List<Map<String, String>> icbcFormattedList = formatResult(icbcQueryResult);
////                saveDataToOracle(icbcFormattedList, "SYS_ICBC");
////
////            } else {
////                System.out.println("无法找到配置文件。");
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
////
////    private static List<Map<String, Object>> queryDataFromProperties(Properties properties, String prefix) {
////
////        List<Map<String, Object>> dataList = new ArrayList<>();
////        for (int i = 1; true; i++) {
////            String sql = properties.getProperty(prefix + "Sql" + i);
////            System.out.println(sql);
////            if (sql == null) {
////                break;
////            }
////            try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//172.16.10.66:1521/ORCL", "kmipcloud", "kmipcloud");
////
////                 Statement statement = connection.createStatement();
////
////                 ResultSet resultSet = statement.executeQuery(sql)) {
////
////                while (resultSet.next()) {
////                    Map<String, Object> rowMap = new HashMap<>();
////
////                    ResultSetMetaData metaData = resultSet.getMetaData();
////
////                    int columnCount = metaData.getColumnCount();
////
////                    for (int j = 1; j <= columnCount; j++) {
////
////                        String columnName = metaData.getColumnName(j);
//////                        System.out.println("icbc"+j+"columnName is"+columnName);
////                        rowMap.put(columnName, resultSet.getObject(columnName));
//////                        System.out.println("icbc"+j+"value is"+resultSet.getObject(columnName));
////                    }
////                    dataList.add(rowMap);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////        return dataList;
////    }
////
////
////
////    private static List<Map<String, String>> formatResult(List<Map<String, Object>> queryResult) {
////        //返回的list
////        List<Map<String, String>> formattedList = new ArrayList<>();
////        for (Map<String, Object> row : queryResult) {
////            Map<String, String> formattedRow = new HashMap<>();
////            for (Map.Entry<String, Object> entry : row.entrySet()) {
////                int i = 0;
////                String columnName = entry.getKey();
////                Object value = entry.getValue();
////                // 解析格式化 SQL，这里只是一个简单示例，假设格式为 "TO_CHAR({column})"
////                //主要处理四种类型
////                //整数型，浮点型，日期，字符串.
////                if (value instanceof oracle.sql.NUMBER) {
////                    value =  String.valueOf((Number) value);
////                } else if (value instanceof Boolean) {
////                    value =  String.valueOf(value);
////                } else if (value instanceof Date || value instanceof Timestamp) {
////                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////                    value =  sdf.format((Date) value);
////                } else if (value instanceof String) {
////                    value =  (String) value;
////                }else {
////                    value = null;
////                }
//////                System.out.println("icbc"+i+"columnName is"+columnName);
//////                System.out.println("icbc"+i+"value is"+value);
////
////                formattedRow.put(columnName, (String) value);
////                i++;
////            }
////            formattedList.add(formattedRow);
////        }
////        return formattedList;
////    }
////
////
////    public static void saveDataToOracle(List<Map<String, String>> dataList, String tableName) {
////        // 数据库连接信息
////        String url = "jdbc:oracle:thin:@//172.16.10.66:1521/ORCL";
////        String username = "kmipcloud";
////        String password = "kmipcloud";
////
////        try (Connection connection = DriverManager.getConnection(url, username, password)) {
////            // 判断表是否存在，如果存在则删除
////            boolean tableExists = checkTableExists(connection, tableName);
////            if (tableExists) {
////                dropTable(connection, tableName);
////            }
////
////            // 创建新表
////            createTable(connection, dataList, tableName);
////
////            // 插入数据
////            insertData(connection, dataList, tableName);
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
////
////    private static boolean checkTableExists(Connection connection, String tableName) throws SQLException {
////        String checkSql = "SELECT COUNT(*) FROM user_tables WHERE table_name = UPPER('" + tableName + "')";
////        try (PreparedStatement statement = connection.prepareStatement(checkSql)) {
////            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
////                if (resultSet.next()) {
////                    return resultSet.getInt(1) > 0;
////                }
////            }
////        }
////        return false;
////    }
////
////    private static void dropTable(Connection connection, String tableName) throws SQLException {
////        try {
////            String dropSql = "DROP TABLE " + tableName;
////            try (Statement statement = connection.createStatement()) {
////                statement.execute(dropSql);
////            }
////        } catch (SQLException e) {
////            // 如果表不存在，忽略异常
////            if (!e.getMessage().contains("table or view does not exist")) {
////                throw e;
////            }
////        }
////    }
////    private static void createTable(Connection connection, List<Map<String, String>> dataList, String tableName)
////            throws SQLException {
////        if (dataList.isEmpty()) {
////            return;
////        }
////        Map<String, String> firstRow = dataList.get(0);
////        StringBuilder createSql = new StringBuilder("CREATE TABLE " + tableName + " (");
////        for (Map.Entry<String, String> entry : firstRow.entrySet()) {
////            int fieldLength = determineFieldLength(entry.getKey(), entry.getValue());
////            createSql.append(entry.getKey()).append(" VARCHAR2(").append(fieldLength).append("),");
////        }
////        createSql.setLength(createSql.length() - 1);
////        createSql.append(")");
////        System.out.println(createSql);
////        try (Statement statement = connection.createStatement()) {
////            statement.execute(createSql.toString());
////        }
////    }
////
////    private static int determineFieldLength(String fieldName, String value) {
////        if (value == null) {
////            return 255; // 默认长度，可根据实际情况调整
////        }
////        if ("ID".equals(fieldName)) {
////            // 假设 ID 列的最大长度为 5
////            return Math.min(value.length(), 5);
////        }
////        return Math.min(value.length() * 2, 4000);
////    }
////
////    private static void insertData(Connection connection, List<Map<String, String>> dataList, String tableName)
////            throws SQLException {
////        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
////        Map<String, String> firstRow = dataList.get(0);
////        for (Map.Entry<String, String> entry : firstRow.entrySet()) {
////            insertSql.append(entry.getKey()).append(",");
////        }
////        insertSql.setLength(insertSql.length() - 1);
////        insertSql.append(") VALUES (");
////        for (int i = 0; i < firstRow.size(); i++) {
////            insertSql.append("?,");
////        }
////        insertSql.setLength(insertSql.length() - 1);
////        insertSql.append(")");
////
////        System.out.println(insertSql.toString());
////        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql.toString())) {
////            for (Map<String, String> row : dataList) {
////                int index = 1;
////                for (Map.Entry<String, String> entry : row.entrySet()) {
////                    if ("ID".equals(entry.getKey())) {
////                        // 处理 ID 列长度问题，假设截断到最大长度 2
////                        String idValue = entry.getValue();
////                        if (idValue.length() > 2) {
////                            idValue = idValue.substring(0, 2);
////                        }
////                        preparedStatement.setString(index++, idValue);
////                    } else {
////                        preparedStatement.setString(index++, entry.getValue());
////                    }
////                }
////                preparedStatement.executeUpdate();
////            }
////        }
////    }
////
////}