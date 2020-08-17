package com.hotspot.hotspotserviceprovider;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserDetailsEdit extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];
    EditText name, phn, add1, add2, mail, refferalCode;
    Button save;
    String uid, phoneNumber, type;
    String key, TAG = "UserDetails";
    ServiceUserModel model;
    ImageView profileImg, addImg;
    TextView drop_type, referrerName;
    FirebaseDatabase db;
    DatabaseReference reff;
    private Spinner serviceSpinner, partnerSpinner;
    FirebaseAuth mAuth;
    Uri imageUri;
    StorageReference ImageRef;
    ProgressBar progress;
    int cityChoice, stateChoice = 0;
    ArrayAdapter<String> cityAdapter;
    String cityName, stateName;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    private String[] stateSpinner = new String[]{
            "Select State", "Delhi NCR", "Uttar Pradesh", "Rajasthan","Maharashtra"};
    private String[] delhiSpinner = new String[]{
            "Select City", "Delhi", "Greater Noida", "Gurgaon"};
    private String[] upSpinner = new String[]{
            "Select City", "Kanpur","Agra", "Noida", "Lucknow","Bareilly"};
    private String[] rajasthanSpinner = new String[]{
            "Select City", "Jaipur"};
    private String[] maharashtraSpinner=new String[]{
      "Select City","Pune","Mumbai"
    };
    LinearLayout referralLayout;
    Spinner state, city;
    String reffererKey;
    String singinReward="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_edit);
        try {
            Intent i = getIntent();
            pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            edit = pref.edit();
            mAuth = FirebaseAuth.getInstance();
            if(i.getStringExtra("reffererKey")!=null)
                reffererKey=i.getStringExtra("reffererKey");

            uid = pref.getString("uid", "");

            phoneNumber = pref.getString("Phone", "");

            Log.w(TAG, "UID RECIEVED-=>" + uid);

            referralLayout=findViewById(R.id.referralLayout);
            referrerName=findViewById(R.id.referrerName);
            progress=findViewById(R.id.progressBar);
            name = findViewById(R.id.UserFullName);
            add1 = findViewById(R.id.address1);
            add2 = findViewById(R.id.address2);
            mail = findViewById(R.id.mail);
            phn = findViewById(R.id.phn);
            save = findViewById(R.id.saveBtn);
            profileImg = findViewById(R.id.profile_image);
            addImg = findViewById(R.id.addImage);
            refferalCode = findViewById(R.id.referralCode);
            state = findViewById(R.id.state);
            city = findViewById(R.id.city);
            if (i.getStringExtra("From")==null){
                referralLayout.setVisibility(View.GONE);
            }
            phn.setText(phoneNumber);

            if(reffererKey!=null){
                refferalCode.setText(reffererKey);
            }
            refferalCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    FirebaseDatabase.getInstance().getReference().child("Partner").orderByChild("referralCode").equalTo(charSequence.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                referrerName.setVisibility(View.VISIBLE);
                                refferalCode.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);
                                String[] splitString = dataSnapshot.getValue().toString().split("name=");
                                String newString = splitString[1].trim();
                                String[] name=newString.split(", Profileimage");
                                Log.w(TAG, "PHONENUBER=>" + name[0]);
                                referrerName.setText("Referrer is "+ name[0].toUpperCase());
                                Log.w(TAG,"NAME=>"+name[0]);

                                singinReward="25";
                            }else {
                                singinReward="0";
                                referrerName.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.w(TAG,"ONTExtChanged=>"+charSequence);

                FirebaseDatabase.getInstance().getReference().child("Partner").orderByChild("referralCode").equalTo(charSequence.toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            referrerName.setVisibility(View.VISIBLE);
                            refferalCode.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);
                            String[] splitString = dataSnapshot.getValue().toString().split("name=");
                            String newString = splitString[1].trim();
                            String[] name=newString.split(", Profileimage");
                            Log.w(TAG, "PHONENUBER=>" + name[0]);
                            referrerName.setText("Referrer is "+ name[0].toUpperCase());
                            Log.w(TAG,"NAME=>"+name[0]);

                            singinReward="25";
                        }else {
                            singinReward="0";
                            referrerName.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    FirebaseDatabase.getInstance().getReference().child("Partner").orderByChild("referralCode").equalTo(editable.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                referrerName.setVisibility(View.VISIBLE);
                                refferalCode.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);
                                String[] splitString = dataSnapshot.getValue().toString().split("name=");
                                String newString = splitString[1].trim();
                                String[] name=newString.split(", Profileimage");
                                Log.w(TAG, "PHONENUBER=>" + name[0]);
                                referrerName.setText("Referrer is "+ name[0].toUpperCase());
                                Log.w(TAG,"NAME=>"+name[0]);

                                singinReward="25";
                            }else {
                                singinReward="0";
                                referrerName.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            model = new ServiceUserModel();

            ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, stateSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            state.setAdapter(adapter);
            state.setOnItemSelectedListener(this);

            cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            String referralCode=generateReferralCode();
            Log.w(TAG,"ReferralCode"+referralCode);

            save.setOnClickListener(this);
            addImg.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addImage: {
                Log.w(TAG, "AddimagePressed");
                showImageImportDialog();

                break;
            }
            case R.id.saveBtn: {

                if (TextUtils.isEmpty(name.getText().toString().trim())) {
                    name.setError("Name is Required");
                    name.requestFocus();
                } else if (TextUtils.isEmpty(add1.getText().toString().trim())) {
                    add1.setError("House number/Apartment Number is Required");
                    add1.requestFocus();
                } else if (TextUtils.isEmpty(add2.getText().toString().trim())) {
                    add2.setError("Area/Block is Required");
                    add2.requestFocus();
                } else if (TextUtils.isEmpty(phn.getText().toString().trim())) {
                    phn.setError("Phone Number is required");
                } else if (TextUtils.isEmpty(mail.getText().toString().trim())) {
                    mail.setError("Mail is mandatory");
                    mail.requestFocus();
                } else if (stateName.equals("Select State")) {
                    Toast.makeText(UserDetailsEdit.this, "Please Select State", Toast.LENGTH_SHORT).show();
                } else if (cityName.equals("Select City")) {
                    Toast.makeText(UserDetailsEdit.this, "Please Select City", Toast.LENGTH_SHORT).show();
                }
                else if(imageUri==null) {

                    Toast.makeText(UserDetailsEdit.this,"Profile Pic Select", Toast.LENGTH_SHORT).show();
                }
                else
                    progress.setVisibility(View.VISIBLE);
                storeDataOnDb();
                }
                break;
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

    public String generateReferralCode(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String SaltNUM="1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            int index2=(int)(rnd.nextFloat()*SaltNUM.length());
            int index3=(int)(rnd.nextFloat()*SALTCHARS.length());
            int index4=(int)(rnd.nextFloat()*SaltNUM.length());

            salt.append(SALTCHARS.charAt(index));
            salt.append(SaltNUM.charAt(index2));
            salt.append(SALTCHARS.charAt(index3));
            salt.append(SaltNUM.charAt(index4));
        }
        String saltStr = salt.toString().toLowerCase();
        return saltStr;

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
        try {
            super.onActivityResult(requestCode, resultCode, data);


            Log.w(TAG, "ResultCode=>" + resultCode);
            Log.w(TAG, "RequestCode=>" + requestCode);

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
                    Bitmap bitmap=result.getBitmap();

                    //Bitmap bitmap = Bitmap.createBitmap(result.getBitmap());
                    //Log.w(TAG,"Bitmap=>"+bitmap);
                    profileImg.setImageURI(imageUri);
                    Picasso.get().load(imageUri).into(profileImg);
                    Log.w(TAG, "ResultBitmap=>" + bitmap);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(UserDetailsEdit.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    private void UpdateProfile(){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(model.getName())
//                .setPhotoUri(Uri.parse(model.getProfileimage()))
//                .build();
//
//
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User profile updated.");
//                        }
//                    }
//                });
//    }

    private void storeDataOnDb() {
        try {
            reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(phoneNumber);
            String pushkey = reff.push().getKey();
            String referral=generateReferralCode();
            ImageRef = FirebaseStorage.getInstance().getReference().child(uid)
                    .child("UserDp");
            final String[] path = new String[1];
            if (imageUri != null) {
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


                                reff.updateChildren(imageObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(UserDetailsEdit.this, AllServices.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });

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
            }else Log.w(TAG,"IMageUri Empty");
            String Name = name.getText().toString();

            edit.putString("userName", Name);
            edit.putString("add1", add1.getText().toString().trim());
            edit.putString("add2", add2.getText().toString().trim());
            edit.putString("mail", mail.getText().toString().trim());
            edit.apply();
            Log.w(TAG, "NAMe==>" + Name);
            model.setName(Name);
            model.setMail(mail.getText().toString());
            model.setPhn(phn.getText().toString());
            model.setAdd2(add2.getText().toString());
            model.setAdd1(add1.getText().toString());
            model.setState(stateName);
            model.setCity(cityName);
            if(singinReward.equals("0")){
                model.setWalletBalance("0");
                Log.w(TAG,"WalletBalanceEDit=>0");
            }
            else
                model.setWalletBalance("25");
            model.setReferralCode(referral);
            model.setReferredBy(refferalCode.getText().toString().trim());
            model.setPushkey(pushkey);
            model.setUid(uid);



            reff.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void Void) {
                    Log.w(TAG,"DAta saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG,"DAta saving failed"+e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.state) {

            if (i == 1) {
                stateChoice = 1;
                cityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, delhiSpinner);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateName = "Delhi NCR";

                //delhi
            } else if (i == 2) {
                stateChoice = 2;
                cityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, upSpinner);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateName = "Uttar Pradesh";


                //up
            } else if (i == 3) {
                stateChoice = 3;
                cityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, rajasthanSpinner);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateName = "Rajasthan";


                //Rajasthan
            }else  if(i==4){
                cityAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, maharashtraSpinner);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateName = "Maharashtra";
            }


            city.setAdapter(cityAdapter);
            city.setOnItemSelectedListener(this);

            Log.w(TAG, "StateSelected" + stateChoice + "STateName=>" + stateName);
        } else if (adapterView.getId() == R.id.city) {
            cityName = adapterView.getItemAtPosition(i).toString();

            Log.w(TAG, "cityName=>" + cityName);
        }
    }

    public void rewardUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRecord =
                FirebaseDatabase.getInstance().getReference()
                        .child("Partner")
                        .child(phoneNumber);
        userRecord.child("referredBy").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        type = "CallForALl";
    }
}