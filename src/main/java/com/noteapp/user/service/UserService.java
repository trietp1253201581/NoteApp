package com.noteapp.user.service;

import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dao.NotExistDataException;
import com.noteapp.user.dao.IUserDAO;
import com.noteapp.user.model.Email;
import com.noteapp.user.model.User;

/**
 * Cung cấp các service liên quan tới User
 * @author Nhóm 17
 * @see IUserDAO
 * @see User
 */
public class UserService implements IUserService {
    protected IUserDAO userDAO;
    
    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setUserDAO(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    private void checkNullDAO() throws UserServiceException {
        if (userDAO == null) {
            throw new UserServiceException("DAO is null!");
        }
    }
    
    @Override
    public boolean isUser(String username) throws UserServiceException {
        checkNullDAO();
        try {
            userDAO.get(username);
            return true;
        } catch (NotExistDataException ex1) {
            return false;
        } catch (DAOException ex2) {
            throw new UserServiceException(ex2.getMessage(), ex2.getCause());
        }
    }
    
    @Override
    public User create(User newUser) throws UserServiceException {
        checkNullDAO();
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
    
    @Override
    public User checkUser(String username, String password) throws UserServiceException {
        checkNullDAO();
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
    
    @Override
    public boolean checkLocked(String username) throws UserServiceException {
        checkNullDAO();
        try {
            //Lấy tài khoản
            User user = userDAO.get(username);
            //Kiểm tra mật khẩu
            return user.isLocked();
        } catch (DAOException exByGet) {
            throw new UserServiceException(exByGet.getMessage(), exByGet.getCause());
        }
    }
    
    @Override
    public void updatePassword(String username, String newPassword) throws UserServiceException {
        checkNullDAO();
        try {
            User user = userDAO.get(username);
            user.setPassword(newPassword);
            userDAO.update(user);
        } catch (DAOException ex) {
            throw new UserServiceException(ex.getMessage(), ex.getCause());
        }
    }
    
    @Override
    public User update(User user) throws UserServiceException {
        checkNullDAO();
        try {
            userDAO.update(user);
            return user;
        } catch (DAOException exByUpdate) {
            throw new UserServiceException(exByUpdate.getMessage());
        }
    } 
    
    @Override
    public Email getVerificationEmail(String username) throws UserServiceException {
        checkNullDAO();
        try { 
            User user = userDAO.get(username);
            return user.getEmail();
        } catch (DAOException exByGet) {
            throw new UserServiceException(exByGet.getMessage());
        }
    }
}