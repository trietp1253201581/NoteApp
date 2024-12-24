package com.noteapp.user.dao;

import com.noteapp.dao.DAOException;
import com.noteapp.user.model.User;
import java.util.List;

/**
 * Định nghĩa các hành động có thể thực hiện trên CSDL của User 
 * @author Nhóm 17
 * @version 1.0
 */
public interface IUserDAO {
    
    /**
     * Lấy tất cả các hàng trong CSDL của User
     * @return Một {@link List} các {@link User}, mỗi 
     * {@link User} dại diện cho một hàng của CSDL.
     * @throws DAOException Khi kết nối bị lỗi 
     * hoặc Query không hợp lệ.
     * @see User 
     */
    List<User> getAll() throws DAOException;
    
    /**
     * Lấy một {@link User} theo username
     * @param username username của User cần lấy.
     * @return User có username tương ứng
     * @throws DAOException Khi không có user nào có username đã cho,
     * hoặc khi kết nối bị lỗi, hoặc Query không hợp lệ
     * @see User
     */
    User get(String username) throws DAOException;
    
    /**
     * Tạo một User mới
     * @param newUser User cần tạo
     * @return User được tạo
     * @throws DAOException Khi User đã tồn tại trong CSDL,
     * hoặc khi kết nối bị lỗi, hoặc Query không hợp lệ
     * @see User
     * @see #get(String) 
     */
    User create(User newUser) throws DAOException;
    
    /**
     * Cập nhật các trường thông tin của một User trong CSDL
     * @param user User cần cập nhật
     * @throws DAOException Khi User không tồn tại trong CSDL,
     * hoặc khi kết nối bị lỗi, hoặc Query không hợp lệ
     * @see User 
     * @see #get(String) 
     */
    void update(User user) throws DAOException;
    
    /**
     * Xóa một User trong CSDL có username cho trước
     * @param username username của User cần xóa
     * @throws DAOException Khi khi kết nối bị lỗi, hoặc Query không hợp lệ
     * @see User
     * @see #get(String) 
     */
    void delete(String username) throws DAOException;
}
