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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotspot.hotspotserviceprovider.modelClasses.ProductsModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddProduct extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    TextInputEditText name,description,price;
    ImageView pic;
    Button addBtn;
    Uri imageUri;
    DatabaseReference reference;
    ProductsModel model;
    String uid;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    private String[] cameraPermission;
    private String[] storagePermission;
    SharedPreferences pref;
    String TAG="AddProductFragment";
    String[]  shopCategory;
    Map<String,Object> shopDetails;
    Spinner shopSelect;
    String shopName,key,category;
    private String phone;
    private String mParam2;
    List<String> shops = new ArrayList<String>();
    List<String> shopkey=new ArrayList<String>();
    List<String> Category=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        phone=pref.getString("Phone","");
        if(phone==null){
            Intent intent=new Intent(AddProduct.this,AllServices.class);
            startActivity(intent);
        }
        pref=getSharedPreferences("PartnerPref", MODE_PRIVATE);
        uid=pref.getString("uid","");

        shopDetails=new HashMap<>();

        shopSelect=findViewById(R.id.shopSelect);
        pic=findViewById(R.id.productImage);
        name=findViewById(R.id.productName);
        description=findViewById(R.id.productDesc);
        price=findViewById(R.id.productPrice);


        getShops();
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        shopSelect.setOnItemSelectedListener(this);

        pic.setOnClickListener(this);
        addBtn=findViewById(R.id.add_btn);

        reference= FirebaseDatabase.getInstance().getReference().child("Shops").child("Products");
        addBtn.setOnClickListener(this);
    }
    public void getShops(){
        FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("ShopDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                     shops = new ArrayList<String>();
                     shopkey=new ArrayList<>();
                    Log.w(TAG,"DatasnapShot=>"+dataSnapshot);

                    for (DataSnapshot model : dataSnapshot.getChildren()) {
                        String key=model.child("pushkey").getValue(String.class);
                        String shopName = model.child("shopName").getValue(String.class);
                        String cat=model.child("shopCategory").getValue(String.class);
                        shopkey.add(key);
                        shops.add(shopName);
                        Category.add(cat);
                    }
//                    Set<String> set = new HashSet<>(shops);
//                    shops.clear();
//                    shops.addAll(set);
//                    Log.w(TAG, "models=>" + shops);

                    ArrayAdapter<String> adapter =new ArrayAdapter<>(AddProduct.this,android.R.layout.simple_spinner_item,shops);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    shopSelect.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    pic.setImageURI(imageUri);
                    Picasso.get().load(imageUri).into(pic);
                    Log.w(TAG, "ResultUri=>" + imageUri);


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception e = result.getError();
                    Toast.makeText(AddProduct.this, e + "", Toast.LENGTH_LONG).show();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_btn) {
            if (name.getText().toString().trim().equals("")) {
                name.setError("Enter Product Name");
            } else if (price.getText().toString().trim().equals(""))
                price.setError("Price is mandatory");
            else if (imageUri == null) {
                Toast.makeText(AddProduct.this, "Select a Product image", Toast.LENGTH_SHORT).show();
            } else
                saveOnDB();
        } else if (view.getId() == R.id.productImage) {
            openImageImportDialog();
        }
    }


        private void openImageImportDialog() {
            String[] items = {"Camera", "Gallery"};

            AlertDialog.Builder dialog = new AlertDialog.Builder(AddProduct.this);

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
        boolean resultStorage = ContextCompat.checkSelfPermission(AddProduct.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return resultStorage;
    }

    private boolean checkCameraPermission() {

        boolean resultCamera = ContextCompat.checkSelfPermission(AddProduct.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean resultStorage = ContextCompat.checkSelfPermission(AddProduct.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return resultCamera && resultStorage;
    }

    //permissionRequest
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(AddProduct.this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(AddProduct.this, cameraPermission, CAMERA_REQUEST_CODE);


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



    private void saveOnDB() {
        try {
            final ProductsModel model = new ProductsModel();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Products");

            final String[] path = new String[1];
            final String pushkey = reference.push().getKey();
            if (imageUri != null) {
                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(AddProduct.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                path[0] = uri.toString();
                                Map<String, Object> imageObject = new HashMap<>();
                                imageObject.put("ProductImage", path[0]);
                                reference.child(pushkey).updateChildren(imageObject);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, e.toString());
                    }

                });
            }

            model.setProductName(name.getText().toString().trim());
            model.setProductDescription(description.getText().toString().trim());
            model.setProductPrice(price.getText().toString().trim());
            model.setProductPushkey(pushkey);
            model.setProductCategory(category);
            model.setSellerUid(uid);
//            model.setShopKey();

            reference.child(pushkey).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AddProduct.this,"Product Added Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AddProduct.this,ManageProducts.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
             });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        shopName=adapterView.getItemAtPosition(i).toString();
        key=shopkey.get(i);
        category=Category.get(i);
        Log.w(TAG,"SHopname=>"+shopName+"Key=>"+key);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {


    }
}