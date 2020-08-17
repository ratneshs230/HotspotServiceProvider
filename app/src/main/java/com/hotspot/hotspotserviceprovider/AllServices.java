package com.hotspot.hotspotserviceprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AllServices extends AppCompatActivity implements View.OnClickListener{
    LinearLayout nav_withdraw,nav_profile,nav_home;

    CarouselView customCarouselView;

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
    LinearLayoutManager linearLayoutManager;
    LinearLayout manageShop,myOrder,Wallet,manageDoc,emergencyContact,feedbackRating,profileUpdate,logoutLayout;
    RecyclerView adsRecycler;

    int[] sampleImages = {R.drawable.cb, R.drawable.medics, R.drawable.one, R.drawable.shopstop2, R.drawable.shopstopv4,R.drawable.shopstopv5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);
try {

    //Notification Builder

    pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    phoneNumber=pref.getString("Phone","");
    mAuth = FirebaseAuth.getInstance();

    Log.w(TAG,"User=>"+mAuth.getCurrentUser());
    if (mAuth.getCurrentUser() == null) {
        Intent intent = new Intent(AllServices.this, PhoneNumberActivity.class);
        startActivity(intent);
    }else
        uid=mAuth.getCurrentUser().getUid();
 checkDetails();
    reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(phoneNumber);

    model = new ServiceUserModel();
    fetchUserDetails(uid);



    //BottomNavBarCode
    customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
    logoutLayout=findViewById(R.id.logoutLayout);
    profileUpdate=findViewById(R.id.profileUpdate);
    manageDoc=findViewById(R.id.manageDoc);
    manageShop=findViewById(R.id.manageShop);
    Wallet=findViewById(R.id.manageProducts);
    emergencyContact=findViewById(R.id.emergencyContact);
    feedbackRating=findViewById(R.id.feedbackRating);
    myOrder=findViewById(R.id.myOrder);


    adsRecycler=findViewById(R.id.adsRecycler);
    linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,true);
    manageShop.setOnClickListener(this);
    manageDoc.setOnClickListener(this);
    Wallet.setOnClickListener(this);
    emergencyContact.setOnClickListener(this);
    feedbackRating.setOnClickListener(this);
    myOrder.setOnClickListener(this);
    profileUpdate.setOnClickListener(this);
    logoutLayout.setOnClickListener(this);
    nav_home = findViewById(R.id.nav_Home);
    nav_profile = findViewById(R.id.nav_Profile);
    nav_withdraw = findViewById(R.id.nav_Withdraw);



    customCarouselView.setPageCount(sampleImages.length);
    customCarouselView.setSlideInterval(3000);
    customCarouselView.setViewListener(viewListener);

    nav_withdraw.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(AllServices.this, Wallet.class);
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

    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);

            ImageView fruitImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

            fruitImageView.setImageResource(sampleImages[position]);

            customCarouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);

            return customView;
        }
    };

    View.OnClickListener pauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customCarouselView.pauseCarousel();
            customCarouselView.reSetSlideInterval(0);
        }
    };

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
            edit.putString("referralCode",model.getReferralCode());
            edit.putString("userName", model.getName());
            edit.putString("WalletBalance", model.getWalletBalance());
            edit.putString("add1", model.getAdd1());
            edit.putString("add2", model.getAdd2());
            edit.putString("add3", model.getAdd3());
            edit.putString("phn", model.getPhn());
            edit.putString("Profileimage", model.getProfileimage());
            edit.putString("Servicetype", model.getServicetype());
            edit.putString("city",model.getCity());
            edit.putString("state",model.getState());



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
                Intent intent=new Intent(AllServices.this,ReferralCode.class);
                startActivity(intent);

                break;
            }
            case R.id.myOrder:{
                Intent intent=new Intent(AllServices.this,MyOrders.class);
                startActivity(intent);
                break;
            }
            case R.id.logoutLayout:{
                FirebaseAuth.getInstance().signOut();
                SharedPreferences pref=getSharedPreferences("PartnerPref",MODE_PRIVATE);
                SharedPreferences.Editor edit=pref.edit();
                edit.clear();
                Intent intent=new Intent(AllServices.this,PhoneNumberActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }

        }
    }
}