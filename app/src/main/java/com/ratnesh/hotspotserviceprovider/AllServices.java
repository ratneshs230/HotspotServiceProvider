package com.ratnesh.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllServices extends AppCompatActivity {
    LinearLayout nav_withdraw,nav_profile,nav_home;
    Intent intent;
    FirebaseAuth mAuth;
    String uid;
    String TAG="ALLServices";
    DatabaseReference reff;
    SharedPreferences pref;
    ServiceUserModel model;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);

        pref=getSharedPreferences("PartnerPref",MODE_PRIVATE);
        edit=pref.edit();
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getUid().equals("")){
            Intent intent=new Intent(AllServices.this,PhoneNumberActivity.class);
            startActivity(intent);
        }
        uid=getIntent().getStringExtra("uid");
        Log.w(TAG,"UID recieved="+uid);
        if(null == uid){
            uid=pref.getString("uid","");
        }
        reff = FirebaseDatabase.getInstance().getReference().child("Partner").child(uid);

        model=new ServiceUserModel();
        fetchUserDetails(uid);
        //BottomNavBarCode
        nav_home=findViewById(R.id.nav_Home);
        nav_profile=findViewById(R.id.nav_Profile);
        nav_withdraw=findViewById(R.id.nav_Withdraw);
        nav_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(AllServices.this,Wallet.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(AllServices.this,AllServices.class);
                startActivity(intent);            }
        });
        nav_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(AllServices.this,PartnerProfile.class);
                startActivity(intent);            }
        });
    }

    private void fetchUserDetails(String uid) {

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                model=dataSnapshot.getValue(ServiceUserModel.class);
                edit.putString("userName",model.getUserName());
                edit.putString("WalletBalance",model.getWalletBalance());
                edit.putString("add1",model.getAdd1());
                edit.putString("add2",model.getAdd2());
                edit.putString("add3",model.getAdd3());
                edit.putString("phn",model.getPhn());
                edit.putString("Profileimage",model.getProfileimage());
                edit.putString("Servicetype",model.getServicetype());
                edit.putString("mail",model.getMail());
                edit.commit();
                edit.apply();
                Log.w(TAG,"ModelUserName=>"+model.getUserName()+"DatasnapShot=>"+dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}