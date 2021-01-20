package com.smieciolapp.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.smieciolapp.menu;
import com.smieciolapp.R;
import com.smieciolapp.data.model.User;


public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // polaczenie z firebase
        db = FirebaseFirestore.getInstance();

        //klasa walidacji
        final RegisterValidation registerValidation=new RegisterValidation();

        //pola layoutu
        final EditText usernameEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText confrimPassword = findViewById(R.id.confirm_password);
        final EditText firstName = findViewById(R.id.firstname);
        final EditText lastName = findViewById(R.id.lastname);
        final EditText username = findViewById(R.id.username);
        final TextView error = findViewById(R.id.error);
        final TextView errorName = findViewById(R.id.errorName);
        final TextView errorSurname = findViewById(R.id.errorSurname);
        final TextView errorUsername = findViewById(R.id.errorUsername);

        //pola akcji
        final Button registerButton = findViewById(R.id.register);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);




        //walidacja maila
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!registerValidation.isValidEmail(usernameEditText.getText().toString())){
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

                //sprawdzenie czy wszystkie pola sa wypelnione poprawnie
                if(registerValidation.isNamesValid(firstName.getText().toString()) &&
                        registerValidation.isNamesValid(lastName.getText().toString()) &&
                        registerValidation.isNamesValid(username.getText().toString()) &&
                        registerValidation.isEmail() && registerValidation.isPasswords()){

                    // nowy user z podanymi z forma danymi
                    User user = new User(firstName.getText().toString(),lastName.getText().toString(),username.getText().toString(),
                            usernameEditText.getText().toString(),passwordEditText.getText().toString());

                    //dodanie do bazy - kolekcja users, dokument jako username
                    db.collection("users").document(username.getText().toString().trim()).set(user);

                    //nowe activity
                    Intent intent = new Intent(RegisterActivity.this, menu.class);
                    intent.putExtra("collection","users");
                    intent.putExtra("document",username.getText().toString().trim());
                    startActivity(intent);

                    //jesli dane niepoprawne...
                } else {
                    Toast.makeText(getApplicationContext(),"Coś posżło nie tak",Toast.LENGTH_SHORT).show();
                }




            }
        });


    }




}
