package com.noteapp.user.dao;

import com.noteapp.dao.NotExistDataException;
import com.noteapp.dao.FailedExecuteException;
import com.noteapp.dao.DAOException;
import com.noteapp.dao.DAOKeyException;
import com.noteapp.dao.connection.MySQLDatabaseConnection;
import com.noteapp.dao.sql.SQLReader;
import com.noteapp.user.model.User;
import com.noteapp.user.model.Email;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai các phương thức thao tác dữ liệu với User
 * @author Nhóm 17
 */
public class UserDAO extends AbstractUserDAO implements IUserDAO {
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/user/db/UserQueries.sql";
    
    /**
     * Tên các cột trong CSDL
     */
    protected static enum ColumnName {
        name, username, password, birthday, school, gender, email, is_locked;
    }
    
    /**
     * Các loại Query được cung cấp
     */
    protected static enum QueriesType {
        GET_ALL_BY_MANAGE, GET, CREATE, UPDATE, DELETE;
    }

    private UserDAO() {
        //init connection
        setDatabaseConnection(new MySQLDatabaseConnection(DATABASE_HOST, DATABASE_PORT, 
            DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD));
        initConnection();
        //get enable query
        setFileReader(new SQLReader());
        getEnableQueriesFrom(SQL_FILE_DIR);
    }
    
    private static class SingletonHelper {
        private static final UserDAO INSTANCE = new UserDAO();
    }
    
    /**
     * Lấy thể hiện duy nhất của {@link UserDAO}
     * @return Instance duy nhất
     * @see IUserDAO
     */
    public static UserDAO getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    @Override
    public List<User> getAll(String admin) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_MANAGE.toString());
            preparedStatement.setString(1, admin);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            List<User> users = new ArrayList<>();
            //Chuyển từng hàng dữ liệu sang user và thêm vào list
            while (resultSet.next()) {
                User user = new User();
                Email email = new Email();

                user.setName(resultSet.getString(ColumnName.name.toString()));
                user.setUsername(resultSet.getString(ColumnName.username.toString()));
                user.setPassword(resultSet.getString(ColumnName.password.toString()));
                user.setBirthday(resultSet.getDate(ColumnName.birthday.toString()));
                user.setSchool(resultSet.getString(ColumnName.school.toString()));
                user.setGender(User.Gender.valueOf(resultSet.getString(ColumnName.gender.toString())));
                email.setAddress(resultSet.getString(ColumnName.email.toString()));
                user.setEmail(email);
                user.setLocked(Boolean.parseBoolean(resultSet.getString(ColumnName.is_locked.toString())));

                users.add(user);
            }    
            return users;
        } catch (SQLException sqlException) {
            throw new FailedExecuteException(sqlException.getCause());
        }
    }

    @Override
    public User get(String username) throws DAOException {
        //Nếu username không hợp lệ thì thông báo ngoại lệ Key
        if ("".equals(username)) {
            throw new DAOKeyException();
        }
        try {
            //Thực thi truy vấn SQL, gán tham số cho USERNAME và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET.toString());
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Lấy User
            User user = new User();
            while (resultSet.next()) {
                Email email = new Email();
                
                user.setName(resultSet.getString(ColumnName.name.toString()));
                user.setUsername(resultSet.getString(ColumnName.username.toString()));
                user.setPassword(resultSet.getString(ColumnName.password.toString()));
                user.setBirthday(resultSet.getDate(ColumnName.birthday.toString()));
                user.setSchool(resultSet.getString(ColumnName.school.toString()));
                user.setGender(User.Gender.valueOf(resultSet.getString(ColumnName.gender.toString())));
                email.setAddress(resultSet.getString(ColumnName.email.toString()));
                user.setEmail(email);
                user.setLocked(Boolean.parseBoolean(resultSet.getString(ColumnName.is_locked.toString())));
            }
            
            //Nếu ko tồn tại thì ném ra ngoại lệ
            if (user.isDefaultValue()) {
                throw new NotExistDataException();
            }
            
            return user;
        } catch (SQLException sqlException) {
            throw new FailedExecuteException(sqlException.getCause());
        }       
    }

    @Override
    public User create(User newUser) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE.toString());
            //Set các tham số cho truy vấn
            preparedStatement.setString(1, newUser.getName());
            preparedStatement.setString(2, newUser.getUsername());
            preparedStatement.setString(3, newUser.getPassword());
            preparedStatement.setDate(4, newUser.getBirthday());
            preparedStatement.setString(5, newUser.getSchool());
            preparedStatement.setString(6, newUser.getGender().toString());
            preparedStatement.setString(7, newUser.getEmail().getAddress());
            preparedStatement.setString(7, String.valueOf(newUser.isLocked()));
            
            //Nếu không hàng nào được tạo mới thì thông báo lỗi
            if (preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
            
            return newUser;
        } catch (SQLException sqlException) {
            throw new FailedExecuteException(sqlException.getCause());
        }
    }

    @Override
    public void update(User user) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE.toString());
            //Set các tham số cho truy vấn
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setDate(3, user.getBirthday());
            preparedStatement.setString(4, user.getSchool());
            preparedStatement.setString(5, user.getGender().toString());
            preparedStatement.setString(6, user.getEmail().getAddress());
            preparedStatement.setString(7, String.valueOf(user.isLocked()));
            preparedStatement.setString(8, user.getUsername());
            
            //Nếu không hàng nào bị thay đổi thì thông báo lỗi
            if (preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException sqlException) {
            throw new FailedExecuteException(sqlException.getCause());
        }
    }

    @Override
    public void delete(String username) throws DAOException {
        //Kiểm tra tính hợp lệ của username
        if ("".equals(username)) {
            throw new DAOKeyException();
        }
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.DELETE.toString());
            //Set các tham số cho truy vấn
            preparedStatement.setString(1, username);

            if(preparedStatement.executeUpdate() < 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException sqlException) {
            throw new FailedExecuteException(sqlException.getCause());
        }
    }
}