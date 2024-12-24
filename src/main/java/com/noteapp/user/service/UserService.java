package com.noteapp.user.service;

import com.noteapp.dao.DAOException;
import com.noteapp.dao.NotExistDataException;
import com.noteapp.service.CausedBySystemException;
import com.noteapp.service.CausedByUserException;
import com.noteapp.service.NoteAppServiceException;
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
    
    private void checkNullDAO() throws NoteAppServiceException {
        if (userDAO == null) {
            throw new CausedBySystemException("DAO is null!");
        }
    }
    
    @Override
    public boolean isUser(String username) throws NoteAppServiceException {
        checkNullDAO();
        try {
            userDAO.get(username);
            return true;
        } catch (NotExistDataException ex1) {
            return false;
        } catch (DAOException ex2) {
            throw new CausedBySystemException(ex2.getMessage(), ex2.getCause());
        }
    }
    
    @Override
    public User create(User newUser) throws NoteAppServiceException {
        checkNullDAO();
        String username = newUser.getUsername();
        //Kiểm tra đã tồn tại user hay chưa
        try {
            userDAO.get(username);
            throw new CausedByUserException("This user is already exist!");
        } catch (NotExistDataException nedExForGet) {
            //Nếu chưa thì tiếp tục
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage(), exByGet.getCause());
        }
        //Tạo User mới và trả về
        try {
            return userDAO.create(newUser);
        } catch (DAOException exByCreate) {
            throw new CausedBySystemException(exByCreate.getMessage());
        }
    }
    
    @Override
    public User checkUser(String username, String password) throws NoteAppServiceException {
        checkNullDAO();
        try {
            //Lấy tài khoản
            User user = userDAO.get(username);
            //Kiểm tra mật khẩu
            if(password.equals(user.getPassword())) {
                return user;
            } else {
                throw new CausedByUserException("Password is false!");
            }
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage(), exByGet.getCause());
        }
    }
    
    @Override
    public boolean checkLocked(String username) throws NoteAppServiceException {
        checkNullDAO();
        try {
            //Lấy tài khoản
            User user = userDAO.get(username);
            //Kiểm tra mật khẩu
            return user.isLocked();
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage(), exByGet.getCause());
        }
    }
    
    @Override
    public void updatePassword(String username, String newPassword) throws NoteAppServiceException {
        checkNullDAO();
        try {
            User user = userDAO.get(username);
            user.setPassword(newPassword);
            userDAO.update(user);
        } catch (DAOException ex) {
            throw new CausedBySystemException(ex.getMessage(), ex.getCause());
        }
    }
    
    @Override
    public User update(User user) throws NoteAppServiceException {
        checkNullDAO();
        try {
            userDAO.update(user);
            return user;
        } catch (DAOException exByUpdate) {
            throw new CausedBySystemException(exByUpdate.getMessage());
        }
    } 
    
    @Override
    public Email getVerificationEmail(String username) throws NoteAppServiceException {
        checkNullDAO();
        try { 
            User user = userDAO.get(username);
            return user.getEmail();
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage());
        }
    }
}