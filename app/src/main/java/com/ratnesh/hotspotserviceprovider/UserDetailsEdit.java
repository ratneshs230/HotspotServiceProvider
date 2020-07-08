package com.ratnesh.hotspotserviceprovider;


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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsEdit extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemSelectedListener{


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];
    EditText name,phn, add1,add2,add3,mail,refferalCode;
    Button save;
    String uid,phoneNumber,type;
    String key,TAG="UserDetails";
    ServiceUserModel model;
    ImageView profileImg,addImg;
    TextView drop_type,referralLayout;
    FirebaseDatabase db;
    DatabaseReference reff;
    private Spinner spinner;
    FirebaseAuth mAuth;
    Uri imageUri;
    StorageReference ImageRef;
    ProgressBar progress;
    private String[] Services = new String[]{"Call For All","Hotspot Drive","Hotspot  Eats","Hotspot Stay","Hotspot  Shop-stop","Hotspot Medics",
            "Hotspot Games","Hotspot Money"};
    private String[] callforAll=new String[]{ "Carpenters","Carpet Cleaners","Driver Service","Duplicate Key Makers","Electricians","Masons",
            "Painters",
            "Pest Control",
            "Plumbers",
            "Towing Services",
            "Carpet Contractors",
            "Electric Chimney",
            "Fire Fighting Contractors",
            "Flooring Contractors",
            "Civil Contractors",
            "Gardening Tools Services",
            "House Keeping Cleaning",
            "Interior Designers Architecture",
            "Home Theatre",
            "Interior Decorators",
            "Internet Service Providers",
            "Lock Repair Services",
            "Architects",
            "Packaging Labelling",
            "Swimming Contractors",
            "Painting Contractors",
            "Roofing Contractors",
            "Security Equipment Services",
            "DTH",
            "Wall Paper Contractors",
            "Water Proofing Contractors",
            "Architects",
            "Carpenters",
            "Carpet Cleaners",
            "Carpet Contractors",
            "Civil Contractors",
            "Driver Service",
            "Duplicate Key Makers",
            "Electric Chimney",
            "Electricians",
            "Fire Fighting Contractors",
            "Flooring Contractors",
            "Gardening Tools Services",
            "Home Theatre",
            "House Keeping Cleaning",
            "Interior Decorators",
            "Interior Designers Architecture",
            "Internet Service Providers",
            "Lock Repair Services",
            "Masons",
            "Packaging Labelling",
            "Painters",
            "Painting Contractors",
            "Pest Control",
            "Plumbers",
            "Roofing Contractors",
            "Security Equipment Services",
            "Swimming Contractors",
            "Towing Services",
            "Wall Paper Contractors",
            "Water Proofing Contractors",
            "Window Tinting Services",
            "On Call Doctors",
            "On Call Gynaecologists",
            "On Call Paediatricians",
            "On Call Veterinary Doctors",
            "Online Doctors"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_edit);

        Intent i=getIntent();
        uid = i.getStringExtra("uid");
        phoneNumber=i.getStringExtra("no");
        mAuth=FirebaseAuth.getInstance();

        Log.w(TAG, "UID RECIEVED-=>" + uid);

        name=findViewById(R.id.UserFullName);
        add1=findViewById(R.id.address1);
        add2=findViewById(R.id.address2);
        add3=findViewById(R.id.address3);
        mail=findViewById(R.id.mail);
        phn=findViewById(R.id.phn);
        save=findViewById(R.id.saveBtn);
        spinner=findViewById(R.id.spinner1);
        profileImg=findViewById(R.id.profile_image);
        addImg=findViewById(R.id.addImage);
        drop_type=findViewById(R.id.type_drop);
        refferalCode=findViewById(R.id.referralCode);
        referralLayout=findViewById(R.id.referralLayout);

        referralLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refferalCode.getVisibility()==View.GONE){
                refferalCode.setVisibility(View.VISIBLE);

                }
                else
                    refferalCode.setVisibility(View.GONE);

            }
        });
        phn.setText(phoneNumber);

        model=new ServiceUserModel();

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, callforAll);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        save.setOnClickListener(this);
        addImg.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.saveBtn:{

                if (TextUtils.isEmpty(name.getText().toString().trim())) {
                    name.setError("Name is Required");
                    name.requestFocus();
                }
                else if (TextUtils.isEmpty(add1.getText().toString().trim())) {
                    add1.setError("House number/Apartment Number is Required");
                    add1.requestFocus();
                } else if (TextUtils.isEmpty(add2.getText().toString().trim())) {
                    add2.setError("Area/Block is Required");
                    add2.requestFocus();
                } else if (TextUtils.isEmpty(add3.getText().toString().trim())) {

                    Toast.makeText(UserDetailsEdit.this, "Please fill in the City", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(phn.getText().toString().trim())) {
                    phn.setError("Phone Number is required");
                }
                else if(type.isEmpty()){
                        drop_type.setTextColor(Color.RED);
                    Toast.makeText(UserDetailsEdit.this, "Select Type of service", Toast.LENGTH_SHORT).show();

                }
                    else{

                        storeDataOnDb();

                    }
                break;
                }
            case R.id.addImage:{

                showImageImportDialog();

                break;
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        openCamera();
                    } else
                        Toast.makeText(UserDetailsEdit.this, "Permission Denied", Toast.LENGTH_LONG).show();
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
                    profileImg.setImageURI(imageUri);
                    Picasso.get().load(imageUri).into(profileImg);
                    Log.w(TAG, "ResultUri=>" + imageUri);
                    model.setProfileimage(imageUri.toString());

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) profileImg.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(UserDetailsEdit.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void storeDataOnDb() {
        try {
            reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(uid);
            String pushkey = reff.push().getKey();

            ImageRef = FirebaseStorage.getInstance().getReference().child(uid)
                    .child("UserDp").child(uid);
            final String[] path = new String[1];

            ImageRef.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.w(TAG, "image Uploaded Successfully");
                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                    downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            path[0] = uri.toString();

                            Map<String, Object> imageObject = new HashMap<>();
                            imageObject.put("Profileimage", path[0]);


                            model.setProfileimage(path[0]);

                            Log.w(TAG, "URI=====>>>" + uri);
                            Log.w(TAG, "Path=====>>>" + path[0]);


                            reff.updateChildren(imageObject);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Image linking failed");
                            Toast.makeText(UserDetailsEdit.this, "Error Uploading File", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });
            String Name = name.getText().toString();
            Log.w(TAG, "NAMe==>" + Name);
            model.setName(Name);
            model.setMail(mail.getText().toString());
            model.setPhn(phn.getText().toString());
            model.setAdd2(add2.getText().toString());
            model.setAdd3(add3.getText().toString());
            model.setAdd1(add1.getText().toString());
            model.setWalletBalance("0");
            model.setReferralCode(refferalCode.getText().toString().trim());
            model.setPushkey(pushkey);
            model.setUid(uid);


            reff.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void Void) {
                    Intent intent = new Intent(UserDetailsEdit.this, PartnerProfile.class);
                    startActivity(intent);
                    Toast.makeText(UserDetailsEdit.this, "Upload Succesfull", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        type=adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(),"TYPE="+type,Toast.LENGTH_LONG).show();

        }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
            type="CallForALl";
    }
}