package com.smieciolapp.ui.login;

import android.text.TextUtils;
import android.util.Patterns;

public class RegisterValidation {

    private boolean email = false;
    private boolean passwords = false;

    RegisterValidation(){
        this.email = false;
        this.passwords = false;
    }

    boolean isNamesValid(String fName){
        return (!fName.equals(""));
    }

    boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(email.matches(emailPattern)){
            this.email = true;
            return true;
        } else return false;
    }

    boolean arePassowrdsSame(String pass1, String pass2){
        if(pass1.equals(pass2)){
            this.passwords = true;
            return true;
        } else return false;
    }

    public boolean isEmail() {
        return email;
    }

    public boolean isPasswords() {
        return passwords;
    }
}
