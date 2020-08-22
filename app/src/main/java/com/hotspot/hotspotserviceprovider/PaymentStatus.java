package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;

public class PaymentStatus extends AppCompatActivity {

    TextView paymentStatus,paymentAmount,updatedBalance;
    String amt,balance;
    Boolean status;
        ConstraintLayout statusLayout;
        DatabaseReference ref;
        String phone,TAG="PaymentStatus";
        String WalletBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);

        try {
            amt=getIntent().getStringExtra("amt");
            status=getIntent().getBooleanExtra("status",false);
            SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            phone = userPref.getString("Phone", "");

            ref= FirebaseDatabase.getInstance().getReference().child("Partner").child(phone);


            statusLayout=findViewById(R.id.statusLayout);

            paymentAmount = findViewById(R.id.paymentAmount);
            paymentStatus = findViewById(R.id.paymentStatus);
            updatedBalance = findViewById(R.id.updatedBalance);

            paymentAmount.setText(amt);


            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Log.w(TAG, "DataSnapshot=>" + dataSnapshot);
                        WalletBalance=dataSnapshot.child("walletBalance").getValue(String.class);
                        Log.w(TAG,"WalletBbalance=>"+WalletBalance);
                        updatedBalance.setText(getString(R.string.Rs)+WalletBalance);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(status){
                //update ui for success
                statusLayout.setBackgroundColor(Color.parseColor("#7CB342"));
                paymentStatus.setText("Money Added successfully");

            }else {
                statusLayout.setBackgroundColor(Color.parseColor("#6DF80000"));
                paymentStatus.setText("Money Adding Failed");

            }
        }catch (Exception  e){
            e.printStackTrace();
        }

    }
}