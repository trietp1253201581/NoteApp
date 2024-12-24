package com.noteapp.user.dao;

import com.noteapp.dao.DAOException;
import com.noteapp.user.model.Admin;

/**
 * Định nghĩa các hành động có thể thực hiện trên CSDL của Admin
 * @author Nhóm 17
 * @version 1.0
 */
public interface IAdminDAO {
    
    /**
     * Lấy một {@link Admin} theo username
     * @param username username của User cần lấy.
     * @return Admin có username tương ứng
     * @throws DAOException Khi không có admin nào có username đã cho,
     * hoặc khi kết nối bị lỗi, hoặc Query không hợp lệ
     * @see Admin
     */
    Admin get(String username) throws DAOException;
    
    /**
     * Cập nhật các trường thông tin của một Admin trong CSDL
     * @param admin Admin cần cập nhật
     * @throws DAOException Khi User không tồn tại trong CSDL,
     * hoặc khi kết nối bị lỗi, hoặc Query không hợp lệ
     * @see Admin
     * @see #get(String) 
     */
    void update(Admin admin) throws DAOException;
}
