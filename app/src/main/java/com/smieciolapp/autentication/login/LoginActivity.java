package com.smieciolapp.autentication.login;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.smieciolapp.ViewModel.Password_reminder;
import com.smieciolapp.ViewModel.WelcomePage;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.ViewModel.MenuMainPage;
import com.smieciolapp.R;
import com.smieciolapp.autentication.registration.RegisterActivity;
import com.smieciolapp.data.model.RegisterValidation;

import io.paperdb.Paper;


public class LoginActivity extends AppCompatActivity {

    TextView smieciol_napis,Login,Haslo,przypomnienie,brak_konta,zarejestruj_sie;
    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton ;
    ImageView segregacja;
    ProgressBar loadingProgressBar ;

    FirebaseAuthClass auth = new FirebaseAuthClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, MenuMainPage.class);
            intent.putExtra("collection","users");
            intent.putExtra("document",auth.getCurrentUser().getEmail());
            startActivity(intent);
        }


        setContentView(R.layout.activity_login);

        smieciol_napis=findViewById(R.id.smieciol_napis);
        Login=findViewById(R.id.Login);
        Haslo=findViewById(R.id.Haslo);
        przypomnienie=findViewById(R.id.przypomnienie);
        przypomnienie.setPaintFlags(przypomnienie.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        brak_konta=findViewById(R.id.brak_konta);
        zarejestruj_sie=findViewById(R.id.zarejestruj_sie);
        zarejestruj_sie.setPaintFlags(zarejestruj_sie.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        segregacja=findViewById(R.id.segregacja);
        loginButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
                    emailEditText.setError("Email jest wymagany");

                }
                if (TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
                    passwordEditText.setError("Hasło jest wymagane");
                    return;
                }
                if (passwordEditText.getText().toString().trim().length() < 6) {
                    passwordEditText.setError("Hasło musi mieć więcej niż 6 znaków");
                    return;
                }

                loadingProgressBar.setVisibility(View.VISIBLE);
                //pobranie uzytkownika wpisanego w formie i walidacja danych jesli ok to nowe activity
                try{
                   auth.logIn(emailEditText.getText().toString().trim(),passwordEditText.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {

                           Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                           loadingProgressBar.setVisibility(View.INVISIBLE);
                       }
                   }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                               Intent intent = new Intent(LoginActivity.this, MenuMainPage.class);
                               intent.putExtra("collection","users");
                               intent.putExtra("document",emailEditText.getText().toString().trim());
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);
                       }
                   });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        zarejestruj_sie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        przypomnienie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Password_reminder.class));
            }
        });
    }





}
