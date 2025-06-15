package com.example.inventra;

public class UserAuthResult {
    public int userId;
    public String userType, username;

    public UserAuthResult(int userId, String userType, String username) {
        this.userId = userId;
        this.userType = userType;
        this.username = username;
    }
}