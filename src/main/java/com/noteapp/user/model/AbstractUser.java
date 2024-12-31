package com.noteapp.user.model;

import java.util.Objects;

/**
 * Một transfer cho dữ liệu của một đối tượng có thể dăng nhập hệ thóng
 * @author Nhóm 17
 * @version 1.0
 */
public abstract class AbstractUser {
    protected String username;
    protected String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.username);
        return hash;
    }

    /**
     * So sánh một {@link Object} với {@link AbstractUser} này.
     * @param obj Object cần so sánh
     * @return {@code true} nếu obj là một thể hiện của User
     * và thuộc tính {@code username} của obj bằng với
     * {@code username} của User, {@code false} nếu ngược lại
     * @see hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractUser)) {
            return false;
        }
        final AbstractUser other = (AbstractUser) obj;
        return Objects.equals(this.username, other.username);
    }
           
    /**
     * Kiểm tra một {@link AbstractUser} có mang giá trị mặc định không
     * @return {@code true} nếu username là một chuỗi rỗng, ngược lại là {@code false}
     */
    public boolean isDefaultValue() {
        return "".equals(username);
    }
}
