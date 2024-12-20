package com.noteapp.common.dao;

/**
 * Ngoại lệ không thể thực thi câu lệnh SQL
 * @author Nhóm 17
 */
public class FailedExecuteException extends DAOException {
    protected static final String NOTIFY = "Cause by NoteApp System. Can't execute!";
    
    public FailedExecuteException() {
        super(NOTIFY);
    }

    public FailedExecuteException(Throwable cause) {
        super(NOTIFY, cause);
    }
    
    public FailedExecuteException(String message) {
        super(message);
    }

    public FailedExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}