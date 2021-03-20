package com.smieciolapp.data.model;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.smieciolapp.ViewModel.ScanProductsPage;

public class ScanBarcode extends CaptureActivity {

    public ScanBarcode() {
    }

    public void scanBarcode(Activity activity, Fragment fragment){
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setCaptureActivity(ScanBarcode.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Skanuję produkt");
        integrator.forSupportFragment(fragment).initiateScan();
    }

}
