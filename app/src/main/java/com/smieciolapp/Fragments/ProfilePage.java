package com.smieciolapp.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.smieciolapp.R;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.autentication.login.LoginActivity;
import com.smieciolapp.data.model.User;

public class ProfilePage extends AppCompatActivity {

    TextView fName, sName, uName, Email;
    Button btn;

    FirebaseAuthClass authClass = new FirebaseAuthClass();

    //obecnie zalogowany user
    User user = new User();
    boolean isAdmin = false;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btn = (Button)findViewById(R.id.back);


        //pobranie usera z inteta
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        try{
            isAdmin = (boolean) intent.getBooleanExtra("admin",false);
        } catch (Exception e){

        }

        // jesli user zalogowany nie jako admin to ...
       if(user != null && user.getType()==2) {

           if(!isAdmin)
           {
               setContentView(R.layout.activity_profile);

               btn = findViewById(R.id.back);
               btn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(ProfilePage.this, MainPage.class);
                       startActivity(intent);
                   }
               });

           }
           else
               setContentView(R.layout.activity_profile_user_admin);


           btn = findViewById(R.id.back);
           btn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(ProfilePage.this, MenuMainPage.class);
                   startActivity(intent);
               }
           });

           uName = findViewById(R.id.userName);
           fName = findViewById(R.id.firstName);
           sName = findViewById(R.id.lastName);


           uName.setText("Nazwa u??ytkownika:" + user.getUserName());
           fName.setText("Imi??: "+ user.getFirstName());
           sName.setText("Nazwisko: "+user.getLastName());

       }
       // jesli admin ...
       else if(user!=null && user.getType()==1){

           setContentView(R.layout.activity_profile_admin);

           recyclerView = findViewById(R.id.viewer);

           Query query = authClass.getDb().collection("users");
           FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                                                        .setQuery(query,User.class).build();
           adapter = new FirestoreRecyclerAdapter<User,UserViewHolder>(options){

               // Stworzenie view dla ka??dego wy??wietlanego elementu
               @NonNull
               @Override
               public UserViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
                   View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_viewer , parent, false);

                   return new UserViewHolder(view);
               }

               // Dodanie do widoku uzytkownika i przycisku, z obs??ug?? wciskania
               @Override
               protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {

                   holder.userEmail.setText(model.getEmail());

                   Button btn = holder.itemView.findViewById(R.id.checkUser);
                   btn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent intent = new Intent(ProfilePage.this, ProfilePage.class);
                           intent.putExtra("user",model);
                           intent.putExtra("admin",true);
                           startActivity(intent);
                       }
                   });
               }
           };

           recyclerView.setLayoutManager(new LinearLayoutManager(this));
           recyclerView.setHasFixedSize(true);
           recyclerView.setAdapter(adapter);

       } else {
           Toast.makeText(this,"Nie mo??na uzyska?? dost??pu do profilu",Toast.LENGTH_SHORT).show();
       }

    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView  userEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.uName);
        }
    }

    // metody od??wie??aj??ce liste
    @Override
    protected void onStart() {
        super.onStart();
        if(user!=null && user.getType()==1) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(user!=null && user.getType()==1) {
            adapter.stopListening();
        }
    }
}
