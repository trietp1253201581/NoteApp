package com.noteapp.common.dao;

/**
 * Ngoại lệ cho việc access dữ liệu
 * @author Nhóm 17
 */
public class DAOException extends Exception {
    public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}