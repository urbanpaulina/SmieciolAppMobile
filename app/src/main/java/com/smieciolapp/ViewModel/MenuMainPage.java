package com.smieciolapp.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smieciolapp.R;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.autentication.login.LoginActivity;
import com.smieciolapp.data.model.User;

public class MenuMainPage extends AppCompatActivity  {

    private DrawerLayout mlayout , m2layout;
    FirebaseAuthClass authClass = new FirebaseAuthClass();

    User user;
    BottomNavigationView bottomNavView;
    TextView userName;
    private String email="";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main_page);


        if(authClass.getCurrentUser()==null){
            Intent intent = new Intent(MenuMainPage.this, Start_screen.class);
            startActivity(intent);
        }


        Toolbar toolbar=findViewById(R.id.toolbar);
        userName = findViewById(R.id.userName);

        //toolbar
        setSupportActionBar(toolbar);

        //intent z poprzedniego activity
        intent = getIntent();
        email = intent.getStringExtra("document");
        Toast.makeText(getApplicationContext(),"email:" + email,Toast.LENGTH_SHORT).show();

        //inicjalizacja usera

        user = getUserFromFirestore(email);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

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
                    Intent intent = new Intent(MenuMainPage.this, ProfilePage.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    return true;
                case R.id.wyloguj:
                    authClass.logOut();
                    Intent intent1 = new Intent(MenuMainPage.this, LoginActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    return true;
            }
        } else {
            switch(item.getItemId()){
                case R.id.Profil:
                    //pobranie danych uzytkownika z firestora
                    Intent intent = new Intent(MenuMainPage.this, ProfilePage.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    return true;
                case R.id.wyloguj:
                    authClass.logOut();
                    Intent intent1 = new Intent(MenuMainPage.this, LoginActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public User getUserFromFirestore(String email){

        DocumentReference docRef = authClass.getDb().collection("users").document(email);
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


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.MainPage:
                    selectedFragment = new MainPage();
                    break;
                case R.id.Scan_Recipt:
                    selectedFragment = new ScanProductsPage();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new ScanProductsPage()).commit();
                    break;
                case R.id.My_Shoppings:
                    selectedFragment = new AddShoppingScan();
                    //selectedFragment = new ScanProductsPage();
                    break;
                case R.id.Add_Product:
                    selectedFragment = new AddProductAdmin();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new AddProductAdmin()).commit();
                    break;
                case R.id.Ranking:
                    selectedFragment = new StatsPage();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new StatsPage()).commit();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container, selectedFragment).commit();
            return true;
        }
    };

}

