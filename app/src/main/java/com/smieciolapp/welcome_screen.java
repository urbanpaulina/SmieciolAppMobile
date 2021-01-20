package com.smieciolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.smieciolapp.ui.login.LoginActivity;
import com.smieciolapp.ui.login.RegisterActivity;

public class welcome_screen extends AppCompatActivity {
    private Button loginId, registerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        loginId = findViewById(R.id.btnId);
        loginId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(welcome_screen.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerId = findViewById(R.id.registerId);
        registerId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(welcome_screen.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


}
