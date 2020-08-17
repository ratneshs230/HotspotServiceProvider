package com.hotspot.hotspotserviceprovider;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class DocumentsUpdate extends AppCompatActivity implements View.OnClickListener {
    TextView bankDetailsUpdate, PanCardUpdate, gstDetailUpdate, adharDetailsUpdate;
    View bank, pan, gst, adhar;
    EditText bankHolderName, bankBranchName, bankIfsc, bankAcNo;
    Button Submit;
    String uid;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    private String[] cameraPermission;
    private String[] storagePermission;
    Uri imageUri;
    ImageView PanBusiness, PanPersonal;
    Button uploadBusinessPan, uploadPersonalPan;
    String phone;
    FirebaseAuth mAuth;
    int ImageClicked, buttonClicked;
    RadioGroup radioPan, radioGst;

    EditText gstNo;
    Button uploadGST;
    String gstNumber;
    //    ImageView gstImg;
    DatabaseReference ref;
    Map<String, Object> gstObject = new HashMap<>();

    String TAG = "DocumentUpdate";
    Button uploadAadhar;
    DatabaseReference adharRef;
    StorageReference adharStorage;
    Uri frontUri, backUri;
    String clicked;
    ImageView aadharFrontIV, adharbackView;
    Boolean frontStatus=false,backStatus=false;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_update);

        try {
            SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            uid = userPref.getString("uid", "");
            phone = userPref.getString("Phone", "");

            checkstatus();
            cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

//            PanBusiness = findViewById(R.id.PanBusiness);
//            uploadBusinessPan = findViewById(R.id.uploadBusinessPan);
            progressBar=findViewById(R.id.progress);
            gstNo = findViewById(R.id.gstNo);
            uploadGST = findViewById(R.id.upload);
            uploadPersonalPan = findViewById(R.id.uploadPersonalPan);
            radioGst = findViewById(R.id.radioGroup);
            radioPan = findViewById(R.id.radioGroup1);
            PanPersonal = findViewById(R.id.PanPersonal);

            aadharFrontIV = findViewById(R.id.aadharFrontIV);
            adharbackView = findViewById(R.id.adharbackView);
            uploadAadhar = findViewById(R.id.uploadAadhar);
            uploadAadhar.setOnClickListener(this);

            radioGst.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.radio_button_1) {
                        gstNo.setVisibility(View.VISIBLE);
                        uploadGST.setVisibility(View.VISIBLE);
                    } else if (i == R.id.radio_button_2) {
                        gstNo.setVisibility(View.GONE);
                        uploadGST.setVisibility(View.GONE);
                    }
                }
            });

            uploadGST.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    gstNumber = gstNo.getText().toString().trim();
                    if (gstNumber.isEmpty()) {
                        gstNo.setError("GST number is Required");
                        gstNo.requestFocus();
                    } else
                        gstObject.put("GST_Number", gstNumber);

                    FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("GST_Details").updateChildren(gstObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            gstDetailUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
                            gst.setVisibility(View.GONE);
                        }
                    });

                }
            });

            PanPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageClicked = 1;
                    showImageImportDialog();
                }
            });

            radioPan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.radio_button_4) {
                        PanPersonal.setVisibility(View.GONE);

                    } else if (i == R.id.radio_button_3) {
                        PanPersonal.setVisibility(View.VISIBLE);
                    }
                }
            });


            PanPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageClicked = 1;
                    showImageImportDialog();
                }
            });

//            PanBusiness.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    showImageImportDialog();
//
//                }
//            });

            uploadPersonalPan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    storePanDB();

                }
            });
