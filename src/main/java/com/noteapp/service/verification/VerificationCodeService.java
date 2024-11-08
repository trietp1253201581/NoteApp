package com.noteapp.service.verification;

/**
 *
 * @author admin
 */
public abstract class VerificationCodeService {
    protected String verificationCode;
    protected String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public abstract void generateVerificationCode();

    public boolean verifyCode(String code) {
        return code.equals(verificationCode);
    }
}