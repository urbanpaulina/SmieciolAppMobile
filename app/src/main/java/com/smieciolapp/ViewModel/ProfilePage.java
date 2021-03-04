package com.smieciolapp.ViewModel;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smieciolapp.R;
import com.smieciolapp.autentication.FirebaseAuthClass;
import com.smieciolapp.data.model.User;

public class ProfilePage extends AppCompatActivity {

    TextView fName, sName, uName, Email;

    FirebaseAuthClass authClass = new FirebaseAuthClass();

    //obecnie zalogowany user
    User user = new User();

    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //pobranie usera z inteta
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");


        // jesli user zalogowany nie jako admin to ...
       if(user != null && user.getType()==2) {
           setContentView(R.layout.activity_profile);

           fName = findViewById(R.id.firstname);
           sName = findViewById(R.id.lastname);
           uName = findViewById(R.id.email);
           Email = findViewById(R.id.login);

           fName.setText(user.getFirstName());
           sName.setText(user.getLastName());
           uName.setText(user.getUserName());
           Email.setText(user.getEmail());


       }
       // jesli admin ...
       else if(user!=null && user.getType()==1){
           setContentView(R.layout.activity_profile_admin);

           recyclerView = findViewById(R.id.viewer);

           Query query = authClass.getDb().collection("users");
           FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                                                        .setQuery(query,User.class).build();
           adapter = new FirestoreRecyclerAdapter<User,UserViewHolder>(options){

               // Stworzenie view dla każdego wyświetlanego elementu
               @NonNull
               @Override
               public UserViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
                   View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_viewer , parent, false);
                   return new UserViewHolder(view);
               }

               // Dodanie do widoku uzytkownika i przycisku, z obsługą wciskania
               @Override
               protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {

                   holder.uName.setText(model.getUserName());
                   System.out.println(model.getUserName());

                   Button btn = holder.itemView.findViewById(R.id.checkUser);
                   btn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent intent = new Intent(ProfilePage.this, ProfilePage.class);
                           intent.putExtra("user",model);
                           startActivity(intent);
                       }
                   });


               }
           };

           recyclerView.setLayoutManager(new LinearLayoutManager(this));
           recyclerView.setHasFixedSize(true);
           recyclerView.setAdapter(adapter);

       } else {
           Toast.makeText(this,"Nie można uzyskać dostępu do profilu",Toast.LENGTH_SHORT).show();
       }

    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView  uName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            uName = itemView.findViewById(R.id.uName);
        }
    }

    // metody odświeżające liste
    @Override
    protected void onStart() {
        super.onStart();
        if(user!=null && user.getUserName().equals("admin")) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(user!=null && user.getUserName().equals("admin")) {
            adapter.stopListening();
        }
    }
}
