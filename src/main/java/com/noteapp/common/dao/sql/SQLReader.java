package com.noteapp.common.dao.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Triển khai các phương thức đọc SQL
 * @author Nhóm 17
 * @version 1.0
 */
public class SQLReader implements ISQLReader {
    protected static final String COMMENT_PREFIX = "--";
    protected static final String LINE_DELIMITER = " ";
    
    @Override
    public Map<String, String> readSQL(String filePath) {
        Map<String, String> enableQueries = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line;
            String queryName = null;
            StringBuilder query = new StringBuilder();
            
            //Lần lượt đọc từng dòng dữ liệu
            while((line = bufferedReader.readLine()) != null) {
                //Nếu bắt đầu bằng dấu -- thì đây là dòng key, còn không thì
                //thêm dòng này vào query có key tương ứng
                if(line.trim().startsWith(COMMENT_PREFIX)) {
                    if(queryName != null) {
                        enableQueries.put(queryName, query.toString().trim());
                        query.setLength(0);
                    }
                    queryName = line.trim().substring(2).trim();
                } else {
                    query.append(line);
                    query.append(LINE_DELIMITER);
                }
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
        return enableQueries;
    }
}
