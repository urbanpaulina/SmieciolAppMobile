package com.smieciolapp.ViewModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.smieciolapp.R;
import com.smieciolapp.data.model.Product;
import com.smieciolapp.data.model.ScanBarcode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AddShoppingScan extends Fragment {

    ScanBarcode scanBarcode;
    ImageView scanProduct;
    TextView sumOfPlasticWeight;
    ListView shoppingList;
    SearchView searchView;
    ListView prodList;
    Button confirmShopping;
    double sumOfWeight=0;
    private final String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};

    //baza
    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Products");

    //listaProduktów
    ArrayList<Product> products;

    //list nazw produktów
    ArrayList<Product> shoppingProducts;

    //adapter dla produktów
    ArrayAdapter<Product> prodAdapter;
    ArrayAdapter<Product> shoppingAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_shoppings_scan, container, false);
        super.onCreate(savedInstanceState);

        int requestCode = 200;
        requestPermissions(permissions, requestCode);

        searchView = (SearchView) view.findViewById(R.id.searchView);
        prodList = (ListView) view.findViewById(R.id.prodList);
        sumOfPlasticWeight = (TextView) view.findViewById(R.id.plasticSum);
        scanProduct = (ImageView) view.findViewById(R.id.imageScan);
        shoppingList = (ListView) view.findViewById(R.id.shoppingList);
        confirmShopping = (Button) view.findViewById(R.id.confirmShopping);
        scanBarcode = new ScanBarcode();

        //lista
        products = new ArrayList<>();

        //zapisany stan listy zakupów - shoppingList
        shoppingProducts = loadPreviousShoppingList();
        sumOfPlasticWeight.setText(loadPreviousPlasticValue());
        try{
            sumOfWeight = Float.parseFloat(loadPreviousPlasticValue());
        } catch (Exception e) {
            sumOfWeight = 0.0;
        }

        //adapter
        prodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, products);
        shoppingAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, shoppingProducts);
        prodList.setAdapter(prodAdapter);
        shoppingList.setAdapter(shoppingAdapter);


        //opcjonalne poszerzenie layoutu
        setListViewHeightBasedOnChildren(shoppingList);


        // odświeżanie dynamiczne
        shoppingAdapter.notifyDataSetChanged();
        shoppingList.invalidateViews();

        //pobranie produktow
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    System.out.println(snapshot.getValue());
                    try{
                        Product prod = (Product)snapshot.getValue(Product.class);
                        products.add(prod);
                    } catch (Exception e) {
                        System.out.println("Nie dodano " + snapshot.getValue(Product.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //potwierdzenie zakupów
        confirmShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences("SHOPPING_LIST_SAVED", Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                Toast.makeText(getContext(), "Usunieto liste " , Toast.LENGTH_LONG).show();
            }
        });


        // skan kodu kreskowego
        scanProduct.setOnClickListener(v -> scanBarcode.scanBarcode(getActivity(),AddShoppingScan.this));


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecommendedOptions(prodList);
                setListViewHeightBasedOnChildren(prodList);
            }
        });

        // search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setListViewHeightBasedOnChildren(prodList);
                prodAdapter.getFilter().filter(newText);
                return false;
            }

        });

        // dodawanie produktów do listy zakupów
        prodList.setOnItemClickListener((parent, view1, position, id) -> {
            System.out.println(prodAdapter.getItem(position));
            Toast.makeText(getContext(), "Dodałeś: " + Objects.requireNonNull(prodAdapter.getItem(position)).getName(), Toast.LENGTH_LONG).show();
            //dodanie produktu do listy zakupów
            shoppingProducts.add(prodAdapter.getItem(position));

            //poszerzenie listView
            setListViewHeightBasedOnChildren(shoppingList);

            //ukrywanie rekomendacji
            hideRecommendedOptions(prodList);

            //zmiana stanu obecnej wagi
            sumOfWeight+= Objects.requireNonNull(prodAdapter.getItem(position)).getWeight();
            sumOfPlasticWeight.setText(String.valueOf(sumOfWeight));

            //zapisanie obecnego stanu produktów i wagi
            saveCurrentShoppingList();

            // odświeżanie dynamiczne
            shoppingAdapter.notifyDataSetChanged();
            shoppingList.invalidateViews();
        });

        return view;
    }


    private static void showRecommendedOptions(ListView listView){
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private static void hideRecommendedOptions(ListView listView){
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height=1;
        params.width=1;
        listView.setLayoutParams(params);
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void saveCurrentShoppingList(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("SHOPPING_LIST_SAVED", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonShoppingList = gson.toJson(shoppingProducts);
        editor.putString("SHOPPING_LIST", jsonShoppingList);
        editor.putString("PLASTIC_WEIGHT", sumOfPlasticWeight.getText().toString());
        editor.apply();
    }

    private ArrayList<Product> loadPreviousShoppingList(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("SHOPPING_LIST_SAVED", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SHOPPING_LIST", null);
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        shoppingProducts = gson.fromJson(json,type);

        // odświeżanie dynamiczne
        shoppingList.invalidateViews();

        if(shoppingProducts == null) {
            shoppingProducts = new ArrayList<>();

        }

        return shoppingProducts;
    }
    private String loadPreviousPlasticValue(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("SHOPPING_LIST_SAVED", MODE_PRIVATE);
        String plasticWeightJson;
        try{
            plasticWeightJson = sharedPreferences.getString("PLASTIC_WEIGHT",null);
        }catch (Exception e){
            return "";
        }
        try{
            sumOfWeight = Float.parseFloat(plasticWeightJson);
        } catch (Exception e){
           return "0.0";
        }

        return plasticWeightJson;
    }

    //pobranie obrazu przez intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        System.out.println("ok");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(result.getContents());
                    //dodanie zeskanowanego produktu do listy produktów
                    scanBarcode.findBarcodeinDatabase(result.getContents(), product -> {
                        shoppingProducts.add(product);

                        //poszerzenie listView
                        setListViewHeightBasedOnChildren(shoppingList);

                        //zmiana stanu obecnej wagi
                        sumOfWeight+=product.getWeight();
                        sumOfPlasticWeight.setText(String.valueOf(sumOfWeight));

                        //zapisanie obecnego stanu produktów
                        saveCurrentShoppingList();

                        //odświeżanie dynamiczne listy
                        shoppingAdapter.notifyDataSetChanged();
                        shoppingList.invalidateViews();
                    });
                builder.setTitle("Rezultat skanowania");
                builder.setPositiveButton("To nie to, skanuj ponownie", (dialog, which) ->
                        scanBarcode.scanBarcode(getActivity(),AddShoppingScan.this)).
                        setNegativeButton("Ok", (dialog, which) ->
                                Toast.makeText(getContext(), "Dodałeś produkt", Toast.LENGTH_LONG).show());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
                Toast.makeText(getContext(), "Brak produktu", Toast.LENGTH_LONG).show();
        } else
            super.onActivityResult(requestCode, resultCode,data);


    }





}


