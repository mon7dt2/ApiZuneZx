package com.ptit.edu.store.utils;

public class PasswordValidate {
    public static boolean isPasswordValidate(String password){
        return password.length() >= 6;
    }
}
