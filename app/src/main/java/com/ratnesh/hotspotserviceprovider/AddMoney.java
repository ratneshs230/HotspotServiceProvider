package com.ratnesh.hotspotserviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easebuzz.payment.kit.PWECouponsActivity;
import datamodels.PWEStaticDataModel;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AddMoney extends AppCompatActivity {
    TextView balance;
    EditText amount;
    Button upi_payment, cash_paymentBtn;
    ServiceUserModel model;
    private static final int PWE_REQUEST_CODE = 100;
    String uid, name, add1, add2, mail, phn, editAmt;
    String TAG = "AddMoney";
    LinearLayout upiLayout, CardLayout;
    TextView Upi, Card;
    EditText cardNumber, cvv, valid;
    Double value;
    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        model = new ServiceUserModel();
        SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        uid = userPref.getString("uid", "");
        name = userPref.getString("userName", "");
        add1 = userPref.getString("add1", "");
        add2 = userPref.getString("add2", "");
        mail = userPref.getString("mail", "");
        phn = userPref.getString("phn", "");


        Log.w(TAG, "Name=>" + name);

        cash_paymentBtn = findViewById(R.id.cash_paymentBtn);
        cardNumber = findViewById(R.id.cardNumber);
        cvv = findViewById(R.id.cvvInput);
        valid = findViewById(R.id.validDate);
        Upi = findViewById(R.id.upi);
        Card = findViewById(R.id.cardPayment);
        upiLayout = findViewById(R.id.upilayout);
        CardLayout = findViewById(R.id.cardLayout);
        amount = findViewById(R.id.amount);
        balance = findViewById(R.id.balance);
        upi_payment = findViewById(R.id.upi_payment);


        //handling visibility
        //Card
        Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upiLayout.setVisibility(View.GONE);

                if (CardLayout.getVisibility() == View.GONE) {
                    CardLayout.setVisibility(View.VISIBLE);
                } else
                    CardLayout.setVisibility(View.GONE);
            }

        });
        //Upi
        Upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardLayout.setVisibility(View.GONE);

                if (upiLayout.getVisibility() == View.GONE) {
                    upiLayout.setVisibility(View.VISIBLE);
                } else
                    upiLayout.setVisibility(View.GONE);
            }

        });

        //upiPayment
        upi_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount.getText().toString().trim().isEmpty()) {
                    amount.setError("Amount is Required");
                    amount.requestFocus();
                } else {
                    payUsingEaseBuzz();

                }
            }
        });

    }

    private void payUsingEaseBuzz() {
        String key = "2PBP7IABZ2";
        String salt = "DAH88E3UWQ";
        String text = amount.getText().toString();
        if (!text.isEmpty())
            try {
                value = Double.parseDouble(text);
                // it means it is double

            } catch (Exception e1) {
                // this means it is not double
                e1.printStackTrace();
            }

        String hashString = key  + uid + value + "testInfo"  + name + mail + "UserDefinedField1" + "UserDefinedField2" +  salt  + key;
        Log.w(TAG, "HAshString" + hashString);

        String encrypted = encryptThisString(hashString);

        Intent intentProceed = new Intent(AddMoney.this, PWECouponsActivity.class);
        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intentProceed.putExtra("txnid", uid);
        intentProceed.putExtra("amount", value);
        intentProceed.putExtra("productinfo", "testInfo");
        intentProceed.putExtra("firstname", name);
        intentProceed.putExtra("email", mail);
        intentProceed.putExtra("phone", phn);
        intentProceed.putExtra("key", "2PBP7IABZ2");
        intentProceed.putExtra("udf1", "UserDefinedField1");
        intentProceed.putExtra("udf2", "UserDefinedField2");
        intentProceed.putExtra("address1", add1);
        intentProceed.putExtra("address2", add2);
        intentProceed.putExtra("city", "Lucknow");
        intentProceed.putExtra("state", "UttarPradesh");
        intentProceed.putExtra("country", "India");
        intentProceed.putExtra("zipcode", "226025");
        intentProceed.putExtra("hash", encrypted);
        intentProceed.putExtra("unique_id", uid);
        intentProceed.putExtra("pay_mode", "test");
        startActivityForResult(intentProceed, PWEStaticDataModel.PWE_REQUEST_CODE);


    }

    public static String encryptThisString(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");


            byte[] messageDigest = md.digest(input.getBytes());

           BigInteger no = new BigInteger(1, messageDigest);

           String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            Log.w("AddMoney", "HashText=>" + hashtext);
            return hashtext;
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response " + resultCode);

       if(data!=null){

           if(requestCode==PWEStaticDataModel.PWE_REQUEST_CODE){
               try{

               }catch (Exception e){
                   e.printStackTrace();
               }
           }
       }
    }


}
