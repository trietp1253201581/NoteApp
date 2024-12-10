package com.noteapp.user.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Một transfer cho dữ liệu của các user 
 * @author Nhóm 17
 * @version 1.0
 */
public class User {
    private String name;
    private String username;
    private String password;
    private Date birthday;
    private String school;
    private Gender gender;
    private Email email;
    
    /**
     * Các giới tính có thể có của User
     */
    public static enum Gender {
        MALE, FEMALE, OTHER;
    }

    public User() {
        this.name = "";
        this.username = "";
        this.password = "";
        this.birthday = Date.valueOf(LocalDate.MIN);
        this.school = "";
        this.gender = Gender.MALE;
        this.email = new Email();
    }

    public User(String username, String password, Email email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = "";
        this.birthday = Date.valueOf(LocalDate.MIN);
        this.school = "";
        this.gender = Gender.MALE;
    }

    public User(String name, String username, String password, Date birthday, String school, Gender gender, Email email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.school = school;
        this.gender = gender;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
    
    /**
     * Kiểm tra một {@link User} có mang giá trị mặc định không
     * @return {@code true} nếu username là một chuỗi rỗng, ngược lại là {@code false}
     */
    public boolean isDefaultValue() {
        return "".equals(username);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.username);
        return hash;
    }
    
    /**
     * So sánh một {@link Object} với {@link User} này.
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
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return !Objects.equals(this.username, other.username);
    }

    @Override
    public String toString() {
        return "User{" + 
                "name=" + name + 
                ", username=" + username + 
                ", password=" + password + 
                ", birthday=" + birthday + 
                ", school=" + school + 
                ", gender=" + gender + 
                ", email=" + email + 
                '}';
    }
}