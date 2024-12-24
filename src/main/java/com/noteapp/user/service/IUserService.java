package com.noteapp.user.service;

import com.noteapp.service.NoteAppServiceException;
import com.noteapp.user.model.Email;
import com.noteapp.user.model.User;

/**
 *
 * @author admin
 */
public interface IUserService {
    
    boolean isUser(String username) throws NoteAppServiceException;
    
    /**
     * Tạo một {@link User} mới và lưu và CSDL, đồng thời trả lại User vừa tạo
     * @param newUser User cần tạo
     * @return User được tạo thành công.
     * @throws NoteAppServiceException Xảy ra khi (1) User đã tồn tại,
     * (2) Các câu lệnh không thể thực hiện do kết nối tới CSDL
     */
    User create(User newUser) throws NoteAppServiceException;
    
    /**
     * Kiểm tra tài khoản và mật khẩu để đăng nhập
     * @param username username được nhập
     * @param password password được nhập
     * @return Các thông tin của User nếu tài khoản và mật khẩu đúng
     * @throws NoteAppServiceException Xảy ra khi (1) User không tồn tại, 
     * (2) password sai, (3) Việc thực thi các câu lệnh lỗi
     */
    User checkUser(String username, String password) throws NoteAppServiceException;
    
    /**
     * Kiểm tra xem một user có bị khóa quyền không?
     * @param username username của {@link User} cần kiểm tra
     * @return {@code true} nếu User này bị khóa, {@code false} nếu ngược lại
     * @throws NoteAppServiceException Xảy ra khi các câu lệnh bị lỗi
     */
    boolean checkLocked(String username) throws NoteAppServiceException;
    
    /**
     * Cập nhật mật khẩu của User
     * @param username username của user
     * @param newPassword password mới của user
     * @throws NoteAppServiceException Xảy ra khi (1) User không tồn tại,
     * (2) Các câu lệnh bị lỗi
     */
    void updatePassword(String username, String newPassword) throws NoteAppServiceException;
    
    /**
     * Cập nhật các thông tin của user
     * @param user User cần cập nhật
     * @return User sau khi cập nhật
     * @throws NoteAppServiceException Xảy ra khi câu lệnh hoặc kết nối bị lỗi
     */
    User update(User user) throws NoteAppServiceException;
    
    /**
     * Lấy địa chỉ email xác thực của User
     * @param username username của User cần lấy
     * @return Một {@link Email} chứa địa chỉ email xác thực của User
     * @throws NoteAppServiceException Xảy ra khi không thể lấy dữ liệu của User
     */
    Email getVerificationEmail(String username) throws NoteAppServiceException;
}
