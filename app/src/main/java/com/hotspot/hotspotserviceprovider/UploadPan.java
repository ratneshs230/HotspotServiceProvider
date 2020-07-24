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

public class UploadPan extends AppCompatActivity implements View.OnClickListener{
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    private String[] cameraPermission;
    private String[] storagePermission;
    Uri imageUri;
    ImageView PanIV;
    Button upload;
    String uid,phone;
    FirebaseAuth mAuth;
    String TAG="UPLOADPAN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pan);
try {
    SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    uid = userPref.getString("uid", "");
    if (uid.isEmpty()) {

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();


    }
    phone=userPref.getString("Phone","");

    cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    PanIV = findViewById(R.id.PanIV);
    upload = findViewById(R.id.upload);

    PanIV.setOnClickListener(this);
    upload.setOnClickListener(this);
}catch (Exception e){
    e.printStackTrace();
}
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.PanIV:{
            showImageImportDialog();

            break;
        }
        case R.id.upload:{
            storeOnDB();
            break;
        }
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

    private void storeOnDB() {

        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("Pan_CardDetails");

        DatabaseReference PanRef= FirebaseDatabase.getInstance().getReference();
        StorageReference PanStorage= FirebaseStorage.getInstance().getReference().child(uid)
                .child("PanCard");





        final String[] path = new String[1];


        PanStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadPan.this,"PanUploadSuccessful",Toast.LENGTH_SHORT).show();
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        path[0] = uri.toString();
                        Map<String, Object> imageObject = new HashMap<>();
                        imageObject.put("PanImage", path[0]);
                        reff.updateChildren(imageObject);



                    }
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(TAG,"ResultCode=>"+resultCode);
        Log.w(TAG,"RequestCode=>"+requestCode);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == OPTION_GALLERY_CODE) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    Log.w(TAG,"GalleryImage");

                }
                if (requestCode == OPTION_CAMERA_CODE) {
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    Log.w(TAG,"CameraImage");
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imageUri = result.getUri();
                    PanIV.setImageURI(imageUri);
                    Picasso.get().load(imageUri).into(PanIV);
                    Log.w(TAG, "ResultUri=>" + imageUri);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(UploadPan.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}