package com.smieciolapp.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smieciolapp.R;
import com.smieciolapp.autentication.login.LoginActivity;

public class Password_reminder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reminder);

        final Button powrot_button = findViewById(R.id.powrot_button);
        final TextView tekst  = findViewById(R.id.tekst);
        final TextView wpisz_adres = findViewById(R.id.wpisz_adres);
        final EditText przypomnienie_email = findViewById(R.id.przypomnienie_email);
        final Button resetuj_button = findViewById(R.id.resetuj_button);
        final ImageView segregacja2 = findViewById(R.id.segregacja2);

        powrot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });



    }


}