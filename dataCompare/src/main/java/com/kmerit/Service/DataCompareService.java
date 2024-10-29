package com.kmerit.Service;

import com.kmerit.entity.DataCompareType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataCompareService  extends Thread {

    @Autowired
    CompareResultOutputService compareResultOutputService;

    @Autowired
    private Map<String, DataReadService> instances;

    @Override
    public void run() {

    }

    public void compare(DataCompareType type) {

        List<Map<String, Object>> list;
        DataReadService instance = instances.get(type.getSourcType());
        if (instance != null) {
            list = instance.readData(null);
        } else {
            throw new IllegalArgumentException("No such bean: " + type.getSourcType());
        }

        //对比数据 生成对比结果
        Map<String, Object> resultMap = null;

        compareResultOutputService.output(resultMap);
    }


}
