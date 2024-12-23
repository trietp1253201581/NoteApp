package com.noteapp.user.model;

/**
 * Một transfer cho dữ liệu của các admin
 * @author Nhóm 17
 * @version 1.0
 */
public class Admin extends AbstractUser {
    public Admin() {
        this.username = "";
        this.password = "";
    }
    
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
