package com.smieciolapp.ViewModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.smieciolapp.R;
import com.smieciolapp.data.model.Product;
import com.smieciolapp.data.model.ScanBarcode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

public class ScanProductsPage extends Fragment {

    ImageView imageView;
    Button takePic;
    Button scanProduct;
    TextView textFromPic;
    TextView sumOfPlasticWeight;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    SearchView searchView;
    ListView prodList;
    ListView shoppingList;
    double sumOfWeight=0;
    boolean scanReceipt=false;
    boolean scanBarcode=false;
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};

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
        View view = inflater.inflate(R.layout.fragment_scan, container, false);


        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        imageView = (ImageView) view.findViewById(R.id.pictureTaken);
        takePic = (Button) view.findViewById(R.id.takePhoto);
        textFromPic = (TextView) view.findViewById(R.id.recognizedText);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        prodList = (ListView) view.findViewById(R.id.prodList);
        shoppingList = (ListView) view.findViewById(R.id.shoppingList);
        sumOfPlasticWeight = (TextView) view.findViewById(R.id.sumOfPlasticWeight);
        scanProduct = (Button) view.findViewById(R.id.scanProduct);

        //lista
        products = new ArrayList<Product>();
        shoppingProducts = new ArrayList<Product>();

        //adapter
        prodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, products);
        shoppingAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, shoppingProducts);
        prodList.setAdapter(prodAdapter);
        shoppingList.setAdapter(shoppingAdapter);

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


        // skan kodu kreskowego
        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            scanBarcode=true;
            scanBarcode();
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
                prodAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // dodawanie produktów
        prodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(prodAdapter.getItem(position));
                shoppingProducts.add(prodAdapter.getItem(position));
                sumOfWeight+=prodAdapter.getItem(position).getWeight();
                sumOfPlasticWeight.setText(String.valueOf(sumOfWeight));
                sumOfPlasticWeight.append(" kg");
                // odświeżanie dynamiczne
                shoppingAdapter.notifyDataSetChanged();
                shoppingList.invalidateViews();

            }
        });

        // obsluga zdjec
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanReceipt=true;
                dispatchTakePictureIntent();
            }
        });

        return view;
    }


    //////////////////////////////////////////////////////////////////////////

    // Obsluga kamery

    //////////////////////////////////////////////////////////////////////////

    //uruchomienie kamery, uruchomienie intentu
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }



    //stworzenie unikalnego pliku obrazu
    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private  Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    //dodanie do galerii
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Objects.requireNonNull(getActivity()).sendBroadcast(mediaScanIntent);
    }


    // ustawienie zdjecia do imageview na podstawie sciezki
    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);

        return bitmap;
    }
    private void scanBarcode(){
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(ScanBarcode.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Skanuję produkt");
        integrator.forSupportFragment(ScanProductsPage.this).initiateScan();
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
                System.out.println(result.getContents());
                textFromPic.setText(result.getContents());
                builder.setTitle("Rezultat skanowania");
                scanBarcode=false;
                builder.setPositiveButton("Skanuj ponownie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanBarcode();
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
            super.onActivityResult(requestCode, resultCode,data);




        try{
                    if(scanReceipt){
                        galleryAddPic();

                        Bitmap bitmap = setPic();
                        Bitmap okBitmap = rotateImage(bitmap);

                        imageView.setImageBitmap(okBitmap);

                        TextRecognizer recognizer = TextRecognition.getClient();
                        InputImage image = InputImage.fromBitmap(okBitmap,0);


                        Task<Text> result1 =
                                recognizer.process(image)
                                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                                            @Override
                                            public void onSuccess(Text visionText) {
                                                textFromPic.setText(visionText.getText());
                                                System.out.println(visionText.getText());
                                                scanReceipt=false;
                                            }
                                        })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Task failed with an exception
                                                        // ...
                                                    }
                                                });

                    }


                } catch (Exception e){
                    // do nothing :)
                }

    }



}
