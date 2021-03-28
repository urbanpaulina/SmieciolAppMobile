package com.smieciolapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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


// TO DO waga nie przyjmuje wartosci po przecinku

public class AddProduct extends Fragment  implements View.OnClickListener {
    EditText NameProduct, WeightProduct;
    Button addProduct;
    FirebaseAuth fAuth;
    TextView barcodeTextView;
    FirebaseFirestore db;
    DatabaseReference Product_Ref;
    CollectionReference collectionReference;
    Button scanProd;
    Spinner dropDownCategory;
    String dropDownSelect;
    boolean exist=false;

    public AddProduct() {
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

        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        NameProduct = view.findViewById(R.id.etName);
        WeightProduct = view.findViewById(R.id.etWeight);
        barcodeTextView = view.findViewById(R.id.barcodeExist);
        dropDownCategory = view.findViewById(R.id.spCategory);
        addProduct = view.findViewById(R.id.addButton);
        addProduct.setOnClickListener(this);
        scanProd = (Button) view.findViewById(R.id.scanProd);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Product_Ref = FirebaseDatabase.getInstance().getReference().child("Products");
        collectionReference = db.collection("category");



        List<Category> categories = new ArrayList<>();

        //pobranie z bazy kategorii
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                    categories.add(snapshot.toObject(Category.class));
            }
        }).addOnFailureListener(e -> Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Pobieranie z bazy nie powiodło się", Toast.LENGTH_SHORT).show());


        dropDownCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dropDownSelect = adapterView.getItemAtPosition(i).toString();
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


        //adapter do kategorii
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        dropDownCategory.setAdapter(adapter);

        return view;

    }

    private boolean hasValidationErrors(String name, String weight) {
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
        String barcode;
        String category = dropDownSelect;


        if (!hasValidationErrors(name, weight)) {
            name = NameProduct.getText().toString();
            weight = WeightProduct.getText().toString();
            barcode = barcodeTextView.getText().toString();

            if(!exist) {
                Product products = new Product(barcode, name, Double.parseDouble(weight), true, barcode, category);
                Product_Ref.push().setValue(products);
                Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Produkt został dodany i czeka na akceptacje", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Produkt już istnieje", Toast.LENGTH_SHORT).show();

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
                builder.setTitle("Rezultat skanowania");
                builder.setMessage("test");
                barcodeTextView.setText(result.getContents());
                ScanBarcode scanBarcode = new ScanBarcode();
                scanBarcode.checkIfProductExists(result.getContents(), exist -> {
                    if(exist){
                        builder.setMessage("Podany produkt istnieje już w naszej bazie");
                        Toast.makeText(AddProduct.this.getActivity().getApplicationContext(), "Podany produkt istnieje już w bazie", Toast.LENGTH_SHORT).show();
                        barcodeTextView.setText("Podany produkt istnieje w bazie");
                        exist = true;
                    } else {
                        barcodeTextView.setText("Dodałeś poprawnie kod kreskowy");
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

