package com.noteapp.common.dao.connection;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection tới hệ QT CSDL MySQL
 * @author Nhóm 17
 * @version 1.0
 */
public class MySQLDatabaseConnection extends SQLDatabaseConnection {

    /**
     * Khởi tạo một kết nối tới hệ quản trị CSDL MySQL
     * @param url đường dẫn kết nối tới máy chủ MySQL ({@code jdbc:mysql://host:port/dbname})
     * @param username tên đăng nhập MySQL
     * @param password mật khẩu của tài khoản MySQL
     */
    public MySQLDatabaseConnection(String url, String username, String password) {
        super.url = url;
        super.username = username;
        super.password = password;
    }
    
    /**
     * Khởi tạo một kết nối tới hệ quản trị CSDL MySQL
     * @param host máy chủ MySQL
     * @param port cổng dịch vụ
     * @param database tên database cần kết nối
     * @param username tên đăng nhập MySQL
     * @param password mật khẩu của tài khoản MySQL
     */
    public MySQLDatabaseConnection(String host, int port, String database, String username, String password) {
        url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        super.username = username;
        super.password = password;
    }
    
    @Override
    public void connect() {
        //Gọi đối tượng Driver để kết nối
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //Tạo 1 connection
        connection = null;
        //Connect tới database với các thông tin đã cho
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}