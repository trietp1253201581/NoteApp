package com.noteapp.common.dao.sql;

import java.util.Map;

/**
 * Cung cấp các phương thức để đọc một file SQL
 * @author Nhóm 17
 * @version 1.0
 */
public interface ISQLReader {
    /**
     * Đọc một file (.sql) và lấy dữ liệu vào
     * các Query có thể xử lý
     * @param filePath đường dẫn tới File .sql
     * @return Trả về một map, trong đó mỗi key là
     * nhãn của các query đọc được, value là các query
     * tương ứng
     */
    Map<String, String> readSQL(String filePath);
}
