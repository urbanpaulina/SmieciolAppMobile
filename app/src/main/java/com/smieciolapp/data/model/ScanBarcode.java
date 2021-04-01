package com.smieciolapp.data.model;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;

public class ScanBarcode extends CaptureActivity {

    private DatabaseReference db;
    private Product product;

    public ScanBarcode() {
        this.db = FirebaseDatabase.getInstance().getReference("Products");
    }

    public void scanBarcode(Activity activity, Fragment fragment) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setCaptureActivity(ScanBarcode.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Skanuję produkt");
        IntentIntegrator.forSupportFragment(fragment).initiateScan();
    }

    public void findBarcodeinDatabase(String requestedBarcode, MyCallback myCallback) {
        //pobranie produktow
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean founded = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Product prod = (Product) snapshot.getValue(Product.class);
                        if (prod.getBarcode().equals(requestedBarcode)) {
                            myCallback.onCallback(prod);
                            founded=true;
                            break;
                        }
                    } catch (Exception e) {
                        // System.out.println("Nie dodano " + snapshot.getValue(Product.class));
                    }
                }
                if(!founded){
                    try{
                        myCallback.onCallback(null);
                    }catch (Exception e){
                        //do nothing
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkIfProductExists(String barcode, CheckIfBarcodeExistsCallback checkCallback){
        //pobranie produktow
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean founded = false;
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    System.out.println(snapshot.getValue());
                    try{
                        Product prod = (Product)snapshot.getValue(Product.class);
                        if(prod.getBarcode().equals(barcode)) {
                            founded = true;
                            checkCallback.onCallback(prod.getBarcode().equals(barcode));
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Nie dodano " + snapshot.getValue(Product.class));
                    }
                }
                if(!founded){
                    try{
                        checkCallback.onCallback(false);
                    }catch (Exception e){
                        //do nothing
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

