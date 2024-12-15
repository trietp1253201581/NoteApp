package com.noteapp.user.service.security;

import com.noteapp.user.model.Email;
import java.util.List;

/**
 * Cung cấp các dịch vụ liên quan tới Email
 * @author Nhóm 17
 */
public interface MailService {
    /**
     * Gửi một email với nội dung và tiêu đề tới một địa chỉ
     * @param toEmail Một {@link Email} chứa địa chỉ Email nhận
     * @param subject tiêu đề của email
     * @param content nội dung của email
     * @return {@code true} nếu gửi thành công, {@code false} nếu ngược lại 
     * @see Email
     */
    boolean sendMail(Email toEmail, String subject, String content);
    
    /**
     * Gửi một email với nội dung và tiêu đề tới nhiều địa chỉ
     * @param toEmail Một list chứa các {@link Email} chứa địa chỉ Email nhận
     * @param subject tiêu đề của email
     * @param content nội dung của email
     * @return {@code true} nếu gửi thành công, {@code false} nếu ngược lại 
     * @see Email
     */
    boolean sendMail(List<Email> toEmails, String subject, String content);
}
