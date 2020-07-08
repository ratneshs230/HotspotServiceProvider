package com.ratnesh.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    int SPLASH_SCREEN_TIME_OUT=1000;
    FirebaseUser user;
    String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences authPref=getSharedPreferences("PartnerPref",MODE_PRIVATE);
        boolean loginState=authPref.getBoolean("Login_State",false);
        String uid=authPref.getString("uid","");

        user= FirebaseAuth.getInstance().getCurrentUser();
        Log.w(TAG,"FirebaseUser=>"+user);

        if(loginState && user!=null){
            intent=new Intent(MainActivity.this, AllServices.class);
            intent.putExtra("uid",uid);
        }
        else {

            intent=new Intent(MainActivity.this, PhoneNumberActivity.class);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(intent);

                finish();

            }
        }, SPLASH_SCREEN_TIME_OUT);




    }
}