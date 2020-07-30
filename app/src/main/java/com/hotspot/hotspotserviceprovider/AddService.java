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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceProviderModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class AddService extends AppCompatActivity implements View.OnClickListener{
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    private String[] cameraPermission;
    private String[] storagePermission;
    ServiceProviderModel model;

    TextInputEditText providerName,serviceCategory,providerContact,providerEmail,providerPin;
    ImageView imageView;
    Uri imageUri;
    ImageButton addImage;
    Button submit;
    String TAG="AddService";
    String uid,phone;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
try {
    cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    phone=pref.getString("Phone","");
    uid=pref.getString("uid","");

    progressBar=findViewById(R.id.progressBar);
    addImage = findViewById(R.id.addImage);
    imageView = findViewById(R.id.imageView);
    providerName = findViewById(R.id.Serviceman);
    serviceCategory = findViewById(R.id.serviceCategory);
    providerContact = findViewById(R.id.providerContact);
    providerEmail = findViewById(R.id.providerEmail);
    providerPin = findViewById(R.id.servicePin);
    submit = findViewById(R.id.submit);

    addImage.setOnClickListener(this);
    submit.setOnClickListener(this);

}catch (Exception e){
    e.printStackTrace();
}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addImage:{
                showImagedImportDialog();
                break;
            }
            case R.id.submit:{
                if(imageUri==null){
                    Toast.makeText(AddService.this,"Select an Image",Toast.LENGTH_SHORT).show();

                }else if(providerName.getText().toString().trim().equals("")){
                    providerName.requestFocus();
                    providerName.setError("Service Provider Name is necessary");
                }else if(providerPin.getText().toString().trim().equals("")){
                    providerPin.requestFocus();
                    providerPin.setError("Service Area Pin is necessary");
                }else if(providerContact.getText().toString().trim().equals("")){
                    providerContact.requestFocus();
                    providerContact.setError("Service Provider Contact is necessary");
                }else if(providerEmail.getText().toString().trim().equals("")){
                    providerEmail.requestFocus();
                    providerEmail.setError("Service Provider Email is necessary");
                }else if(serviceCategory.getText().toString().trim().equals("")){
                    serviceCategory.requestFocus();
                    serviceCategory.setError("Service Category is necessary");
                }else
                {
                    progressBar.setVisibility(View.VISIBLE);
                saveOnDB();
                }
                break;
            }
        }
    }

    private void saveOnDB() {
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Services");
        final String pushkey=ref.push().getKey();
        final String[] path = new String[1];
        model=new ServiceProviderModel();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Partner").child("Services");
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
                            imageObject.put("providerImage", path[0]);
                            ref.child(pushkey).updateChildren(imageObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent=new Intent(AddService.this,ManageShop.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AddService.this,"Service Added.Wait for Verification",Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });
                }
            });
        }
        model.setProviderCategory(serviceCategory.getText().toString().trim());
        model.setProviderMail(providerEmail.getText().toString().trim());
        model.setProviderName(providerName.getText().toString().trim());
        model.setPushkey(pushkey);
        model.setProviderNumber(providerContact.getText().toString().trim());
        model.setProviderPincode(providerPin.getText().toString().trim());

        ref.child(pushkey).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                Intent intent=new Intent(AddService.this,ManageShop.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }
        });




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
                    imageView.setImageURI(imageUri);
                    Picasso.get().load(imageUri).into(imageView);
                    Log.w(TAG, "ResultUri=>" + imageUri);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(AddService.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}