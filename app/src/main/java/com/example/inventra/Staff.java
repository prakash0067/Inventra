package com.example.inventra;

public class Staff {
    private int user_id;
    private String username;
    private String email, password, profile_pic, created_at;

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Staff(int user_id, String username, String email, String password) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Staff(int id, String name, String email) {
        this.user_id = id;
        this.username = name;
        this.email = email;
    }

    public int getId() { return user_id; }
    public String getName() { return username; }
    public String getEmail() { return email; }
}

