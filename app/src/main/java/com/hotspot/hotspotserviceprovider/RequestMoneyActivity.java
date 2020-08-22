package com.hotspot.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotspot.hotspotserviceprovider.modelClasses.WithdrawalRequest;

import org.w3c.dom.Text;

public class RequestMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    EditText amount;
    Button sendRequest;
    TextView balance;
    WithdrawalRequest model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);

        model=new WithdrawalRequest();

        amount=findViewById(R.id.amount);
        sendRequest=findViewById(R.id.sendRequest);
        balance=findViewById(R.id.balance);

        sendRequest.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.sendRequest:{
                sendWithdrawalRequest();
                break;
            }
        }
    }

    private void sendWithdrawalRequest() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Withdraw Requests");
        String pushkey=ref.push().getKey();
        model.setAmount(amount.getText().toString().trim());


        ref.child(pushkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RequestMoneyActivity.this,"Money Request successful",Toast.LENGTH_LONG).show();
            }
        });


    }
}