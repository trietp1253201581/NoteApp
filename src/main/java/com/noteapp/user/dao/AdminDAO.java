package com.noteapp.user.dao;

import com.noteapp.dao.DAOException;
import com.noteapp.dao.DAOKeyException;
import com.noteapp.dao.FailedExecuteException;
import com.noteapp.dao.NotExistDataException;
import com.noteapp.dao.connection.MySQLDatabaseConnection;
import com.noteapp.dao.sql.SQLReader;
import com.noteapp.user.model.Admin;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Triển khai các phương thức thao tác dữ liệu với Admin
 * @author Nhóm 17
 */
public class AdminDAO extends AbstractUserDAO implements IAdminDAO {
 
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/user/db/AdminQueries.sql";
    
    /**
     * Tên các cột trong CSDL
     */
    protected static enum ColumnName {
        username, password;
    }
    
    /**
     * Các loại Query được cung cấp
     */
    protected static enum QueriesType {
        GET, UPDATE;
    }
    
    private AdminDAO() {
        //init connection
        setDatabaseConnection(new MySQLDatabaseConnection(DATABASE_HOST, DATABASE_PORT, 
            DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD));
        initConnection();
        //get enable query
        setFileReader(new SQLReader());
        getEnableQueriesFrom(SQL_FILE_DIR);
    }
    
    private static class SingletonHelper {
        private static final AdminDAO INSTANCE = new AdminDAO();
    }
    
    /**
     * Lấy thể hiện duy nhất của {@link AdminDAO}
     * @return Instance duy nhất
     * @see IAdminDAO
     */
    public static AdminDAO getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    @Override
    public Admin get(String username) throws DAOException {
        //Nếu username không hợp lệ thì thông báo ngoại lệ Key
        if ("".equals(username)) {
            throw new DAOKeyException();
        }
        try {
            //Thực thi truy vấn SQL, gán tham số cho USERNAME và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET.toString());
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Lấy admin
            Admin admin = new Admin();
            while (resultSet.next()) {
                admin.setUsername(resultSet.getString(ColumnName.username.toString()));
                admin.setPassword(resultSet.getString(ColumnName.password.toString()));
            }
            if(admin.isDefaultValue()) {
                throw new NotExistDataException();
            }
            return admin;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void update(Admin admin) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE.toString());
            //Set các tham số cho truy vấn
            preparedStatement.setString(1, admin.getPassword());
            preparedStatement.setString(2, admin.getUsername());
            
            //Nếu không hàng nào bị thay đổi thì thông báo lỗi
            if (preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
}
