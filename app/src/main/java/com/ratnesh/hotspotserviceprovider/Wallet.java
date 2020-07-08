package com.ratnesh.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Wallet extends AppCompatActivity {
    LinearLayout walletLayout;
    TextView walletBalance;
    String uid;
    DatabaseReference ref;
    String TAG="Wallet";
    ServiceUserModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        uid = userPref.getString("uid", "");

        walletLayout=findViewById(R.id.walletLayout);
        walletBalance=findViewById(R.id.walletBalance);

        ref= FirebaseDatabase.getInstance().getReference().child("Partner").child(uid);

        model=new ServiceUserModel();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w(TAG,"DataSnapshot=>"+dataSnapshot);
                model=dataSnapshot.getValue(ServiceUserModel.class);
                walletBalance.setText(model.getWalletBalance());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        walletLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           Intent intent=new Intent(Wallet.this,WalletOptions.class);
           startActivity(intent);
            }
        });
    }
}