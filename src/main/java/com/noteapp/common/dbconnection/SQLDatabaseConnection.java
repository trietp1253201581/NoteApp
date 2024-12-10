package com.noteapp.common.dbconnection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 *  Connection tới Database
 * @author admin
 * @version 1.0
 */
public abstract class SQLDatabaseConnection {
    protected Connection connection;
    protected String url;
    protected String username;
    protected String password;
    protected Map<String, String> enableQueries;
    
    protected static final String COMMENT_PREFIX = "--";
    protected static final String LINE_DELIMITER = " ";
    
    /**
     * Connect tới cơ sở dữ liệu
     * @see Connection
     */
    public abstract void connect();
    
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Đọc một file (.sql) và lấy dữ liệu vào
     * các Query có thể xử lý
     * @param filePath đường dẫn tới File .sql
     */
    public void readSQL(String filePath) {
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
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public Map<String, String> getEnableQueries() {
        return enableQueries;
    }
}
