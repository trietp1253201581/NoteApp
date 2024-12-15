package com.noteapp.user.service.security;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Dịch vụ tạo mã xác thực khi được yêu cầu
 * @author Nhóm 17
 */
public abstract class VerificationCodeService {
    protected String verificationCode;
    protected String username;
    protected boolean isExpired;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * Tạo mã xác thực theo nguyên tắc nào đó, bắt buộc phải được implement 
     * bởi các class con
     */
    public abstract void generateVerificationCode();

    /**
     * Kiểm tra code có hợp lệ không
     * @param code code cần kiểm tra
     * @return {@code true} nếu code là giống với code được tạo ra,
     * {@code false} nếu ngược lại
     */
    public boolean verifyCode(String code) {
        return code.equals(verificationCode);
    }
    
    /**
     * Kiểm tra xem code được tạo đã hết hạn chưa
     * @return {@code true} nếu code được tạo đã hết hạn,
     * ngược lại thì {@false}
     */
    public boolean isExpiredCode() {
        return isExpired;
    }
    
    /**
     * Bắt đầu bộ đếm thời gian để thay đổi trạng thái của code
     * từ hợp lệ sang hết hạn (thời gian hiện tại là 3 phút)
     */
    public void startExpiryCodeTimer() {
        scheduler.schedule(() -> isExpired = true, 3, TimeUnit.MINUTES);
    }
}