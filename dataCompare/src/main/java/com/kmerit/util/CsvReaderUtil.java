package com.kmerit.util;
//        String filePath = "C:\\Users\\sharping\\Desktop\\a_flow.csv"; // 替换为你的 CSV 文件路径

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Slf4j
public class CsvReaderUtil {

    static Logger logger = LoggerFactory.getLogger(CsvReaderUtil.class);

    public static List<Map<String, Object>> readDataFromCsv(String filePath) {

        List<Map<String, Object>> dataList = new ArrayList<>();

        EasyExcel.read(filePath, new ReadListener<LinkedHashMap<Integer, Object>>() {
            private boolean isFirstRow = true;
            private LinkedHashMap<Integer, Object> headers;

            @Override
            public void invoke(LinkedHashMap<Integer, Object> data, AnalysisContext context) {
                if (isFirstRow) {
                    // 处理表头
                    headers = data; // 将首行数据作为表头
                    isFirstRow = false;
                    logger.info("Headers: " + headers);
                    return;
                }
                LinkedHashMap<String, Object> dataNew = new LinkedHashMap<>();
                data.keySet().forEach(r -> {
                    dataNew.put((String) (headers.get(r)), data.get(r));
                });
                // 输出数据行
                dataList.add(dataNew);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 所有数据读取完成后执行
                logger.info("All data has been read.");
            }
        }).headRowNumber(0).sheet().doRead();
        return dataList;
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\sharping\\Desktop\\a_flow.csv"; // 替换为你的 CSV 文件路径
        List<Map<String, Object>> dataList = readDataFromCsv(filePath);
        log.info(dataList.toString());

    }
}