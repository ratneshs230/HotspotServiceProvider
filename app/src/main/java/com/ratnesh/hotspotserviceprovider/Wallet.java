package com.ratnesh.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Wallet extends AppCompatActivity {
    LinearLayout walletLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        walletLayout=findViewById(R.id.walletLayout);

        walletLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           Intent intent=new Intent(Wallet.this,WalletOptions.class);
           startActivity(intent);
            }
        });
    }
}