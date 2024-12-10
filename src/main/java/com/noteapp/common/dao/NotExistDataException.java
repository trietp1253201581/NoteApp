package com.noteapp.common.dao;

/**
 * Ngoại lệ cho việc dữ liệu không tồn tại
 * @author Nhóm 17
 */
public class NotExistDataException extends DAOException {  
    protected static final String NOTIFY = "Data is not exist";
    
    public NotExistDataException() {
        super(NOTIFY);
    }
    
    public NotExistDataException(String message) {
        super(message);
    }

    public NotExistDataException(Throwable cause) {
        super(NOTIFY, cause);
    }

    public NotExistDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
