package com.noteapp.user.service.security;

import com.noteapp.user.model.Email;

/**
 * Dịch vụ xác thực bằng email
 * @author Nhóm 17
 */
public class VerificationMailService {
    private MailService mailService;
    private VerificationCodeGenerator codeGenerator;
    private CodeStatus codeStatus;

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
    public VerificationMailService(MailService mailService, VerificationCodeGenerator codeGenerator) {
        this.mailService = mailService;
        this.codeGenerator = codeGenerator;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setCodeGenerator(VerificationCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }
    
    /**
     * Gửi email chứa code xác thực tới địa chỉ email nhận
     * @param toEmail Một {@code Email} chứa địa chỉ nhận
     */
    public void sendCode(Email toEmail) {
        String subject = "Verification Code for Note App";
        codeGenerator.generateVerificationCode();
        String verificationCode = codeGenerator.getVerificationCode();
        String content = "Your verification code is " + verificationCode;
        mailService.sendMail(toEmail, subject, content);
    }
    
    /**
     * Nhận một code đầu vào và kiểm tra trạng thái của code này
     * @param inputCode code người dùng nhập
     */
    public void checkCode(String inputCode) {
        if(codeGenerator.isExpiredCode()) {
            codeStatus = CodeStatus.EXPIRED;
        } else if (!codeGenerator.verifyCode(inputCode)) {
            codeStatus = CodeStatus.FALSE;
        } else {
            codeStatus = CodeStatus.TRUE;
        }
    }

    public CodeStatus getCodeStatus() {
        return codeStatus;
    }
}