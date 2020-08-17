package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotspot.hotspotserviceprovider.modelClasses.ShopDetailModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AddShop extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    private String[] cameraPermission;
    private String[] storagePermission;

    ProgressBar progressBar;
    ShopDetailModel model;
    Uri imageUri,fssaiUri;
    ArrayAdapter<String> cityAdapter;
    Spinner citySpinner;
    String TAG="ManageShop";
    String cityName, stateName;
    ImageView imageView;
    ImageButton addImage;
    TextInputEditText shopName,shopOwnerName,ownerContact,ownerEmail,shopAddress,address2,PinCode;
    Spinner spinnerCategory,city;

    Button submit;
    DatabaseReference reference;
    String phone,category;
LinearLayout fssaiLayout;
ImageView fssaiImage;
String clicked;
    private String[] categoryList = new String[]{
            "Select Category", "Eats", "Medics","Stay","Games","Shop Stop","Liquor Land","Call for Any","Vocal to Local","Fashion and Accessories"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        try {
            cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

                SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                phone=pref.getString("Phone","");
                if(phone.equals("")){
                    phone="UnKnown";
                }
            model=new ShopDetailModel();
            fssaiLayout=findViewById(R.id.fssaiLayout);
            fssaiImage=findViewById(R.id.fssaiImage);
            progressBar=findViewById(R.id.progressBar);
            imageView = findViewById(R.id.imageView);
            addImage = findViewById(R.id.addImage);
            shopAddress = findViewById(R.id.address1);
            shopName = findViewById(R.id.shopName);
            spinnerCategory = findViewById(R.id.shopCategory);
            shopOwnerName = findViewById(R.id.shopOwnerName);
            ownerContact = findViewById(R.id.ownerContact);
            ownerEmail = findViewById(R.id.ownerEmail);
            address2 = findViewById(R.id.address2);
            submit = findViewById(R.id.submit);
            PinCode=findViewById(R.id.shoppincode);
            addImage.setOnClickListener(this);

            submit.setOnClickListener(this);

            ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item,categoryList );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
            spinnerCategory.setOnItemSelectedListener(this);

            fssaiImage.setOnClickListener(this);
            if(category.equals("Eats")){
                fssaiLayout.setVisibility(View.VISIBLE);
            }else
                fssaiLayout.setVisibility(View.GONE);


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.w(TAG, "ResultCode=>" + resultCode);
        Log.w(TAG, "RequestCode=>" + requestCode);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == OPTION_GALLERY_CODE) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    Log.w(TAG, "GalleryImage");

                }
                if (requestCode == OPTION_CAMERA_CODE) {
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    Log.w(TAG, "CameraImage");
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    if(clicked.equals("fssai")){
                        fssaiUri=result.getUri();
                        Picasso.get().load(fssaiUri).into(fssaiImage);

                    }else if(clicked.equals("shop")){

                        imageUri = result.getUri();
                        imageView.setImageURI(imageUri);
                        Picasso.get().load(imageUri).into(imageView);
                        Log.w(TAG, "ResultUri=>" + imageUri);

                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(AddShop.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:{
                if(imageUri==null){
                    Toast.makeText(AddShop.this,"Please select an Image of your shop",Toast.LENGTH_SHORT).show();

                }else if(shopName.getText().toString().trim().equals("")){
                    shopName.setError("Shop name is mandatory");
                    shopName.requestFocus();
                }else if (category.equals("Select Category")){

                    Toast.makeText(getApplicationContext(),"Select Shop Category",Toast.LENGTH_LONG).show();

                }else if(category.equals("Eats") && fssaiUri==null) {
                    Toast.makeText(getApplicationContext(), "FSSAI license is required for addding Food shops", Toast.LENGTH_LONG).show();
                }
                else if(shopOwnerName.getText().toString().equals("")){
                    shopOwnerName.requestFocus();
                    shopOwnerName.setError("Enter Owner Name");
                }else if(ownerContact.getText().toString().equals("")){
                    ownerContact.requestFocus();
                    ownerContact.setError("Enter Owner Number");
                }else if(ownerEmail.getText().toString().equals("")){
                    ownerEmail.requestFocus();
                    ownerEmail.setError("Enter Owner Email");
                }else if(shopAddress.getText().toString().equals("")){
                    shopAddress.requestFocus();
                    shopAddress.setError("Enter Owner Address");
                }  else if(PinCode.getText().toString().equals("")){
                    PinCode.setError("PinCode is necessary");
                    PinCode.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    saveOnDb();
                }
                break;
            }
            case R.id.fssaiImage:{
                showImagedImportDialog();
                clicked="fssai";
                break;
            }
            case R.id.addImage:{
                showImagedImportDialog();
                clicked="shop";
                break;
            }

        }
    }

    private void showImagedImportDialog() {
        String[] items = {"Camera", "Gallery"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Select App");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else
                        openCamera();
                }
                if (i == 1) {
                    if (i == 0) {
                        if (!checkStoragePermission()) {
                            requestStoragePermission();
                        }
                    } else
                        openGallery();
                }
            }
        });
        dialog.create().show();
    }


    //permissionCheck
    private boolean checkStoragePermission() {
        boolean resultStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return resultStorage;
    }
    private void saveOnDb() {
        try {
            final DatabaseReference shopReferrance=FirebaseDatabase.getInstance().getReference().child("Shop").child(phone);
            reference = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("ShopDetails");
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Partner").child("PartnerShop");
            final String pushkey=reference.push().getKey();
            final String[] path = new String[1];

            model.setShopName(shopName.getText().toString().trim());
            model.setOwnerContact(ownerContact.getText().toString().trim());
            model.setOwnerMail(ownerEmail.getText().toString().trim());
            model.setOwnerName(shopOwnerName.getText().toString().trim());
            model.setShopAddress(shopAddress.getText().toString().trim() + address2.getText().toString().trim());
            model.setShopCategory(category);
            model.setPushkey(pushkey);
            model.setCity(cityName);
            model.setState(stateName);
            model.setVerificationStatus(true);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if(imageUri!=null){
                        storageReference.child(phone).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        path[0] = uri.toString();
                                        Map<String, Object> imageObject = new HashMap<>();
                                        imageObject.put("ShopImage", path[0]);
                                        shopReferrance.child(model.getShopName()).updateChildren(imageObject);
                                        reference.child(pushkey).updateChildren(imageObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent intent=new Intent(AddShop.this,ManageShop.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(AddShop.this, "Data Saved Successfully ", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                });
                            }
                        });
                    }
                }
            });

            shopReferrance.child(model.getShopName()).setValue(model);
            reference.child(pushkey).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    shopReferrance.child(model.getShopName()).setValue(model);
                    Log.w(TAG, "Data Saved");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    //permissionRequest
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }


    //GalleryOpen
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, OPTION_GALLERY_CODE);
    }

    private boolean checkCameraPermission() {

        boolean resultCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean resultStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultCamera && resultStorage;
    }
    //CameraOpen
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Result Image");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, OPTION_CAMERA_CODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category=adapterView.getItemAtPosition(i).toString();
        if(category.equals("Eats")){
            fssaiLayout.setVisibility(View.VISIBLE);
        }else
            fssaiLayout.setVisibility(View.GONE);



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}