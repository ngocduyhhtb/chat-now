package com.nduy.realtimechatapp.Model;

import android.os.Parcel;

import java.io.Serializable;

public class User implements Serializable {
    private String userID;
    private String email;
    private String password;
    private String imageEncode;
    private String displayName;
    private String token;
    private Long isOnline;
    public static final String DISPLAY_NAME = "name";
    public static final String User_ID = "userID";
    public static final String EMAIL_FIELD = "email";
    public static final String PASSWORD_FIELD = "password";
    public static final String IMAGE_FIELD = "image";
    public static final String IS_SIGN_IN = "isLoggedIn";
    public static final String USER_FCM_TOKEN = "userFCMToken";

    public User() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public User(String userID, String email, String password, String imageEncode, String displayName, String token, Long isOnline) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.imageEncode = imageEncode;
        this.displayName = displayName;
        this.token = token;
        this.isOnline = isOnline;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImageEncode() {
        return imageEncode;
    }

    public void setImageEncode(String imageEncode) {
        this.imageEncode = imageEncode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Long isOnline) {
        this.isOnline = isOnline;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imageEncode='" + imageEncode + '\'' +
                ", displayName='" + displayName + '\'' +
                ", token='" + token + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}
