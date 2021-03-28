package com.smieciolapp.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.smieciolapp.R;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.autentication.login.LoginActivity;
import com.smieciolapp.autentication.registration.RegisterActivity;

public class WelcomePage extends AppCompatActivity {
    private Button loginId, registerId;
    FirebaseAuthClass authClass = new FirebaseAuthClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(authClass.getCurrentUser()!=null){
            Intent intent = new Intent(WelcomePage.this, MenuMainPage.class);
            intent.putExtra("collection","users");
            intent.putExtra("document",authClass.getCurrentUser().getEmail());
            startActivity(intent);
        }

        setContentView(R.layout.activity_welcome_page);

        loginId = findViewById(R.id.btnId);
        loginId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerId = findViewById(R.id.registerId);
        registerId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


}
