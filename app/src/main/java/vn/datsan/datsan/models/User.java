package vn.datsan.datsan.models;

import android.location.Location;

import java.util.Date;

/**
 * Created by xuanpham on 6/13/16.
 */
public class User {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String groups;
    private String location;

    public User() {}

    public User(String name, String email, String phone, String address, String groups, String location) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.groups = groups;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}