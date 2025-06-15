package com.example.inventra.admin.Profile;

public class User {
    String username;
    String email;
    String created_at;
    String user_type, profile_pic;

    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getCreated_at() { return created_at; }
    public String getUser_type() { return user_type; }
}
