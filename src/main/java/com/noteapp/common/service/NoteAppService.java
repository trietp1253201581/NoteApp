package com.noteapp.common.service;

import com.noteapp.note.service.INoteService;
import com.noteapp.note.service.IShareNoteService;
import com.noteapp.note.service.io.FileIOService;
import com.noteapp.user.service.IAdminService;
import com.noteapp.user.service.IUserService;
import com.noteapp.user.service.security.VerificationMailService;

/**
 * Cung cấp các service ở mức tổng quan của NoteApp System
 * @author Nhóm 17
 * @see IUserService
 * @see IAdminService
 * @see INoteService
 * @see IShareNoteService
 * @see VerificationMailService
 * @see FileIOService
 */
public class NoteAppService {
    private IUserService userService;
    private IAdminService adminService;
    private INoteService noteService;
    private IShareNoteService shareNoteService;
    private VerificationMailService verificationMailService;
    private FileIOService fileIOService;

    public NoteAppService() {
        userService = null;
        adminService = null;
        noteService = null;
        shareNoteService = null;
        verificationMailService = null;
        fileIOService = null;
    }
    
    public IUserService getUserService() throws NoteAppServiceException {
        if (userService == null) {
            throw new CausedBySystemException("Service has not inited!");
        }
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public IAdminService getAdminService() throws NoteAppServiceException {
        if (adminService == null) {
            throw new CausedBySystemException("Service has not inited!");
        }
        return adminService;
    }

    public void setAdminService(IAdminService adminService) {
        this.adminService = adminService;
    }

    public INoteService getNoteService() throws NoteAppServiceException {
        if (noteService == null) {
            throw new CausedBySystemException("Service has not inited!");
        }
        return noteService;
    }

    public void setNoteService(INoteService noteService) {
        this.noteService = noteService;
    }

    public IShareNoteService getShareNoteService() throws NoteAppServiceException {
        if (shareNoteService == null) {
            throw new CausedBySystemException("Service has not inited!");
        }
        return shareNoteService;
    }

    public void setShareNoteService(IShareNoteService shareNoteService) {
        this.shareNoteService = shareNoteService;
    }

    public VerificationMailService getVerificationMailService() throws NoteAppServiceException {
        if (verificationMailService == null) {
            throw new CausedBySystemException("Service has not inited!");
        }
        return verificationMailService;
    }

    public void setVerificationMailService(VerificationMailService verificationMailService) {
        this.verificationMailService = verificationMailService;
    }

    public FileIOService getFileIOService() throws NoteAppServiceException {
        if (fileIOService == null) {
            throw new CausedBySystemException("Service has not inited!");
        }
        return fileIOService;
    }

    public void setFileIOService(FileIOService fileIOService) {
        this.fileIOService = fileIOService;
    }
}
