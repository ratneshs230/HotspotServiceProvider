package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AllServices extends AppCompatActivity implements View.OnClickListener{
    LinearLayout nav_withdraw,nav_profile,nav_home;
    Intent intent;
    FirebaseAuth mAuth;
    String uid;
    String TAG="ALLServices";
    DatabaseReference reff;
    SharedPreferences pref;
    ServiceUserModel model;
    SharedPreferences.Editor edit;
    String phoneNumber;
    FirebaseUser user;
    LinearLayout manageShop,myOrder,Wallet,manageDoc,emergencyContact,feedbackRating,profileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);
try {

    //Notification Builder

    pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    phoneNumber=pref.getString("Phone","");
    mAuth = FirebaseAuth.getInstance();

    Log.w(TAG,"User=>"+user);
    if (mAuth.getCurrentUser() == null) {
        Intent intent = new Intent(AllServices.this, PhoneNumberActivity.class);
        startActivity(intent);
    }else
        uid=mAuth.getCurrentUser().getUid();
 checkDetails();
    reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(uid);

    model = new ServiceUserModel();
    fetchUserDetails(uid);
    //BottomNavBarCode
    profileUpdate=findViewById(R.id.profileUpdate);
    manageDoc=findViewById(R.id.manageDoc);
    manageShop=findViewById(R.id.manageShop);
    Wallet=findViewById(R.id.manageProducts);
    emergencyContact=findViewById(R.id.emergencyContact);
    feedbackRating=findViewById(R.id.feedbackRating);
    myOrder=findViewById(R.id.myOrder);

    manageShop.setOnClickListener(this);
    manageDoc.setOnClickListener(this);
    Wallet.setOnClickListener(this);
    emergencyContact.setOnClickListener(this);
    feedbackRating.setOnClickListener(this);
    myOrder.setOnClickListener(this);
    profileUpdate.setOnClickListener(this);
    nav_home = findViewById(R.id.nav_Home);
    nav_profile = findViewById(R.id.nav_Profile);
    nav_withdraw = findViewById(R.id.nav_Withdraw);



    nav_withdraw.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, Wallet.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    });

    nav_home.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, AllServices.class);
            startActivity(intent);
        }
    });
    nav_profile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, PartnerProfile.class);
            startActivity(intent);
        }
    });
}catch (Exception e){
    e.printStackTrace();
}
}

    private void checkDetails() {
        try {
            FirebaseDatabase.getInstance().getReference().child("Partner").child(phoneNumber).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(AllServices.this,"Complete Updating Your Profile",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AllServices.this, UserDetailsEdit.class);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
            }


    private void fetchUserDetails(String uid) {
try {
    edit=pref.edit();

    reff.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
            model = dataSnapshot.getValue(ServiceUserModel.class);
            edit.putString("userName", model.getName());
            edit.putString("WalletBalance", model.getWalletBalance());
            edit.putString("add1", model.getAdd1());
            edit.putString("add2", model.getAdd2());
            edit.putString("add3", model.getAdd3());
            edit.putString("phn", model.getPhn());
            edit.putString("Profileimage", model.getProfileimage());
            edit.putString("Servicetype", model.getServicetype());

                Log.w(TAG,"NAME=>"+model.getName());
            edit.apply();}

            Log.w(TAG, "ModelUserName=>" + model.getName() + "DatasnapShot=>" + dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}catch (Exception e){
    e.printStackTrace();
}
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profileUpdate:{
                Intent intent=new Intent(AllServices.this,UserDetailsEdit.class);
                startActivity(intent);

                break;
            }
            case R.id.manageDoc:{
                Intent intent=new Intent(AllServices.this,DocumentsUpdate.class);

                startActivity(intent);
                break;
            }
            case R.id.manageShop:{
                Intent intent=new Intent(AllServices.this,ManageShop.class);
                Log.w(TAG,"MANAGESHOP pressed");
                startActivity(intent);

                break;
            }
            case R.id.feedbackRating:{
                Intent intent=new Intent(AllServices.this,FeedbackRatings.class);
                startActivity(intent);

                break;
            }
            case R.id.manageProducts:{
                Intent intent=new Intent(AllServices.this,ManageProducts.class);
                startActivity(intent);

                break;
            }
            case R.id.emergencyContact:{
                Intent intent=new Intent(AllServices.this,EmergencyContact.class);
                startActivity(intent);

                break;
            }
            case R.id.myOrder:{
                Intent intent=new Intent(AllServices.this,MyOrders.class);
                startActivity(intent);
                break;
            }

        }
    }
}