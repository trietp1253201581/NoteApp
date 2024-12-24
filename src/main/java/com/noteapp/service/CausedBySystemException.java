package com.noteapp.service;

/**
 * Đại diện cho các ngoại lệ gây ra bởi hệ thống NoteApp
 * @author Nhóm 17
 */
public class CausedBySystemException extends NoteAppServiceException {
    protected static final String DEFAULT_NOTIFY = "Caused by NoteApp System";

    public CausedBySystemException(String message) {
        super(DEFAULT_NOTIFY + message);
    }

    public CausedBySystemException(String message, Throwable cause) {
        super(DEFAULT_NOTIFY + message, cause);
    }

    public CausedBySystemException(Throwable cause) {
        super(DEFAULT_NOTIFY, cause);
    }
    
}
