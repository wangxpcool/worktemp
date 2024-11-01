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
        //输出对比结果到文件  对比结果可能包括3部分，a多出来的b多出来的 ab 共有的
        List<Map<String,Object>> equals =(List<Map<String,Object>>) resultMap.get("equals");
        List<Map<String,Object>> notEquals =(List<Map<String,Object>>) resultMap.get("notEquals");
        writeListToFile(equals,filePath,"equals");
        writeListToFile(notEquals,filePath,"notEquals");

   }
//    public static void writeListToFile(List<Map<String, Object>> list, String filePath) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//            for (Map<String, Object> map : list) {
//                writeMapToFile(map, writer);
//                writer.newLine(); // 每个对象后换行
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void writeMapToFile(Map<String, Object> map, BufferedWriter writer) throws IOException{
        Stack<Map<String, Object>> stack = new Stack<>();
        stack.push(map);
        Stack<Integer> indentStack = new Stack<>();
        indentStack.push(0);

            while (!stack.isEmpty()) {
                Map<String, Object> currentMap = stack.pop();
                int currentIndent = indentStack.pop();
                String indentation = generateIndentation(currentIndent);

                for (Map.Entry<String, Object> entry : currentMap.entrySet()) {
                    writer.write(indentation + entry.getKey() + ":");
                    writer.newLine(); // 键换行

                    Object value = entry.getValue();
                    if (value instanceof Map<?, ?>) {
                        stack.push((Map<String, Object>) value); // 将值为Map的情况推入栈中
                        indentStack.push(currentIndent + 1); // 增加缩进
                    } else {
                        writer.write(indentation + "    " + value); // 值输出，缩进
                        writer.newLine(); // 值换行
                    }
                }
            }


    }

    private static String generateIndentation(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("    "); // 每个级别使用四个空格
        }
        return sb.toString();
    }

    public static void writeListToFile(List<Map<String, Object>> list, String filePath,String project) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(project);
            writer.newLine(); // 换行
            for (Map<String, Object> map : list) {
                StringBuilder sb = new StringBuilder();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals("diff")){
                        writeMapToFile(map, writer);
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
