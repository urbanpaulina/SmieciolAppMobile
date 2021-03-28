package com.smieciolapp.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.smieciolapp.R;
import com.smieciolapp.autentication.login.LoginActivity;

public class Start_screen extends AppCompatActivity {

    private static int Time_Out = 3700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent (Start_screen.this, LoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },Time_Out);
    }
}