package com.noteapp.service.verification;

/**
 *
 * @author admin
 */
public class Email {
    private String address;
    private String name;
    
    public Email() {
        this.address = "";
        this.name = "";
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
}
