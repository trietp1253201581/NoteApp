package com.noteapp.user.service.security;

import java.util.Random;

/**
 * Tạo mã xác thực ngẫu nhiên gồm 6 chữ số
 * @author Nhóm 17
 */
public class SixNumVerificationCodeService extends VerificationCodeService {
    
    @Override
    public void generateVerificationCode() {
        Random random = new Random();
        verificationCode = String.format("%06d", random.nextInt(999999));
        startExpiryCodeTimer();
    }
}