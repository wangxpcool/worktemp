package com.kmerit.Service.iml;

import com.kmerit.Service.DataReadService;
import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataReadFromDBService  implements DataReadService {


    @Autowired
    QueryService queryService;

    public List<Map<String, Object>> readData(DataSyncType type) {
        //读取数据内容  返回map对象，包括a列数据 b列数据
        List<Map<String, Object>> data = new ArrayList<>();
        if ("summit".equals(type.getDb())){
            data = queryService.getPrimaryData(type);
        }else if ("icbc".equals(type.getDb())){
            data = queryService.getSecondaryData(type);
        }else{
            return data;
        }
        data.stream().forEach(map-> System.out.println(map));
        return data;
    }

}
