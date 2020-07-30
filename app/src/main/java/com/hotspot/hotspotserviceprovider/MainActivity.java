package com.hotspot.hotspotserviceprovider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
        try {
            setContentView(R.layout.activity_main);

            SharedPreferences authPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            boolean loginState = authPref.getBoolean("Login_State", false);

            user= FirebaseAuth.getInstance().getCurrentUser();

        if (loginState && user!=null) {
                intent = new Intent(MainActivity.this, AllServices.class);
            } else {

                intent = new Intent(MainActivity.this, PhoneNumberActivity.class);

            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(intent);

                    finish();

                }
            }, SPLASH_SCREEN_TIME_OUT);
        }catch (Exception e){
            e.printStackTrace();
        }



    }
}