//            uploadBusinessPan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    buttonClicked=2;
//                    storePanDB();
//                }
//            });

            adharDetailsUpdate = findViewById(R.id.adharDetailsUpdate);
            gstDetailUpdate = findViewById(R.id.gstDetailUpdate);
            PanCardUpdate = findViewById(R.id.PanCardUpdate);
            bankDetailsUpdate = findViewById(R.id.bankDetailsUpdate);
            bank = findViewById(R.id.bankdetailLayout);
            pan = findViewById(R.id.pancardLayout);
            gst = findViewById(R.id.gstLayout);
            adhar = findViewById(R.id.adharLayout);

            bankAcNo = findViewById(R.id.bankAcNo);
            bankBranchName = findViewById(R.id.bankBranchName);
            bankHolderName = findViewById(R.id.bankHolderName);
            bankIfsc = findViewById(R.id.bankIfsc);

            findViewById(R.id.uploadBank).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bankAcNo.getText().toString().isEmpty()) {
                        bankAcNo.setError("Account Number Is Required");
                        bankAcNo.requestFocus();
                    } else if (bankBranchName.getText().toString().isEmpty()) {

                        bankBranchName.setError("Account Number Is Required");
                        bankBranchName.requestFocus();
                    } else if (bankHolderName.getText().toString().isEmpty()) {

                        bankHolderName.setError("Account Number Is Required");
                        bankHolderName.requestFocus();
                    } else if (bankIfsc.getText().toString().isEmpty()) {

                        bankIfsc.setError("Account Number Is Required");
                        bankIfsc.requestFocus();
                    } else {
                        storeBankDB();
                    }
                }
            });


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    if(frontStatus && backStatus){
                        adhar.setVisibility(View.GONE);
                        adharDetailsUpdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);
                    }
                }
            });
            adharDetailsUpdate.setOnClickListener(this);
            gstDetailUpdate.setOnClickListener(this);
            PanCardUpdate.setOnClickListener(this);
            bankDetailsUpdate.setOnClickListener(this);

            aadharFrontIV.setOnClickListener(this);
            adharbackView.setOnClickListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.adharDetailsUpdate: {
                if (adhar.getVisibility() == View.GONE)
                    adhar.setVisibility(View.VISIBLE);
                else
                    adhar.setVisibility(View.GONE);

                gst.setVisibility(View.GONE);
                pan.setVisibility(View.GONE);
                bank.setVisibility(View.GONE);
                break;
            }
            case R.id.gstDetailUpdate: {
                adhar.setVisibility(View.GONE);
                if (gst.getVisibility() == View.GONE)
                    gst.setVisibility(View.VISIBLE);
                else
                    gst.setVisibility(View.GONE);
                pan.setVisibility(View.GONE);
                bank.setVisibility(View.GONE);

                break;
            }
            case R.id.PanCardUpdate: {
                adhar.setVisibility(View.GONE);
                gst.setVisibility(View.GONE);
                if (pan.getVisibility() == View.GONE)
                    pan.setVisibility(View.VISIBLE);
                else
                    pan.setVisibility(View.GONE);
                bank.setVisibility(View.GONE);

                break;
            }
            case R.id.bankDetailsUpdate: {
                adhar.setVisibility(View.GONE);
                gst.setVisibility(View.GONE);
                pan.setVisibility(View.GONE);
                if (bank.getVisibility() == View.GONE)
                    bank.setVisibility(View.VISIBLE);
                else
                    bank.setVisibility(View.GONE);
                break;
            }
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
            case R.id.uploadAadhar: {
                Log.w(TAG, "Button Pressed");
                storeAadharDB();

                break;
            }
        }

    }
    private void checkstatus() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("AadharDetails").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("backImage").exists() && snapshot.child("frontImage").exists()) {
                                adharDetailsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
                                Picasso.get().load(snapshot.child("frontImage").getValue(String.class)).into(aadharFrontIV);
                                Picasso.get().load(snapshot.child("backImage").getValue(String.class)).into(adharbackView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("BankDetails").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                          bankDetailsUpdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("Pan_CardDetails").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            PanCardUpdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("GST_Details").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            gstDetailUpdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



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

                    if (ImageClicked == 1) {
                        imageUri = result.getUri();
                        PanPersonal.setImageURI(imageUri);
                        Picasso.get().load(imageUri).into(PanPersonal);
                        Log.w(TAG, "ResultUri=>" + imageUri);

                    }
                    if (clicked == "Front") {
                        frontUri = imageUri;
                        aadharFrontIV.setImageURI(imageUri);
                        Picasso.get().load(imageUri).into(aadharFrontIV);
                        Log.w(TAG, "ResultUri=>" + imageUri);

                    }
                    if (clicked == "Back") {
                        backUri = imageUri;
                        adharbackView.setImageURI(imageUri);
                        Picasso.get().load(imageUri).into(adharbackView);
                        Log.w(TAG, "ResultUri=>" + imageUri);
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(DocumentsUpdate.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void storePanDB() {
try {
    final DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("Pan_CardDetails");

    DatabaseReference PanRef = FirebaseDatabase.getInstance().getReference();
    StorageReference PanStorage = FirebaseStorage.getInstance().getReference().child(uid)
            .child("PanCard");


    final String[] path = new String[1];
    if (imageUri != null) {
        PanStorage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(DocumentsUpdate.this, "PanUploadSuccessful", Toast.LENGTH_SHORT).show();
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        path[0] = uri.toString();
                        Map<String, Object> imageObject = new HashMap<>();
                        imageObject.put("PanImage", path[0]);
                        reff.updateChildren(imageObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                PanCardUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);

                                pan.setVisibility(View.GONE);

                            }
                        });


                    }
                });

            }
        });

    }
}catch (Exception e){
    e.printStackTrace();
}
    }

    private void storeBankDB() {
        String name = bankHolderName.getText().toString().trim();
        String ifsc = bankIfsc.getText().toString().trim();
        String branch = bankBranchName.getText().toString().trim();
        String account = bankAcNo.getText().toString().trim();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("BankDetails");
        String pushkey=ref.push().getKey();

        Map<String, Object> bankdetails = new HashMap<>();
        bankdetails.put("AccHolder", name);
        bankdetails.put("AccIFSC", ifsc);
        bankdetails.put("AccBranch", branch);
        bankdetails.put("AccNumber", account);

        ref.child(pushkey).updateChildren(bankdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DocumentsUpdate.this, "BankDetailsUpdated", Toast.LENGTH_SHORT).show();
                bank.setVisibility(View.INVISIBLE);
                bankDetailsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);



            }
        });
    }

    //    public void verify(){
