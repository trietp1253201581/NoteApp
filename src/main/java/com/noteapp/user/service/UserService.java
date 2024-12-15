package com.noteapp.user.service;

import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dao.NotExistDataException;
import com.noteapp.user.dao.IUserDAO;
import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.model.Email;
import com.noteapp.user.model.User;

/**
 * Cung cấp các service liên quan tới User
 * @author Nhóm 17
 * @see IUserDAO
 * @see User
 */
public class UserService {
    protected IUserDAO userDAO;

    public UserService() {
        
    }
    
    /**
     * Lấy các thể hiện của DAO, dùng để truy cập và kết nối tới CSDL
     */
    protected void getInstanceOfDAO() {
        userDAO = UserDAO.getInstance();
    }
    
    /**
     * Tạo một {@link User} mới và lưu và CSDL, đồng thời trả lại User vừa tạo
     * @param newUser User cần tạo
     * @return User được tạo thành công.
     * @throws UserServiceException Xảy ra khi (1) User đã tồn tại,
     * (2) Các câu lệnh không thể thực hiện do kết nối tới CSDL
     * @see IUserDAO#get(String) 
     * @see IUserDAO#create(User) 
     * @see com.noteapp.common.dao.FailedExecuteException
     */
    public User create(User newUser) throws UserServiceException {
        getInstanceOfDAO();
        String username = newUser.getUsername();
        //Kiểm tra đã tồn tại user hay chưa
        try {
            userDAO.get(username);
            throw new UserServiceException("This user is already exist!");
        } catch (NotExistDataException nedExForGet) {
            //Nếu chưa thì tiếp tục
        } catch (DAOException exByGet) {
            throw new UserServiceException(exByGet.getMessage(), exByGet.getCause());
        }
        //Tạo User mới và trả về
        try {
            return userDAO.create(newUser);
        } catch (DAOException exByCreate) {
            throw new UserServiceException(exByCreate.getMessage());
        }
    }
    
    /**
     * Kiểm tra tài khoản và mật khẩu để đăng nhập
     * @param username username được nhập
     * @param password password được nhập
     * @return Các thông tin của User nếu tài khoản và mật khẩu đúng
     * @throws UserServiceException Xảy ra khi (1) User không tồn tại, 
     * (2) password sai, (3) Việc thực thi các câu lệnh lỗi
     * @see IUserDAO#get(String) 
     * @see com.noteapp.common.dao.DAOException
     */
    public User checkPassword(String username, String password) throws UserServiceException {
        getInstanceOfDAO();
        try {
            //Lấy tài khoản
            User user = userDAO.get(username);
            //Kiểm tra mật khẩu
            if(password.equals(user.getPassword())) {
                return user;
            } else {
                throw new UserServiceException("Password is false!");
            }
        } catch (DAOException exByGet) {
            throw new UserServiceException(exByGet.getMessage(), exByGet.getCause());
        }
    }
    
    /**
     * Cập nhật mật khẩu của User
     * @param username username của user
     * @param newPassword password mới của user
     * @throws UserServiceException Xảy ra khi (1) User không tồn tại,
     * (2) Các câu lệnh bị lỗi
     * @see IUserDAO#get(String)
     * @see IUserDAO#update(User) 
     * @see com.noteapp.common.dao.DAOException
     */
    public void updatePassword(String username, String newPassword) throws UserServiceException {
        getInstanceOfDAO();
        try {
            User user = userDAO.get(username);
            user.setPassword(newPassword);
            userDAO.update(user);
        } catch (DAOException ex) {
            throw new UserServiceException(ex.getMessage(), ex.getCause());
        }
    }
    
    /**
     * Cập nhật các thông tin của user
     * @param user User cần cập nhật
     * @return User sau khi cập nhật
     * @throws UserServiceException Xảy ra khi câu lệnh hoặc kết nối bị lỗi
     * @see IUserDAO#update(User)
     * @see com.noteapp.common.dao.DAOException
     */
    public User update(User user) throws UserServiceException {
        getInstanceOfDAO();
        try {
            userDAO.update(user);
            return user;
        } catch (DAOException exByUpdate) {
            throw new UserServiceException(exByUpdate.getMessage());
        }
    }
    
    /**
     * Lấy địa chỉ email xác thực của User
     * @param username username của User cần lấy
     * @return Một {@link Email} chứa địa chỉ email xác thực của User
     * @throws UserServiceException Xảy ra khi không thể lấy dữ liệu của User
     * @see IUserDAO#get(String)
     * @see com.noteapp.common.dao.DAOException
     */
    public Email getVerificationEmail(String username) throws UserServiceException {
        getInstanceOfDAO();
        try { 
            User user = userDAO.get(username);
            return user.getEmail();
        } catch (DAOException exByGet) {
            throw new UserServiceException(exByGet.getMessage());
        }
    }
}