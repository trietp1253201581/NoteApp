package com.noteapp.dao;

/**
 * Ngoại lệ cho việc khóa dùng truy cập các dữ liệu là không hợp lệ.
 * @author admin
 */
public class DAOKeyException extends DAOException {
    public DAOKeyException() {
        super("This key is not accepted!");
    }

    public DAOKeyException(Throwable cause) {
        super("This key is not accepted!", cause);
    }

    public DAOKeyException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DAOKeyException(String message) {
        super(message);
    }
}
