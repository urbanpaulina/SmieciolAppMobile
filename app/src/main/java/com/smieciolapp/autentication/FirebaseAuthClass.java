package com.smieciolapp.autentication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.smieciolapp.data.model.User;



public class FirebaseAuthClass {

     FirebaseAuth auth;
     FirebaseFirestore db;
     FirebaseUser firebaseUser;
     private User user;



    public FirebaseAuthClass() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = new User();
    }

    public Task<AuthResult> logIn(String email, String password) {
       return auth.signInWithEmailAndPassword(email, password);
    }

    public void logOut(){
         auth.signOut();
    }

    public Task<Void> resetPassword(String email){
        return auth.sendPasswordResetEmail(email);
    }

    public Task<Void> updateUser(FirebaseUser user){
        return auth.updateCurrentUser(user);
    }

    public Task<AuthResult> signUp(String email, String password, String fname, String lname, String uname, int type){
        User user = new User(fname,lname,uname,email,password,type);
        user.setPassword("");
        db.collection("users").document(email).set(user);
       return auth.createUserWithEmailAndPassword(email,password);
    }

    private void setUser(User us){
        user = us;
    }

    private User getUser(){
        return user;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public FirebaseFirestore getDb() {
        return db;
    }


}

