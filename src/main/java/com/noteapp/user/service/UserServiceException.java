package com.noteapp.user.service;

/**
 * Ngoại lệ cho các service của user
 * @author Nhóm 17
 */
public class UserServiceException extends Exception {
    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}