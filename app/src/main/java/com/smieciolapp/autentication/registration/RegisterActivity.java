package com.smieciolapp.autentication.registration;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.autentication.login.LoginActivity;
import com.smieciolapp.data.model.RegisterValidation;
import com.smieciolapp.Fragments.MenuMainPage;
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
        final ScrollView scroll_view =findViewById(R.id.scroll_view);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText confrimPassword = findViewById(R.id.confirm_password);
        final EditText firstName = findViewById(R.id.firstname);
        final EditText lastName = findViewById(R.id.lastname);
        final EditText username = findViewById(R.id.username);
        final TextView error = findViewById(R.id.error);
        final TextView errorUsername = findViewById(R.id.errorUsername);
        final TextView Konto_istnieje = findViewById(R.id.Konto_istnieje);
        final TextView logIn = findViewById(R.id.logIn);
        logIn.setPaintFlags(logIn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        final ImageView segregacja2 = findViewById(R.id.segregacja2);

        //pola akcji
        final Button registerButton = findViewById(R.id.register);
        final ProgressBar progressBar = findViewById(R.id.progresBar);


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
                if (TextUtils.isEmpty(firstName.getText().toString().trim())) {
                    firstName.setError("Imie jest wymagane");

                }
                if (TextUtils.isEmpty(lastName.getText().toString().trim())) {
                    lastName.setError("Nazwisko jest wymagane");

                }
                if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
                    emailEditText.setError("Email jest wymagany");

                }
                if (TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
                    passwordEditText.setError("Hasło jest wymagane");

                }
                if (TextUtils.isEmpty(confrimPassword.getText().toString().trim())) {
                    confrimPassword.setError("Ptwierdzenie jest wymagane");
                    return;
                }

                if (!passwordEditText.getText().toString().trim().equals(confrimPassword.getText().toString().trim()) ) {
                    confrimPassword.setError("Hasła muszą być identyczne");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

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
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

            } // onClick
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });


    }




}
