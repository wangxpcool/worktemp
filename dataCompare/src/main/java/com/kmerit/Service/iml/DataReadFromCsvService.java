package com.kmerit.Service.iml;

import com.kmerit.Service.DataReadService;
import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataReadFromCsvService implements DataReadService {

    @Override
    public List<Map<String, Object>> readData(DataSyncType type) {
        //读取数据内容  返回map对象，包括a列数据 b列数据
        //todo key 值
        String filePath = "C:\\Users\\sharping\\Desktop\\work\\test\\2.csv";
        Path path = Paths.get(filePath);
        List result = new ArrayList();
        Map<String, Object> map;
        // 使用BufferedReader逐行读取文件
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                map = new HashMap<>();
                // 将每一行以逗号分隔
                String[] values = line.split(",");
                for (String value : values) {
                    map.put(value,value);
                    System.out.print(value + " ");
                }
                System.out.println(); // 换行
                result.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
