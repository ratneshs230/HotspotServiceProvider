package com.hotspot.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class EmergencyContact extends AppCompatActivity implements View.OnClickListener{
    TextView contact;
    Button callBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
    contact=findViewById(R.id.contactNumber);
    callBtn=findViewById(R.id.callBtn);

    callBtn.setOnClickListener(this);
    contact.setText("Contact Support: 9026203040");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.callBtn:{

        String phone="+919026203040";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));


        startActivity(intent);

            break;}
        }
    }
}