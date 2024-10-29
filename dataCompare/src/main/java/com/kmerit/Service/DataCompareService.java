package com.kmerit.Service;

import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataCompareService extends Thread {

    @Autowired
    CompareResultOutputService compareResultOutputService;

    @Autowired
    private Map<String, DataReadService> instances;
    @Autowired
    DataSyncService dataSyncService;

    @Autowired
    QueryService queryService;

    @Override
    public void run() {

    }

    //对比方式1 将源a表 源b表数据同步至本地库local,我这边查询出来主键一致的，进行比对
    public void compare(DataCompareType type) {

        dataSyncService.sync(type.getDatasourceA());
        dataSyncService.sync(type.getDatasourceB());
        List<Map<String, Object>> list;
        DataSyncType localType = new DataSyncType();
        localType.setSql("select a.id from a_flow_a a,a_flow_b b where a.id=b.id ");
        list = queryService.getThirdData(localType);
        //对比数据 生成对比结果
        Map<String, Object> resultMap = null;
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(stringObjectMap -> {
                DataSyncType localAType = new DataSyncType();
                localAType.setSql("select * from a_flow_a a where a.id= " + stringObjectMap.get("id"));
                List<Map<String, Object>> listA = queryService.getThirdData(localAType);
                Map<String, Object> mapA = listA.get(0);

                DataSyncType localBType = new DataSyncType();
                localBType.setSql("select * from a_flow_b a where a.id= " + stringObjectMap.get("id"));
                List<Map<String, Object>> listB = queryService.getThirdData(localBType);
                Map<String, Object> mapB = listB.get(0);

                //默认结果为不同
                Boolean equalsFlag = true;
                List<String> column = new ArrayList<>();
                mapA.keySet().forEach(r -> {
                    if (r.equals("012")){
                        System.out.println();
                    }
                    Object obj =mapA.get(r);
                    if (obj instanceof java.lang.String){
                        String valueA = (String) mapA.get(r);
                        String valueB = (String) mapB.get(r);
                        if (!valueA.equals(valueB)) {
                            column.add(r);
                        }
                    }
                });
                if (column.size() > 0) {
                    equalsFlag = false;
                    System.out.println("数据不等：");
                    System.out.println("数据A：" + mapA);
                    System.out.println("数据B：" + mapB);
                    System.out.println("数据不同点：" + column);
                    //循环输出不同的值

                }

            });
        }


        compareResultOutputService.output(resultMap);
    }


}
