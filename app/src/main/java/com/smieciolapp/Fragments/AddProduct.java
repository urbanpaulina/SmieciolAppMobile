package com.smieciolapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smieciolapp.R;
import com.smieciolapp.data.model.Product;
import com.smieciolapp.data.model.ScanBarcode;
import com.smieciolapp.data.model.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// TO DO waga nie przyjmuje wartosci po przecinku

public class AddProduct extends Fragment {
    EditText NameProduct, WeightProduct;
    Button addProduct;
    FirebaseAuth fAuth;
    EditText barcodeTextView;
    FirebaseFirestore db;
    DatabaseReference Product_Ref;
    CollectionReference collectionReference;
    Button scanProd;
    ListView dropDownCategory;
    EditText etCategory;
    String dropDownSelect;
    //adapter dla produktów
    ArrayAdapter<String> adapter;
    boolean exist=false;

    public AddProduct() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        NameProduct = view.findViewById(R.id.etName);
        WeightProduct = view.findViewById(R.id.etWeight);
        barcodeTextView = view.findViewById(R.id.barcodeExist);
        dropDownCategory = view.findViewById(R.id.spCategory);
        addProduct = view.findViewById(R.id.addButton);
        scanProd = (Button) view.findViewById(R.id.scanProd);
        etCategory = view.findViewById(R.id.etCategory);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Product_Ref = FirebaseDatabase.getInstance().getReference().child("Products");
        collectionReference = db.collection("category");


        //adapter drop down lista nazw
        List<String> spinnerArray = new ArrayList<String>();

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, spinnerArray);

        dropDownCategory.setAdapter(adapter);

        //pobranie z bazy kategorii
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot snapshot:queryDocumentSnapshots){
                    Category category = snapshot.toObject(Category.class);
                    spinnerArray.add(category.getName());
                    adapter.notifyDataSetChanged();
                    dropDownCategory.invalidateViews();
                }

            }
        }).addOnFailureListener(e -> Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Pobieranie z bazy nie powiodło się", Toast.LENGTH_SHORT).show());


        dropDownCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                if (item != null) {
                    Toast.makeText(getActivity().getApplicationContext(),"Wybrałeś " + item.toString(),
                            Toast.LENGTH_SHORT).show();
                    etCategory.setText(item.toString());
                }

            }
        });

        dropDownCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                if (item != null) {
                    Toast.makeText(getActivity().getApplicationContext(), item.toString(),
                            Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getActivity().getApplicationContext(), "Selected",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // on click skanuj produkt
        scanProd.setOnClickListener(view1 -> {
            ScanBarcode scanBarcode = new ScanBarcode();
            scanBarcode.scanBarcode(getActivity(),AddProduct.this);
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addproduct();
            }
        });

        return view;

    }

    private boolean hasValidationErrors(String name, String weight, boolean exist, String barcode) {
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



        return false;
    }


    public void addproduct() {

        String name = NameProduct.getText().toString();
        String weight = WeightProduct.getText().toString();
        String barcode = barcodeTextView.getText().toString();
        String category = etCategory.getText().toString();


        if (!hasValidationErrors(name, weight, exist, barcode)) {
            name = NameProduct.getText().toString();
            weight = WeightProduct.getText().toString();
            barcode = barcodeTextView.getText().toString();


            if(!exist) {
                Product products = new Product(barcode, name, Double.parseDouble(weight), false, barcode, category);
                Product_Ref.push().setValue(products);
                Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Produkt został dodany i czeka na akceptacje", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Produkt już istnieje", Toast.LENGTH_SHORT).show();

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        System.out.println("ok");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Rezultat skanowania");
                builder.setMessage(result.getContents());
                ScanBarcode scanBarcode = new ScanBarcode();
                scanBarcode.checkIfProductExists(result.getContents(), exista -> {
                    System.out.println("Exist " + exista);
                    if(exista){
                        System.out.println("exist?" + exista);
                        builder.setMessage("Podany produkt istnieje już w naszej bazie");
                        barcodeTextView.setText("Produkt istnieje");
                        barcodeTextView.setTextColor(Color.parseColor("#FF0000"));
                        addProduct.setBackgroundColor(Color.parseColor("#FF0000"));
                        addProduct.setEnabled(false);

                        Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Podany produkt istnieje już w bazie", Toast.LENGTH_SHORT).show();
                        exist = true;
                    } else if(!exista){
                        System.out.println("elseexist?" + exista);
                        addProduct.setEnabled(true);
                        addProduct.setBackgroundColor(Color.parseColor("#4BB6AF"));
                        barcodeTextView.setText(result.getContents());
                        builder.setMessage("Dodałeś produkt do bazy. Dzięki !");
                    }
                });

                builder.setPositiveButton("Skanuj ponownie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScanBarcode scanBarcode = new ScanBarcode();
                        scanBarcode.scanBarcode(getActivity(),AddProduct.this);
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

