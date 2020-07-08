package com.ratnesh.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneNumberActivity extends AppCompatActivity  implements View.OnClickListener{
    EditText phn;
        Button next_login;
        TextView forgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);


        phn = findViewById(R.id.phn);
        next_login = findViewById(R.id.login_next);

        forgotPass=findViewById(R.id.forgotPassword);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        next_login.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
            if( view.getId()==R.id.login_next){

                String number = phn.getText().toString().trim();

                if (number.length() != 10 ) {
                    phn.setError("Valid number is required");
                    phn.requestFocus();
                    return;
                }

                String phonenumber = "+" + "91" + number;

                Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);

            }
    }
}