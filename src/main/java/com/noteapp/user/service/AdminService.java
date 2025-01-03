package com.noteapp.user.service;

import com.noteapp.dao.DAOException;
import com.noteapp.dao.NotExistDataException;
import com.noteapp.service.CausedBySystemException;
import com.noteapp.service.CausedByUserException;
import com.noteapp.service.NoteAppServiceException;
import com.noteapp.user.dao.IAdminDAO;
import com.noteapp.user.dao.IUserDAO;
import com.noteapp.user.model.Admin;
import com.noteapp.user.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author admin
 */
public class AdminService implements IAdminService {
    protected IUserDAO userDAO;
    protected IAdminDAO adminDAO;
    
    public AdminService(IUserDAO userDAO, IAdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
    }

    public void setUserDAO(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setAdminDAO(IAdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }
    
    private void checkNullDAO() throws NoteAppServiceException {
        if (adminDAO == null || userDAO == null) {
            throw new CausedBySystemException("DAO is null!");
        }
    }
    
    @Override
    public boolean isAdmin(String username) throws NoteAppServiceException {
        checkNullDAO();
        try {
            adminDAO.get(username);
            return true;
        } catch (NotExistDataException ex1) {
            return false;
        } catch (DAOException ex2) {
            throw new CausedBySystemException(ex2.getMessage(), ex2.getCause());
        }
    }
    
    @Override
    public Admin checkAdmin(String username, String password) throws NoteAppServiceException {
        checkNullDAO();
        try {
            //Lấy tài khoản
            Admin admin = adminDAO.get(username);
            //Kiểm tra mật khẩu
            if(password.equals(admin.getPassword())) {
                return admin;
            } else {
                throw new CausedByUserException("Password is false!");
            }
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage(), exByGet.getCause());
        }
    }    
    
    @Override
    public Map<String, Boolean> getAllLockedStatus(String admin) throws NoteAppServiceException {
        checkNullDAO();
        try {
            List<User> users = userDAO.getAll(admin);
            Map<String, Boolean> lockedStatus = new HashMap<>();
            for (User user: users) {
                lockedStatus.put(user.getUsername(), user.isLocked());
            }
            return lockedStatus;
        } catch (DAOException ex) {
            throw new CausedBySystemException(ex.getMessage(), ex.getCause());
        }
    }
    
    @Override
    public void updateLockedStatus(String username, boolean lockedStatus) throws NoteAppServiceException {
        checkNullDAO();
        try {
            User user = userDAO.get(username);
            user.setLocked(lockedStatus);
            userDAO.update(user);
        } catch (DAOException ex) {
            throw new CausedBySystemException(ex.getMessage(), ex.getCause());
        }
    }
}