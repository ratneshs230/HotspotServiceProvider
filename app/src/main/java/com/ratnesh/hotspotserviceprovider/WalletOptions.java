package com.ratnesh.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WalletOptions extends AppCompatActivity implements View.OnClickListener{
    TextView balance;
    ConstraintLayout statement,send,add;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_options);

        SharedPreferences userPref=getSharedPreferences("PartnerPref",MODE_PRIVATE);
        uid=userPref.getString("uid","");

        balance=findViewById(R.id.balance);
        statement=findViewById(R.id.statementRequest);
        send=findViewById(R.id.sentToBank);
        add=findViewById(R.id.addMoney);

        statement.setOnClickListener(this);
        add.setOnClickListener(this);
        send.setOnClickListener(this);

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