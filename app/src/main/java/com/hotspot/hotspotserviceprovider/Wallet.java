package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;

public class Wallet extends AppCompatActivity {
    ConstraintLayout walletLayout;
    TextView walletBalance;
    String phone;
    DatabaseReference ref;
    String TAG="Wallet";
    ServiceUserModel model;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        try {
            SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            phone = userPref.getString("Phone", "");
            final SharedPreferences.Editor editor=userPref.edit();
            walletLayout = findViewById(R.id.walletLayout);
            walletBalance = findViewById(R.id.walletBalance);
            progressBar=findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
            ref = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                String channelId = getString(R.string.generalNotificationId);
                String channelName = getString(R.string.generalNotificationChannel);
                NotificationManager notificationManager =
                        getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                        channelName, NotificationManager.IMPORTANCE_LOW));
            }
            //Notification Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                String channelId = getString(R.string.transactionId);
                String channelName = getString(R.string.transactionNotification);
                NotificationManager notificationManager =
                        getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                        channelName, NotificationManager.IMPORTANCE_LOW));
            }

            model = new ServiceUserModel();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                    Log.w(TAG, "DataSnapshot=>" + dataSnapshot);
                    model = dataSnapshot.getValue(ServiceUserModel.class);
                    walletBalance.setText(model.getWalletBalance());
                    progressBar.setVisibility(View.GONE);
                    editor.putString("Walletbalance",model.getWalletBalance());
                    editor.apply();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            walletLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Wallet.this, WalletOptions.class);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}