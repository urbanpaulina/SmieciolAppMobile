package com.smieciolapp.ViewModel;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smieciolapp.R;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.autentication.login.LoginActivity;
import com.smieciolapp.data.model.User;

public class MenuMainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mlayout , m2layout;
    FirebaseAuthClass authClass = new FirebaseAuthClass();

    User user;
    TextView userName;
    private String email="";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if(authClass.getCurrentUser()==null){
            Intent intent = new Intent(MenuMainPage.this, WelcomePage.class);
            startActivity(intent);
        }

        setContentView(R.layout.menu_main_page);
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

        mlayout = (DrawerLayout) findViewById(R.id.left_menu);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mtoggle=new ActionBarDrawerToggle(this,mlayout,toolbar,R.string.open,R.string.close);
        mlayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        if (savedInstanceState == null) {
           // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                   // new MainPage()).commit();
            navigationView.setCheckedItem(R.id.MainPage);
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
                    Intent intent = new Intent(MenuMainPage.this, ProfilePage.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    return true;
                case R.id.wyloguj:
                    authClass.logOut();
                    Intent intent1 = new Intent(MenuMainPage.this, WelcomePage.class);
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
                    Intent intent1 = new Intent(MenuMainPage.this, WelcomePage.class);
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.MainPage:
               getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new MainPage()).commit();
                break;
            case R.id.Scan_Recipt:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new ScanProductsPage()).commit();
                break;
            case R.id.My_Shoppings:
               // getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new oKlubie_fragment()).commit();
                break;
            case R.id.Add_Product:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new AddProductAdmin()).commit();
                break;
            case R.id.Ranking:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new StatsPage()).commit();
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
            // wyczyść stos, wyjdź z appki
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }


}

