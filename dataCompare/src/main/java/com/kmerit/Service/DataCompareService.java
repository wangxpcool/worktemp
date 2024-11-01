package com.kmerit.Service;

import com.kmerit.config.TaskStatusManager;
import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DataCompareService {

    @Autowired
    CompareResultOutputService compareResultOutputService;

    @Autowired
    DataSyncService dataSyncService;

    @Autowired
    QueryService queryService;

    @Autowired
    private TaskStatusManager taskStatusManager;

    @Autowired
    private Map<String, ComparatorFactory> comparatorFactoryMap;

    static Logger logger = LoggerFactory.getLogger(DataCompareService.class);

    //对比方式1 将源a表 源b表数据同步至本地库local,我这边查询出来主键一致的，进行比对
    @Async("taskExecutor") // 指定自定义线程池
    public String compare(DataCompareType type,String jobName) {

        try {
            //前置任务1 数据同步：读取数据并落本地库
            dataSyncService.sync(type.getDatasourceA());
            dataSyncService.sync(type.getDatasourceB());
            logger.info(">>>>>>>>任务1 数据同步：读取数据并落本地库 执行完毕");

            //任务2 格式化
            logger.info(">>>>>>>>任务2 格式化 执行完毕");

            //任务3 比对总数
            logger.info(">>>>>>>>任务3 比对总数 执行完毕");

            //任务4 比对内容
            Map<String, Object> result = compareDiff(type);
            System.out.println(result);
            compareResultOutputService.output(result,type.getOutputFilePath());
            logger.info(">>>>>>>>任务4 比对内容 执行完毕");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }finally {
            taskStatusManager.markTaskCompleted(jobName);
        }
        return "data compare successfully";
    }

    public Map<String, Object> compareDiff(DataCompareType type) {

        String tableName_a = type.getDatasourceA().getTableNameLocal();
        String tableName_b = type.getDatasourceB().getTableNameLocal();
        String primaryKey = type.getPrimaryKey();
        List<Map<String, Object>> list;
        DataSyncType localType = new DataSyncType();
        String querySql = String.format("SELECT a.%s FROM %s a, %s b WHERE a.%s = b.%s",
                primaryKey, tableName_a, tableName_b, primaryKey, primaryKey);
        localType.setSql(querySql);
        list = queryService.getThirdData(localType);
        //对比数据 生成对比结果

        //相同结果集
        List<Map<String, Object>> equalsResultList = new ArrayList<>();
        //不同结果集
        List<Map<String, Object>> notEqualsResultList = new ArrayList<>();

        Map<String, Object> resultMap = new HashMap<>();
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(stringObjectMap -> {
                //主键值
                String primaryKeyValue = (String) stringObjectMap.get("id");


                DataSyncType localAType = new DataSyncType();
                String queryDataASql = String.format("select * from %s a where a.%s= %s",
                        tableName_a, primaryKey, primaryKeyValue);
                localAType.setSql(queryDataASql);
                List<Map<String, Object>> listA = queryService.getThirdData(localAType);
                Map<String, Object> mapA = listA.get(0);

                DataSyncType localBType = new DataSyncType();
                String queryDataBSql = String.format("select * from %s a where a.%s= %s",
                        tableName_b, primaryKey, primaryKeyValue);
                localBType.setSql(queryDataBSql);
                List<Map<String, Object>> listB = queryService.getThirdData(localBType);
                Map<String, Object> mapB = listB.get(0);

                //默认结果为不同
                Boolean equalsFlag = true;
                List<String> column = new ArrayList<>();
                mapA.keySet().forEach(r -> {
                    Object obj = mapA.get(r);
                    ComparatorFactory comparator;
                    String format = null;
                    if (obj instanceof BigDecimal) {
                        comparator = comparatorFactoryMap.get("bigDecimalComparator");
                        format = type.getNumberFormat();

                    } else {
                        comparator = comparatorFactoryMap.get("normalComparator");
                    }
                    Object valueA = mapA.get(r);
                    Object valueB = mapB.get(r);
                    if (!comparator.compare(valueA, valueB, format)) {
                        column.add(r);
                    }
                });
                if (column.size() > 0) {
                    equalsFlag = false;
                    Map<String, Object> diff = new HashMap<>();
                    diff.put("dataA", mapA);
                    diff.put("dataB", mapB);
                    Map<String, Object> diffAandB = new HashMap<>();
                    column.stream().forEach(r -> {
                        String diffStr = "数据A：" + mapA.get(r) + " 数据B：" + mapB.get(r);
                        diffAandB.put(r, diffStr);
                    });
                    diff.put("diff", diffAandB);
                    notEqualsResultList.add(diff);
                } else {
                    equalsResultList.add(mapA);
                }
            });
        }
        resultMap.put("equals", equalsResultList);
        resultMap.put("notEquals", notEqualsResultList);
        return resultMap;


    }


}
