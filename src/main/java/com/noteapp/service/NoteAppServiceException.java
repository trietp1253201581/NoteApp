package com.noteapp.service;

/**
 * Đại diện cho tất cả các ngoại lệ có thể xảy ra khi sử dụng dịch vụ 
 * của hệ thống NoteApp
 * @author Nhóm 17
 */
public class NoteAppServiceException extends Exception {

    public NoteAppServiceException(String message) {
        super(message);
    }

    public NoteAppServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoteAppServiceException(Throwable cause) {
        super(cause);
    }
    
}
