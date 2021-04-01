package com.smieciolapp.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.smieciolapp.R;
import com.smieciolapp.autentication.login.LoginActivity;

public class Password_reminder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reminder);

        FirebaseAuth firebaseAuth;

        final Button powrot_button = findViewById(R.id.powrot_button);
        final TextView tekst  = findViewById(R.id.tekst);
        final TextView wpisz_adres = findViewById(R.id.wpisz_adres);
        final EditText przypomnienie_email = findViewById(R.id.przypomnienie_email);
        final Button resetuj_button = findViewById(R.id.resetuj_button);
        final ImageView segregacja2 = findViewById(R.id.segregacja2);

        firebaseAuth = FirebaseAuth.getInstance();

        powrot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        resetuj_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail(przypomnienie_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Password_reminder.this, "Wiadomość wysłana", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(Password_reminder.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        przypomnienie_email.getText().clear();
                    }
                });
            }
        });



    }


}