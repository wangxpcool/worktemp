package com.kmerit.Service;

import com.kmerit.entity.DataCompareType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        List<Map<String, Object>> dataList = dataReadService.readData(null);

        //对比数据 生成对比结果
        Map<String, Object> resultMap = null;

        compareResultOutputService.output(resultMap);
    }


}
