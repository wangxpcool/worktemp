package com.kmerit.Service;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class CompareResultOutputService {

    public void output(Map<String,Object> resultMap, String filePath){
        //输出对比结果到文件  对比结果可能包括相等与不等
        List<Map<String,Object>> equals =(List<Map<String,Object>>) resultMap.get("equals");
        List<Map<String,Object>> notEquals =(List<Map<String,Object>>) resultMap.get("notEquals");
        writeListToFile(equals,filePath,"equals");
        writeListToFile(notEquals,filePath,"notEquals");

   }


    public static void writeListToFile(List<Map<String, Object>> list, String filePath,String project) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(project);
            writer.newLine(); // 换行
            for (Map<String, Object> map : list) {
                StringBuilder sb = new StringBuilder();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals("diff")){
                        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
                    }else{
                        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
                    }
                }
                // 去掉最后一个逗号和空格
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2);
                }
                writer.write(sb.toString());
                writer.newLine(); // 换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
