package com.hotspot.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateBankDetails extends AppCompatActivity {
    EditText bankHolderName,bankBranchName,bankIfsc,bankAcNo;
    Button Submit;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bank_details);
try {
    SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    uid = userPref.getString("uid", "");


    bankAcNo = findViewById(R.id.bankAcNo);
    bankBranchName = findViewById(R.id.bankBranchName);
    bankHolderName = findViewById(R.id.bankHolderName);
    bankIfsc = findViewById(R.id.bankIfsc);
    Submit = findViewById(R.id.Submit);

    Submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (bankAcNo.getText().toString().isEmpty()) {
                bankAcNo.setError("Account Number Is Required");
                bankAcNo.requestFocus();
            } else if (bankBranchName.getText().toString().isEmpty()) {

                bankBranchName.setError("Account Number Is Required");
                bankBranchName.requestFocus();
            } else if (bankHolderName.getText().toString().isEmpty()) {

                bankHolderName.setError("Account Number Is Required");
                bankHolderName.requestFocus();
            } else if (bankIfsc.getText().toString().isEmpty()) {

                bankIfsc.setError("Account Number Is Required");
                bankIfsc.requestFocus();
            } else {
                storeOnDB();
            }


        }
    });
}catch(Exception e){
    e.printStackTrace();
}
    }

    private void storeOnDB() {
        String name=bankHolderName.getText().toString().trim();
        String ifsc=bankIfsc.getText().toString().trim();
        String branch=bankBranchName.getText().toString().trim();
        String account=bankAcNo.getText().toString().trim();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Partner").child(uid).child("Documents").child("BankDetails");
        Map<String,Object> bankdetails=new HashMap<>();
        bankdetails.put("Account_Holder_Name",name);
        bankdetails.put("Account_Ifsc",ifsc);
        bankdetails.put("BankBranch",branch);
        bankdetails.put("Account_Number",account);

        ref.updateChildren(bankdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateBankDetails.this,"BankDetailsUpdated",Toast.LENGTH_SHORT).show();
            }
        });
    }
}