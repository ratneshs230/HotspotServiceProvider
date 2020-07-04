package com.ratnesh.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DocumentsUpdate extends AppCompatActivity implements View.OnClickListener{
ConstraintLayout bankDetailsUpdate,PanCardUpdate,gstDetailUpdate,adharDetailsUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_update);

        adharDetailsUpdate=findViewById(R.id.adharDetailsUpdate);
        gstDetailUpdate=findViewById(R.id.gstDetailUpdate);
        PanCardUpdate=findViewById(R.id.PanCardUpdate);
        bankDetailsUpdate=findViewById(R.id.bankDetailsUpdate);

        adharDetailsUpdate.setOnClickListener(this);
        gstDetailUpdate.setOnClickListener(this);
        PanCardUpdate.setOnClickListener(this);
        bankDetailsUpdate.setOnClickListener(this);
    }

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