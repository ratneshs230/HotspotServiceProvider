package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;

public class WalletOptions extends AppCompatActivity implements View.OnClickListener{
    TextView balance;
    TextView statement,send,add;
    String uid;
    DatabaseReference ref;
    ServiceUserModel model;
   String TAG="WalletOptions";
   String phone;
   ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_options);
        try {
            SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            uid = userPref.getString("uid", "");
            phone=userPref.getString("Phone","");
            balance = findViewById(R.id.balance);
            statement = findViewById(R.id.statementRequest);
            send = findViewById(R.id.sentToBank);
            add = findViewById(R.id.addMoney);
            progressBar=findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
            ref = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone);

            model = new ServiceUserModel();

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.w(TAG, "DataSnapshot=>" + dataSnapshot);
                    model = dataSnapshot.getValue(ServiceUserModel.class);
                    progressBar.setVisibility(View.GONE);
                    balance.setText(model.getWalletBalance());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            statement.setOnClickListener(this);
            add.setOnClickListener(this);
            send.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.statementRequest:{

                Intent intent=new Intent(WalletOptions.this,RequestStatement.class);
                startActivity(intent);
                break;
            }
            case R.id.sentToBank:{

                Intent intent=new Intent(WalletOptions.this,SendToBank.class);
                startActivity(intent);
                break;
            }
            case R.id.addMoney:{
                Intent intent=new Intent(WalletOptions.this,AddMoney.class);
                startActivity(intent);

                break;
            }
        }
    }
}