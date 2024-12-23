package com.noteapp.user.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Biểu diễn cho các Email dùng để gửi nhận
 * @author Nhóm 17
 * @version 1.0
 */
public class Email {
    private String address;
    private String name;
    
    protected static final String EMAIL_ADDRESS_PATTERN = "^([a-zA-Z0-9._]+)@([a-zA-Z0-9_.]+)\\.([a-zA-Z]{2,})$";
    
    public Email() {
        this.address = "";
        this.name = "";
    }

    public Email(String address) {
        this.address = address;
    }

    public Email(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Kiểm tra một {@link Email} có mang giá trị mặc định không
     * @return {@code true} nếu address là một chuỗi rỗng, ngược lại là {@code false}
     */
    public boolean isDefaultValue() {
        return "".equals(address);
    }
    
    /**
     * Kiểm tra địa chỉ Email của đối tượng {@link Email} này
     * có hợp lệ không
     * @return 
     */
    public boolean checkAddress() {
        Pattern emailAddPattern = Pattern.compile(EMAIL_ADDRESS_PATTERN);
        Matcher matcher = emailAddPattern.matcher(this.address);
        return matcher.matches();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.address);
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     * So sánh một {@link Object} với {@link Email} này.
     * @param obj Object cần so sánh
     * @return {@code true} nếu obj là một thể hiện của Email
     * và thuộc tính {@code address} của obj bằng với
     * {@code address} của Note, {@code false} nếu ngược lại.
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
        if (!(obj instanceof Email)) {
            return false;
        }
        final Email other = (Email) obj;
        return Objects.equals(this.address, other.address);
    }

    @Override
    public String toString() {
        return "Email{" + 
                "address=" + address + 
                ", name=" + name + 
                '}';
    }
}
