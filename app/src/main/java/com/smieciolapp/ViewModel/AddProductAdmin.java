package com.smieciolapp.ViewModel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smieciolapp.R;
import com.smieciolapp.data.model.Product;
import com.smieciolapp.data.model.ScanBarcode;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


// TO DO waga nie przyjmuje wartosci po przecinku

public class AddProductAdmin extends Fragment  implements View.OnClickListener {
    EditText NameProduct, WeightProduct, Barcode, Category;
    Button addProduct;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    DatabaseReference Product_Ref;
    Button scanProd;
    String productId;

    public AddProductAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_product_admin, container, false);

        NameProduct = view.findViewById(R.id.etName);
        WeightProduct = view.findViewById(R.id.etWeight);
        Barcode = view.findViewById(R.id.etBarcode);
        Category = view.findViewById(R.id.etCategory);
        addProduct = view.findViewById(R.id.addButton);
        addProduct.setOnClickListener(this);
        scanProd = (Button) view.findViewById(R.id.scanProd);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Product_Ref = FirebaseDatabase.getInstance().getReference().child("Products");


        // on click skanuj produkt
        scanProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanBarcode scanBarcode = new ScanBarcode();
                scanBarcode.scanBarcode(getActivity(),AddProductAdmin.this);
            }
        });


        return view;



    }

    private boolean hasValidationErrors(String name, String weight, String barcode) {
        if (name.isEmpty()) {
            NameProduct.setError("Wpisz nazwę produktu");
            NameProduct.requestFocus();
            return true;
        }
        if (weight.isEmpty()) {
            WeightProduct.setError("Waga jest wymagana");
            WeightProduct.requestFocus();
            return true;
        }
        if (barcode.isEmpty()) {
            Barcode.setError("Wymagany jest kod kreskowy");
            Barcode.requestFocus();
            return true;
        }
        //if (barcode.length() < 12) {
            //Barcode.setError("Barcode must have 12 numbers");
            //return true;
        //}
        //if (barcode.length() > 12) {
          //  Barcode.setError("Barcode must have 12 numbers");
          //  return true;
        //}
        return false;
    }


    public void addproduct() {

        String name = NameProduct.getText().toString();
        String weight = WeightProduct.getText().toString();
        String barcode = Barcode.getText().toString();
        String category = Category.getText().toString();
       // double w = Double.parseDouble(weight);

        if (!hasValidationErrors(name, weight, barcode)) {


            name = NameProduct.getText().toString();
            weight = WeightProduct.getText().toString();
            barcode = Barcode.getText().toString();


            Product products = new Product(barcode, name, Double.parseDouble(weight), true, barcode, category);
            Product_Ref.push().setValue(products);


            Toast.makeText(AddProductAdmin.this.getActivity().getApplicationContext(), "Product has been added", Toast.LENGTH_SHORT).show();

        }
    }

        @Override
        public void onClick (View v){
            addproduct();
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("ok");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(result.getContents());
                System.out.println(result.getContents());
                Barcode.setText(result.getContents());
                builder.setTitle("Rezultat skanowania");

                builder.setPositiveButton("Skanuj ponownie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScanBarcode scanBarcode = new ScanBarcode();
                        scanBarcode.scanBarcode(getActivity(),AddProductAdmin.this);
                    }
                }).setNegativeButton("Koniec", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
                Toast.makeText(getContext(), "Brak produktu", Toast.LENGTH_LONG).show();
        } else
        super.onActivityResult(requestCode, resultCode, data);
    }
}

