package com.hotspot.hotspotserviceprovider;

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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class UploadAAdhar extends AppCompatActivity implements View.OnClickListener {
    ImageView aadharFrontIV, adharbackView;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    String clicked;
    Uri imageUri;
    private String[] cameraPermission;
    private String[] storagePermission;
    String TAG = "AADharUpload";
    Bitmap Frontbitmap, Backbitmap;
    Button upload;
    DatabaseReference adharRef;
    StorageReference adharStorage;
    String uid;
    FirebaseAuth mAuth;
    Uri frontUri,backUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_a_adhar);
try {
    SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    uid = userPref.getString("uid", "");
    if (uid.isEmpty()) {

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

    }
    aadharFrontIV = findViewById(R.id.aadharFrontIV);
    adharbackView = findViewById(R.id.adharbackView);
    upload = findViewById(R.id.upload);
    upload.setOnClickListener(this);
    aadharFrontIV.setOnClickListener(this);
    adharbackView.setOnClickListener(this);

    cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

}catch (Exception e){
    e.printStackTrace();
}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aadharFrontIV: {
                clicked = "Front";
                showImageImportDialog();


                break;
            }
            case R.id.adharbackView: {
                clicked = "Back";
                showImageImportDialog();
                break;
            }
            case R.id.upload:{
                storeOnDB();
                break;
            }
        }
    }

    private void storeOnDB() {
    try {
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(uid).child("Documents").child("AadharDetails");

        adharRef = FirebaseDatabase.getInstance().getReference();
        adharStorage = FirebaseStorage.getInstance().getReference().child(uid)
                .child("Aadhar");
        StorageReference adharFront = adharStorage.child("Front");
        StorageReference adharBack = adharStorage.child("Back");


        final String[] path = new String[1];

        adharBack.putFile(backUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadAAdhar.this, "BackUploadSuccessful", Toast.LENGTH_SHORT).show();

                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        path[0] = uri.toString();
                        Map<String, Object> imageObject = new HashMap<>();
                        imageObject.put("backImage", path[0]);
                        reff.updateChildren(imageObject);

                    }
                });

            }
        });
        adharFront.putFile(frontUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadAAdhar.this, "FrontUploadSuccessful", Toast.LENGTH_SHORT).show();
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        path[0] = uri.toString();
                        Map<String, Object> imageObject = new HashMap<>();
                        imageObject.put("frontImage", path[0]);
                        reff.updateChildren(imageObject);


                    }
                });

            }
        });
    }catch(Exception e){
        e.printStackTrace();
    }


    }


    private void showImageImportDialog() {
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

    private boolean checkCameraPermission() {

        boolean resultCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean resultStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultCamera && resultStorage;
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
                    imageUri = result.getUri();
                    if (clicked == "Front") {
                        frontUri=imageUri;
                        aadharFrontIV.setImageURI(imageUri);
                        Picasso.get().load(imageUri).into(aadharFrontIV);
                        Log.w(TAG, "ResultUri=>" + imageUri);
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) aadharFrontIV.getDrawable();
                         Frontbitmap = bitmapDrawable.getBitmap();

                    }
                    if (clicked == "Back") {
                        backUri=imageUri;
                        adharbackView.setImageURI(imageUri);
                        Picasso.get().load(imageUri).into(adharbackView);
                        Log.w(TAG, "ResultUri=>" + imageUri);
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) aadharFrontIV.getDrawable();
                         Backbitmap = bitmapDrawable.getBitmap();
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(UploadAAdhar.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
