package com.kmerit.Service.iml;

import com.kmerit.Service.DataReadService;
import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import com.kmerit.reponsitory.QueryService;
import com.kmerit.util.CsvReaderUtil;
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
        //todo key 值  key值可能在表头上，这个得看数据格式
        String filePath = type.getSourcPath();
        return CsvReaderUtil.readDataFromCsv(filePath);
    }

}
