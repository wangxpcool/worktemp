package com.kmerit.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ComparisonDataLineUtil {

    public static void main(String[] args) throws SQLException {
        //TODO 配置中获取属性，summit和工行格式化后的表（可以是语句查询的虚拟表），主键，日期，其实时间，文件存放位置
        String summitTable = "SYS_POSTING_SEND";
        String icbcTable = "(SELECT * FROM SYS_POSTING_SEND_BAK)";
        String dataColumnName = "SEND_DATE";
        String fileName = "C:/Users/suzhongqi/Downloads/coreTest/dataNumDiffReport.csv";
        String startDate = "20240420";
        String endDate = "20240430";

        //TODO 获取数据库连接
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//172.16.10.66:1521/ORCL", "kmipcloud", "kmipcloud");

        //TODO 主键从数据库中获取或是配置中获取
//        ArrayList<String> primaryKeyList = Arrays.asList("POSTING_ID", "TYPE");
        List<String> primaryKeyList = getPrimaryKeyList(connection, summitTable);


        //获取summit表与icbc表，在startdate和enddate之间的差异情况
        int matchNum = prepareMatchedResult(connection, summitTable, icbcTable, primaryKeyList, dataColumnName, startDate, endDate);
        List<String> diffMsgList = prepareDiffResult(connection, summitTable, icbcTable, primaryKeyList, dataColumnName, startDate, endDate);
        connection.close();

        // 获取最终的差异结果 差错数量diffSummitNum和diffIcbcNum，差错内容summitDiffStrInfo和icbcDiffStrInfo
        int diffSummitNum = 0;
        int diffIcbcNum = 0;
        String summitDiffStrInfo = null;
        String icbcDiffStrInfo = null;
        String summitDiffStr = diffMsgList.get(0);
        if (summitDiffStr != null) {
            String[] split = summitDiffStr.split(",");
            diffSummitNum = Integer.parseInt(split[0]);
            summitDiffStrInfo = summitDiffStr.substring(split[0].length() + 1);
        }
        String icbcDiffStr = diffMsgList.get(1);
        if (icbcDiffStr != null) {
            String[] split = icbcDiffStr.split(",");
            diffIcbcNum = Integer.parseInt(split[0]);
            icbcDiffStrInfo = icbcDiffStr.substring(split[0].length() + 1);
        }

        //数据量比较报告内容
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("summit数据共" + (matchNum + diffSummitNum) + "条，");
        stringBuffer.append("summit与icbc匹配上的数据共" + matchNum + "条，");
        stringBuffer.append("summit有但是icbc没有的数据共" + diffSummitNum + "条。\n");
        if (diffSummitNum != 0) {
            stringBuffer.append("summit有但是icbc没有的数据展示如下：\n" + summitDiffStrInfo);
        }
        stringBuffer.append("\n");
        stringBuffer.append("icbc数据共" + (matchNum + diffIcbcNum) + "条，");
        stringBuffer.append("icbc与summit匹配上的数据共" + matchNum + "条，");
        stringBuffer.append("icbc有但是summit没有的数据共" + diffIcbcNum + "条。\n");
        if (diffIcbcNum != 0) {
            stringBuffer.append("icbc有但是summit没有的数据展示如下：\n" + icbcDiffStrInfo);
        }

        // 生成差异文件
        boolean diffReport = createDiffReport(fileName, stringBuffer.toString());
        if (diffReport) {
            System.out.println("数据量比对报告已生成，报告名为：" + fileName);
        } else {
            System.out.println("数据量比对报告生成失败，具体原因请检查程序。");
        }

    }

    private static List getPrimaryKeyList(Connection connection, String tableName) {
        ArrayList<String> primaryKeyList = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME " +
                "FROM USER_CONSTRAINTS UC " +
                "JOIN USER_CONS_COLUMNS UCC ON UC.CONSTRAINT_NAME = UCC.CONSTRAINT_NAME " +
                "WHERE UC.TABLE_NAME = '" + tableName + "' " +
                "AND UC.CONSTRAINT_TYPE = 'P'";
        try {
            Statement primaryKeyStatement = connection.prepareStatement(sql);
            ResultSet primaryKeyResultSet = primaryKeyStatement.executeQuery(sql);
            while (primaryKeyResultSet.next()) {
                primaryKeyList.add(primaryKeyResultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return primaryKeyList;
    }

    /**
     * @param summitTable    从配置中获取格式化后，summit系统的表的新表名
     * @param icbcTbale      从配置中获取格式化后，工行的表的新表名
     * @param dateColumnName 导入日期字段
     * @param primaryKeyList 主键字段(可能是联合主键)
     * @param startDate      查询期间-开始时间
     * @param endDate        查询期间-结束时间
     * @return summit与工行主键有匹配上的数据条数
     */
    private static int prepareMatchedResult(Connection connection, String summitTable, String icbcTbale, List<String> primaryKeyList, String dateColumnName, String startDate, String endDate) {
        //联合主键需要判断多个
        StringJoiner primaryKeySql = new StringJoiner(" and ");
        for (String primaryKey : primaryKeyList) {
            primaryKeySql.add("summitTable." + primaryKey + "=icbcTbale." + primaryKey);
        }

        String matchedNumSQL = "SELECT count(*) " +
                "FROM " + summitTable + " summitTable," + icbcTbale + " icbcTbale" +
                " WHERE " + primaryKeySql +
                " AND summitTable." + dateColumnName + " BETWEEN ? AND ?" +
                " AND icbcTbale." + dateColumnName + " BETWEEN ? AND ?";
        PreparedStatement matchedNumPS = null;
        ResultSet matchedNumSet = null;
        int matchNum = 0;
        try {
            matchedNumPS = connection.prepareStatement(matchedNumSQL);
            matchedNumPS.setString(1, startDate);
            matchedNumPS.setString(2, endDate);
            matchedNumPS.setString(3, startDate);
            matchedNumPS.setString(4, endDate);
            matchedNumSet = matchedNumPS.executeQuery();
            while (matchedNumSet.next()) {
                matchNum = matchedNumSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (matchedNumSet != null) {
                try {
                    matchedNumSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (matchedNumPS != null) {
                try {
                    matchedNumPS.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return matchNum;
    }


    /**
     * @param summitTable    从配置中获取格式化后，summit系统的表的新表名
     * @param icbcTbale      从配置中获取格式化后，工行的表的新表名
     * @param dateColumnName 导入日期字段
     * @param primaryKeyList 主键字段(可能是联合主键)
     * @param startDate      查询期间-开始时间
     * @param endDate        查询期间-结束时间
     * @return summit与工行主键有差异的数据，List集合中0为summit有工行没有的，1为工行有summit没有的。数据内容格式为 {差异数量,内容}
     * @throws SQLException SQLException
     */
    private static List<String> prepareDiffResult(Connection connection, String summitTable, String icbcTbale, List<String> primaryKeyList, String dateColumnName, String startDate, String endDate) {
        ArrayList<String> diffMsgList = new ArrayList<>();
        PreparedStatement summitPS = null;
        PreparedStatement icbcPS = null;
        ResultSet summitResultSet = null;
        ResultSet icbcResultSet = null;

        //联合主键需要判断多个
        StringJoiner primaryKeySql = new StringJoiner(" and ");
        for (String primaryKey : primaryKeyList) {
            primaryKeySql.add("summitTable." + primaryKey + "=icbcTbale." + primaryKey);
        }

        String summitSql = "SELECT summitTable.* FROM " + summitTable + " summitTable LEFT JOIN " + icbcTbale + " icbcTbale" +
                " ON " + primaryKeySql +
                " AND icbcTbale." + dateColumnName + " BETWEEN ? AND ?" +
                " WHERE icbcTbale ." + primaryKeyList.get(0) + " IS NULL" +
                " AND summitTable." + dateColumnName + " BETWEEN ? AND ?";
        try {
            summitPS = connection.prepareStatement(summitSql);
            summitPS.setString(1, startDate);
            summitPS.setString(2, endDate);
            summitPS.setString(3, startDate);
            summitPS.setString(4, endDate);
            summitResultSet = summitPS.executeQuery();
            String summitDiff = getDiffMsgByResult(summitResultSet);
            diffMsgList.add(summitDiff);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (summitResultSet != null) {
                try {
                    summitResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (summitPS != null) {
                try {
                    summitPS.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        String icbcSql = "SELECT icbcTbale .* FROM " + icbcTbale + " icbcTbale LEFT JOIN " + summitTable + " summitTable" +
                " ON " + primaryKeySql +
                " AND summitTable." + dateColumnName + " BETWEEN ? AND ?" +
                " WHERE summitTable." + primaryKeyList.get(0) + " IS NULL" +
                " AND icbcTbale ." + dateColumnName + " BETWEEN ? AND ?";
        try {
            icbcPS = connection.prepareStatement(icbcSql);
            icbcPS.setString(1, startDate);
            icbcPS.setString(2, endDate);
            icbcPS.setString(3, startDate);
            icbcPS.setString(4, endDate);
            icbcResultSet = icbcPS.executeQuery();
            String icbcDiff = getDiffMsgByResult(icbcResultSet);
            diffMsgList.add(icbcDiff);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (icbcResultSet != null) {
                try {
                    icbcResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (icbcPS != null) {
                try {
                    icbcPS.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return diffMsgList;
    }

    /**
     * @param resultSet 数据库加载出来的差异数据对象
     * @return 特定格式的差异数据字符串。格式为照配置文件顺序输出字段名，然后输出字段值
     */
    private static String getDiffMsgByResult(ResultSet resultSet) throws SQLException {
        int diffNum = 0;
        StringBuffer sb = new StringBuffer();
        int columnCount = resultSet.getMetaData().getColumnCount();
        //TODO 从配置文件中获取字段顺序集合 trueOrderColumnNameList
        ArrayList<String> trueOrderColumnNameList = new ArrayList<>(Arrays.asList("POSTING_ID", "TYPE", "SEND_DATE", "STATUS", "TIMES"));
//        ArrayList<String> trueOrderColumnNameList = getTrueOrderColumnNameList("");
        //获取set中的字段顺序
        Map<Integer, Integer> columnNameNoMap = getColumnNameNoMap(resultSet, trueOrderColumnNameList);
        //先输出正确顺序的字段名
        StringJoiner joiner = new StringJoiner(",");
        for (String columnName : trueOrderColumnNameList) {
            joiner.add(columnName);
        }
        sb.append(joiner).append("\n");
        while (resultSet.next()) {
            diffNum++;
            for (int i = 1; i <= columnCount; i++) {
                sb.append("\"" + resultSet.getString(columnNameNoMap.get(i)) + "\"");
                if (i < columnCount) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        if (diffNum == 0) {
            return null;
        } else {
            return diffNum + "," + sb.toString();
        }
    }

    //TODO 获取配置文件中的字段顺序
    private ArrayList<String> getTrueOrderColumnNameList(String propertyPath) {
        return new ArrayList<>();
    }

    /**
     * 通过配置文件中的顺序，为数据库中获取的数据的顺序进行顺序的映射。
     * 如：resultSet {字段1，字段2，字段3} ，trueOrderColumnNameList {字段2，字段3，字段1}
     * 得到的映射就是 1-2,2-3,3-1 即第一个字段取SET的第二个字段，第二个字段取SET第三个字段，第三个字段取SET第一个字段
     */
    private static Map<Integer, Integer> getColumnNameNoMap(ResultSet resultSet, ArrayList<String> trueOrderColumnNameList) throws SQLException {
        Map<String, Integer> originalColumnNameMap = new HashMap<>();
        Map<Integer, Integer> trueOrderColumnNameMap = new HashMap<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            originalColumnNameMap.put(columnName, i);
        }
        for (int i = 0; i < trueOrderColumnNameList.size(); i++) {
            trueOrderColumnNameMap.put(originalColumnNameMap.get(trueOrderColumnNameList.get(i)), i + 1);
        }
        return trueOrderColumnNameMap;
    }

    /**
     * 根据文件名和文件内容产出两表数据量结果的文件
     *
     * @param filename 文件名
     * @param diffInfo 文件内容
     * @return 生成文件是否成功，报错则是返回false
     */
    private static boolean createDiffReport(String filename, String diffInfo) {
        File file = new File(filename);
        try (FileWriter writer = new FileWriter(file)) {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            writer.write(diffInfo);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
