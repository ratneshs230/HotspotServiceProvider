package com.hotspot.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DocumentsUpdate extends AppCompatActivity implements View.OnClickListener{
TextView bankDetailsUpdate,PanCardUpdate,gstDetailUpdate,adharDetailsUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_update);

        try {
            adharDetailsUpdate = findViewById(R.id.adharDetailsUpdate);
            gstDetailUpdate = findViewById(R.id.gstDetailUpdate);
            PanCardUpdate = findViewById(R.id.PanCardUpdate);
            bankDetailsUpdate = findViewById(R.id.bankDetailsUpdate);

            adharDetailsUpdate.setOnClickListener(this);
            gstDetailUpdate.setOnClickListener(this);
            PanCardUpdate.setOnClickListener(this);
            bankDetailsUpdate.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    public void verify(){
//        adharDetailsUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                adharDetailsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checked, 0);
//            }
//        });
//    }
    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()){

            case R.id.adharDetailsUpdate:{
                intent=new Intent(DocumentsUpdate.this,UploadAAdhar.class);
                startActivity(intent);

                break;
            }
            case R.id.gstDetailUpdate:{
                intent=new Intent(DocumentsUpdate.this,UpdateGSTDetails.class);
                startActivity(intent);

                break;
            }
            case R.id.PanCardUpdate:{
                intent=new Intent(DocumentsUpdate.this,UploadPan.class);
                startActivity(intent);

                break;
            }
            case R.id.bankDetailsUpdate:{
                intent=new Intent(DocumentsUpdate.this,UpdateBankDetails.class);
                startActivity(intent);
                break;
            }
        }

    }
}