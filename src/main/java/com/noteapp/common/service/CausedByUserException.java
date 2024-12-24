package com.noteapp.common.service;

/**
 * Đại diện cho các ngoại lệ gây ra bởi người dùng khi sử dụng hệ thống NoteApp
 * @author Nhóm 17
 */
public class CausedByUserException extends NoteAppServiceException {

    public CausedByUserException(String message) {
        super(message);
    }

    public CausedByUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public CausedByUserException(Throwable cause) {
        super(cause);
    }
    
}
