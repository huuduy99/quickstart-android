package com.google.firebase.quickstart.auth.simplifiedcoding.ghostapp;

/**
 * Created by Belal on 4/15/2018.
 */

public class User {
    public String name, email, phone;

    public User(){

    }

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
