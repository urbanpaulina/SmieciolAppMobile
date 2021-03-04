package com.smieciolapp.autentication.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.data.model.RegisterValidation;
import com.smieciolapp.ViewModel.MenuMainPage;
import com.smieciolapp.R;


public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //FirebaseAuthClass
        final FirebaseAuthClass authClass = new FirebaseAuthClass();

        // polaczenie z firebase
        db = FirebaseFirestore.getInstance();

        //klasa walidacji
        final RegisterValidation registerValidation=new RegisterValidation();

        //pola layoutu
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText confrimPassword = findViewById(R.id.confirm_password);
        final EditText firstName = findViewById(R.id.firstname);
        final EditText lastName = findViewById(R.id.lastname);
        final EditText username = findViewById(R.id.username);
        final TextView error = findViewById(R.id.error);
        final TextView errorUsername = findViewById(R.id.errorUsername);

        //pola akcji
        final Button registerButton = findViewById(R.id.register);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        //walidacja maila
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!registerValidation.isValidEmail(emailEditText.getText().toString())){
                    errorUsername.setText(R.string.invalid_mail);

                } else
                    errorUsername.setText("");
            }
        });


        // sprawdzenie czy hasła są takie same
        confrimPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!registerValidation.arePassowrdsSame(passwordEditText.getText().toString(), confrimPassword.getText().toString())){
                    error.setText(R.string.not_same_passwords);

                } else {
                    error.setText("");
                }
            }
        });

        // po kliknieciu zarejetruj
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TO DO - walidacja aby nie móc kliknąć przycisku - opcjonalnie
                    authClass.signUp(emailEditText.getText().toString().trim(),passwordEditText.getText().toString().trim(),
                            firstName.getText().toString().trim(),lastName.getText().toString().trim(), username.getText().toString().trim(),2).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //nowe activity
                            Intent intent = new Intent(RegisterActivity.this, MenuMainPage.class);
                            intent.putExtra("collection","users");
                            intent.putExtra("document",emailEditText.getText().toString().trim());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //jesli dane niepoprawne...
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });


            } // onClick
        });


    }




}
