package com.noteapp.controller;

import com.noteapp.service.NoteAppService;

/**
 * Một class đại diện cho các controller yêu cầu sử dụng dịch vụ của
 * hệ thống NoteApp
 * @author Nhóm 17
 */
public abstract class RequestServiceController extends Controller {
    protected NoteAppService noteAppService;
}
