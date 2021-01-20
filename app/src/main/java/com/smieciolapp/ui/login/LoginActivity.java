package com.smieciolapp.ui.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smieciolapp.menu;
import com.smieciolapp.R;
import com.smieciolapp.data.model.User;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private boolean validationOk = false;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton ;
    ProgressBar loadingProgressBar ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        usernameEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);

        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pobranie uzytkownika wpisanego w formie i walidacja danych jesli ok to nowe activity
               getUserFromFirestore("users",usernameEditText.getText().toString().trim(),passwordEditText.getText().toString().trim());
            }
        });
    }

    public boolean getUserFromFirestore(String colId, String docId, final String password){

        DocumentReference docRef = db.collection("users").document(docId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if(user!=null){

                    if(user.getPassword().equals(password)){
                        validationOk = true;
                        Intent intent = new Intent(LoginActivity.this, menu.class);
                        intent.putExtra("collection","users");
                        intent.putExtra("document",usernameEditText.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),"Podałeś błędne hasło",Toast.LENGTH_SHORT).show();
                        validationOk = false;
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),"Nie ma takiego użytkowinka",Toast.LENGTH_SHORT).show();
                    validationOk = false;
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Nie ma takiego użytkownika",Toast.LENGTH_SHORT).show();
                validationOk = false;
            }
        });
        return validationOk;
    }

}
