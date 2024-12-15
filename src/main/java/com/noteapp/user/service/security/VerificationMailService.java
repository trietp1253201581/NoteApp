package com.noteapp.user.service.security;

import com.noteapp.user.model.Email;

/**
 * Dịch vụ xác thực bằng email
 * @author Nhóm 17
 */
public class VerificationMailService {
    protected MailService mailService;
    protected VerificationCodeService verificationCodeService;
    protected CodeStatus codeStatus;

    /**
     * Các trạng thái có thể có của code xác thực
     */
    public static enum CodeStatus {
        EXPIRED, TRUE, FALSE
    }

    /**
     * Khởi tạo, khai báo dịch vụ email và dịch vụ sinh code tương ứng
     * @param mailService dịch vụ gửi email nào đó
     * @param verificationCodeService dịch vụ sinh code nào đó
     */
    public VerificationMailService(MailService mailService, VerificationCodeService verificationCodeService) {
        this.mailService = mailService;
        this.verificationCodeService = verificationCodeService;
    }
    
    /**
     * Gửi email chứa code xác thực tới địa chỉ email nhận
     * @param toEmail Một {@code Email} chứa địa chỉ nhận
     */
    public void sendCode(Email toEmail) {
        String subject = "Verification Code for Note App";
        verificationCodeService.generateVerificationCode();
        String verificationCode = verificationCodeService.getVerificationCode();
        String content = "Your verification code is " + verificationCode;
        mailService.sendMail(toEmail, subject, content);
    }
    
    /**
     * Nhận một code đầu vào và kiểm tra trạng thái của code này
     * @param inputCode code người dùng nhập
     */
    public void checkCode(String inputCode) {
        if(verificationCodeService.isExpiredCode()) {
            codeStatus = CodeStatus.EXPIRED;
        } else if (!verificationCodeService.verifyCode(inputCode)) {
            codeStatus = CodeStatus.FALSE;
        } else {
            codeStatus = CodeStatus.TRUE;
        }
    }

    public CodeStatus getCodeStatus() {
        return codeStatus;
    }
}