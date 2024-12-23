package com.noteapp.user.service;

import com.noteapp.user.model.Admin;
import java.util.Map;

/**
 *
 * @author admin
 */
public interface IAdminService {
    
    /**
     * Kiểm tra xem một tên đăng nhập có phải là admin hay không
     * @param username username cần kiểm tra
     * @return {@code true} nếu đây là một {@link Admin}, {@code false} nếu ngược lại
     * @throws UserServiceException Xảy ra khi các câu lệnh không thể 
     * thực hiện do kết nối tới CSDL
     */
    public boolean isAdmin(String username) throws UserServiceException;
    
    /**
     * Kiểm tra tài khoản và mật khẩu để đăng nhập
     * @param username username được nhập
     * @param password password được nhập
     * @return Các thông tin của Admin nếu tài khoản và mật khẩu đúng
     * @throws UserServiceException Xảy ra khi (1) Admin không tồn tại, 
     * (2) password sai, (3) Việc thực thi các câu lệnh lỗi
     */
    Admin checkAdmin(String username, String password) throws UserServiceException;
    
    /**
     * Lất tất cả các thông tin bị khóa quyền hay không của các user
     * @return Một Map chứa username và trạng thái khóa tương ứng của
     * các {@link User}
     * @throws UserServiceException Xảy ra khi các câu lệnh bị lỗi
     */
    public Map<String, Boolean> getAllLockedStatus() throws UserServiceException;
    
    /**
     * Cập nhật trạng thái khóa của một user
     * @param username username của {@link User} cần cập nhật
     * @param lockedStatus trạng thái khóa cần cập nhật của User
     * @throws UserServiceException Xảy ra khi các câu lệnh bị lỗi
     */
    void updateLockedStatus(String username, boolean lockedStatus) throws UserServiceException;
}
