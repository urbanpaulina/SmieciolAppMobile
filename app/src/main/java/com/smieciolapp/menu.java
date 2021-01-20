package com.smieciolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smieciolapp.data.model.User;
import com.smieciolapp.ui.login.LoginActivity;

import java.io.Serializable;

public class menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mlayout , m2layout;
    FirebaseFirestore db;
    User user;
    TextView fname;
    TextView sname;
    private String username="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen_logged);
        Toolbar toolbar=findViewById(R.id.toolbar);

        //toolbar
        setSupportActionBar(toolbar);

        //baza
        db = FirebaseFirestore.getInstance();

        //intent z poprzedniego activity
        Intent intent = getIntent();
        username = intent.getStringExtra("document");

        //inicjalizacja usera
        user = getUserFromFirestore("users",username);

        mlayout = (DrawerLayout) findViewById(R.id.left_menu);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mtoggle=new ActionBarDrawerToggle(this,mlayout,toolbar,R.string.open,R.string.close);
        mlayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        if (savedInstanceState == null) {
           // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                   // new glowna()).commit();
            navigationView.setCheckedItem(R.id.glowna);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.top_menu,menu);
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(!user.getUserName().equals("admin"))
        {
            switch(item.getItemId()){
                case R.id.Profil:
                    //pobranie danych uzytkownika z firestora
                    Intent intent = new Intent(menu.this, profil.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    return true;
                case R.id.wyloguj:
                    Toast.makeText(this,"Tutaj bedzie sie mozna wylogowac",Toast.LENGTH_SHORT).show();
                    return true;
            }
        } else {
            switch(item.getItemId()){
                case R.id.Profil:
                    //pobranie danych uzytkownika z firestora
                    Intent intent = new Intent(menu.this, profil.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    return true;
                case R.id.wyloguj:
                    Toast.makeText(this,"Tutaj bedzie sie mozna wylogowac",Toast.LENGTH_SHORT).show();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.glowna:
               // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new glowna_fragment()).commit();
                break;
            case R.id.skanuj_paragon:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new scan()).commit();
                break;
            case R.id.moje_zakupy:
               // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new oKlubie_fragment()).commit();
                break;
            case R.id.dodaj_produkt:
               // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new aktu()).commit();
                break;
            case R.id.grupy:
               // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new cennik_fragment()).commit();
                break;
        }
        mlayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed(){
        if(mlayout.isDrawerOpen(GravityCompat.START)){
            mlayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public User getUserFromFirestore(String colId, String docId){

        DocumentReference docRef = db.collection("users").document(docId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               User user1 = documentSnapshot.toObject(User.class);
                if(user1!=null){
                    user = user1;
                }
                else {
                    Toast.makeText(getApplicationContext(),"Coś poszło nie tak",Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Coś poszło nie tak",Toast.LENGTH_SHORT).show();
            }
        });
        return user;
    }

}

