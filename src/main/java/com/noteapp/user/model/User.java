package com.noteapp.user.model;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Một transfer cho dữ liệu của các user 
 * @author Nhóm 17
 * @version 1.0
 */
public class User extends AbstractUser {
    private String name;
    private Date birthday;
    private String school;
    private Gender gender;
    private Email email;
    private boolean locked;
    
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
        this.locked = false;
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

    public User(String name, String username, String password, Date birthday, String school, Gender gender, Email email, boolean locked) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        this.school = school;
        this.gender = gender;
        this.email = email;
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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
                ", locked=" + locked +
                '}';
    }
}