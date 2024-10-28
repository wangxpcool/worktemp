package com.kmerit.Service;

import com.kmerit.entity.DataCompareType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataCompareService  extends Thread {

    @Autowired
    DataReadService dataReadService;

    @Autowired
    CompareResultOutputService compareResultOutputService;

    @Override
    public void run() {

    }

    public void compare(DataCompareType type) {
        Map<String, Object> dataMap = dataReadService.readData(type);

        //对比数据 生成对比结果
        Map<String, Object> resultMap = null;

        compareResultOutputService.output(resultMap);
    }


}