//        adharDetailsUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                adharDetailsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
//            }
//        });
//    }

    private void storeAadharDB() {

        try {
            final DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("AadharDetails");

            adharRef = FirebaseDatabase.getInstance().getReference();
            adharStorage = FirebaseStorage.getInstance().getReference().child(uid)
                    .child("Aadhar");
            StorageReference adharFront = adharStorage.child("Front");
            StorageReference adharBack = adharStorage.child("Back");


            final String[] path = new String[1];
            if (backUri != null) {
                adharBack.putFile(backUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(DocumentsUpdate.this, "BackUploadSuccessful", Toast.LENGTH_SHORT).show();

                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                path[0] = uri.toString();
                                backStatus=true;
                                Map<String, Object> imageObject = new HashMap<>();
                                imageObject.put("backImage", path[0]);
                                reff.updateChildren(imageObject);

                            }
                        });

                    }
                });
            } else
                Toast.makeText(DocumentsUpdate.this, "Please select back side of Aadhar Card", Toast.LENGTH_SHORT).show();
            ;

            if (frontUri != null) {
                adharFront.putFile(frontUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(DocumentsUpdate.this, "FrontUploadSuccessful", Toast.LENGTH_SHORT).show();
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                path[0] = uri.toString();
                                frontStatus=true;
                                Map<String, Object> imageObject = new HashMap<>();
                                imageObject.put("frontImage", path[0]);
                                reff.updateChildren(imageObject);



                            }
                        });

                    }
                });
            } else
                Toast.makeText(DocumentsUpdate.this, "Please select Front side of Aadhar Card", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }


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


}