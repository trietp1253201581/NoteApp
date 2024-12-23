package com.noteapp.common.dao.connection;

import java.sql.Connection;

/**
 *  Connection tới Database
 * @author admin
 * @version 1.0
 */
public abstract class SQLDatabaseConnection {
    protected Connection connection;
    protected String url;
    protected String username;
    protected String password;
    
    /**
     * Connect tới cơ sở dữ liệu
     * @see java.sql.Connection
     */
    public abstract void connect();
    
    public Connection getConnection() {
        return connection;
    }
}
