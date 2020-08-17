package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;
import com.hotspot.hotspotserviceprovider.modelClasses.TransactionModel;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddMoney extends AppCompatActivity {
    TextView balance;
    EditText amount;
    Button cardPayment;
    ServiceUserModel model;
    private static final int PWE_REQUEST_CODE = 100;
    String uid, name, add1, add2, mail, phn, editAmt;
    String TAG = "AddMoney";
    LinearLayout upiLayout, CardLayout;
    TextView Upi, Card;
    EditText cardNumber, cvv, valid;
    Double value;
    final int UPI_PAYMENT = 0;
    String amt;
    DatabaseReference ref,transactionsref;
    String userBalance;
    String rs;
    TransactionModel transactionModel;
    String timeFormat[],time,phone;

    String city,state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        model = new ServiceUserModel();
        SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        uid = userPref.getString("uid", "");

        amount = findViewById(R.id.amount);
        balance = findViewById(R.id.balance);
        cardPayment = findViewById(R.id.cardPayment);
        amt = amount.getText().toString().trim();


        name = userPref.getString("userName", "");
        add1 = userPref.getString("add1", "");
        add2 = userPref.getString("add2", "");
        mail = userPref.getString("mail", "");
        phn = userPref.getString("Phone", "");
        city=userPref.getString("city","");
        state=userPref.getString("state","");
        Log.w(TAG, "NAME=>" + name+"UID=>"+uid);
        rs = getString(R.string.Rs);
        transactionModel=new TransactionModel();
        ref = FirebaseDatabase.getInstance().getReference().child("Partner").child(phn);
        transactionsref=FirebaseDatabase.getInstance().getReference().child("Transactions").child(phn);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(ServiceUserModel.class);


                userBalance = model.getWalletBalance();
                Log.w(TAG, "UserBalance=>" + userBalance);
                balance.setText("Available Balance :" + rs + " " + userBalance);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.w(TAG, "Name=>" + name);



        //EaseBuzz
        cardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount.getText().toString().trim().isEmpty()) {
                    amount.setError("Amount is Required");
                    amount.requestFocus();
                } else {
                    amt = amount.getText().toString().trim();
                    payUsingEaseBuzz();
                    // payUsingUpi(name,"ratneshs230-1@okhdfcbank","Add Money to Hotspot Wallet",amt);

                }
            }
        });

    }

    private void payUsingEaseBuzz() {
//        String key = "2PBP7IABZ2";
//        String salt = "DAH88E3UWQ";
//prod key=>
        String key="CSVC8AHW8D";
        String salt="IHSTFNAU4O";
        String txnID=getSaltString();
        Log.w(TAG,"TxnID=>"+txnID);
        ref= FirebaseDatabase.getInstance().getReference();
        String pushkey=ref.push().getKey();
        String text = amount.getText().toString();
        if (!text.isEmpty())
            try {
                value = Double.parseDouble(text);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        String hashString = key + "|" + txnID  + "|" + value + "|" + "testInfo" + "|" + name + "|"
                + "contacthotspoot@gmail.com" + "|" + "" + "|" + "" + "|" + "" + "|"
                +
                "" + "|" + "" + "||||||" + salt + "|" + key;

        String encrypted = encryptThisString(hashString);
        Log.w("AddMoney", "hashString=>" + hashString);

        Log.w("AddMoney", "HashText=>" + encrypted);

        Intent intentProceed = new Intent(AddMoney.this, PWECouponsActivity.class);
        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intentProceed.putExtra("txnid", txnID);
        intentProceed.putExtra("amount", value);
        intentProceed.putExtra("productinfo", "testInfo");
        intentProceed.putExtra("firstname", name);
        intentProceed.putExtra("email", "contacthotspoot@gmail.com");
        intentProceed.putExtra("phone", phn);
        intentProceed.putExtra("key", key);
        intentProceed.putExtra("udf1", "");
        intentProceed.putExtra("udf2", "");
        intentProceed.putExtra("udf3", "");
        intentProceed.putExtra("udf4", "");
        intentProceed.putExtra("udf5", "");
        intentProceed.putExtra("udf6", "");
        intentProceed.putExtra("udf7", "");
        intentProceed.putExtra("udf8", "");
        intentProceed.putExtra("address1", add1);
        intentProceed.putExtra("address2", add2);
        intentProceed.putExtra("city", city);
        intentProceed.putExtra("state", state);
        intentProceed.putExtra("country", "India");
        intentProceed.putExtra("zipcode", "226025");
        intentProceed.putExtra("hash", encrypted);
        intentProceed.putExtra("unique_id", "");
        intentProceed.putExtra("pay_mode", "production");
        startActivityForResult(intentProceed, PWEStaticDataModel.PWE_REQUEST_CODE);


    }
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 12) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//    void payUsingUpi(String name, String upiId, String note, String amount) {
//        Log.e("main ", "name " + name + "--up--" + upiId + "--" + note + "--" + amount);
//        Uri uri = Uri.parse("upi://pay").buildUpon()
//                .appendQueryParameter("pa", upiId)
//                .appendQueryParameter("pn", name)
//                //.appendQueryParameter("mc", "")
//                //.appendQueryParameter("tid", "02125412")
//                .appendQueryParameter("tr", "83183655")
//                .appendQueryParameter("tn", note)
//                .appendQueryParameter("am", 1 + "")
//                .appendQueryParameter("cu", "INR")
//                //.appendQueryParameter("refUrl", "blueapp")
//                .build();
//
//        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
//        upiPayIntent.setData(uri);
//
//        // will always show a dialog to user to choose an app
//        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
//
//        // check if intent resolves
//        if (null != chooser.resolveActivity(getPackageManager())) {
//            startActivityForResult(chooser, UPI_PAYMENT);
//        } else {
//            Toast.makeText(AddMoney.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("main ", "response " + resultCode);

            switch (requestCode) {
                case UPI_PAYMENT:
                    if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                        if (data != null) {
                            String trxt = data.getStringExtra("response");
                            Log.e("UPI", "onActivityResult: " + trxt);
                            ArrayList<String> dataList = new ArrayList<>();
                            dataList.add(trxt);
                            upiPaymentDataOperation(dataList);
                        } else {
                            Log.e("UPI", "onActivityResult: " + "Return data is null");
                            ArrayList<String> dataList = new ArrayList<>();
                            dataList.add("nothing");
                            upiPaymentDataOperation(dataList);
                        }
                    } else {
                        //when user simply back without payment
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                    break;
            }
        }

        private void upiPaymentDataOperation(ArrayList<String> data) {
            if (isConnectionAvailable(AddMoney.this)) {
                String str = data.get(0);
                Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
                String paymentCancel = "";
                if(str == null) str = "discard";
                String status = "";
                String approvalRefNo = "";
                String response[] = str.split("&");
                for (int i = 0; i < response.length; i++) {
                    String equalStr[] = response[i].split("=");
                    if(equalStr.length >= 2) {
                        if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                            status = equalStr[1].toLowerCase();
                        }
                        else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                            approvalRefNo = equalStr[1];
                        }
                    }
                    else {
                        paymentCancel = "Payment cancelled by user.";
                    }
                }

                if (status.equals("success")) {
                    //Code to handle successful transaction here.
                    Toast.makeText(AddMoney.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                    Integer value=Integer.valueOf(userBalance)+Integer.valueOf(amt);

                    Map<String,Object> newBalance=new HashMap<>();
                    newBalance.put("walletBalance",value);
                    ref.child("WalletBalance").setValue(value.toString());


                    //    moveRecord(cartRef,orderRef.child(key).child("OrderList"));
                    Log.e("UPI", "payment successfull: "+approvalRefNo);
                }
                else if("Payment cancelled by user.".equals(paymentCancel)) {
                    Toast.makeText(AddMoney.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                    Log.e("UPI", "Cancelled by user: "+approvalRefNo);

                }
                else {
                    Toast.makeText(AddMoney.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                    Log.e("UPI", "failed payment: "+approvalRefNo);

                }
            } else {
                Log.e("UPI", "Internet issue: ");

                Toast.makeText(AddMoney.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
            }
        }
    */
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response " + resultCode);
        Intent intent=new Intent(AddMoney.this,PaymentStatus.class);
        String pushkey= FirebaseDatabase.getInstance().getReference().child("Transactions").child(phn).push().getKey();

        if (data != null) {

            if (requestCode == PWEStaticDataModel.PWE_REQUEST_CODE) {
                try {

                    transactionModel.setTransactionAmount(amt);
                    transactionModel.setTransactionID(uid);
                    transactionModel.setTransactionStatus("Success");

                    timeFormat= Calendar.getInstance().getTime().toString().split("GMT");
                    time=timeFormat[0].trim();

                    Log.w(TAG,"Time=>"+time);
                    transactionModel.setTransactionTime(time);

                    String result = data.getStringExtra("result");
                    String payment_response = data.getStringExtra("payment_response");

                    if (result.contains(PWEStaticDataModel.TXN_SUCCESS_CODE)) {
                        Toast.makeText(AddMoney.this, "Payment Successful", Toast.LENGTH_SHORT).show();


                        transactionModel.setTransactionStatus("Success");

                        Log.w(TAG,"Time=>"+time);
                        transactionModel.setTransactionTime(time);
                        Map<String,Object> newBal=new HashMap<>();
                        String newAmt=String.valueOf(Integer.valueOf(userBalance)+Integer.valueOf(amount.getText().toString()));
                        newBal.put("walletBalance",newAmt);
                        FirebaseDatabase.getInstance().getReference().child("Partner").child(phn).updateChildren(newBal);
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        //PWWtaticDataModel.TXN_SUCCESS_CODE is a string constant and its value is “payment_successfull”
                        //Code here will execute if the payment transaction completed successfully.
                        // here merchant can show the payment success message.
                    } else if (result.contains(PWEStaticDataModel.TXN_TIMEOUT_CODE)) {
                        Toast.makeText(AddMoney.this, "Transaction Timed out", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        //PWEStaticDataModel.TXN_TIMEOUT_CODE is a string constant and its value is “txn_session_timeout”
                        //Code here will execute if the payment transaction failed because of the transaction time out.
                        // here merchant can show the payment failed message.
                    } else if (result.contains(PWEStaticDataModel.TXN_BACKPRESSED_CODE)) {
                        Toast.makeText(AddMoney.this, "Back Button Pressed", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        //PWEStaticDataModel.TXN_BACKPRESSED_CODE is a string constant and its value is “back_pressed”
                        //Code here will execute if the user pressed the back button on coupons Activity.
                        // here merchant can show the payment failed message.
                    } else if (result.contains(PWEStaticDataModel.TXN_USERCANCELLED_CODE)) {
                        Toast.makeText(AddMoney.this, "Transaction Cancelled By User", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        //PWEStaticDataModel.TXN_USERCANCELLED_CODE is a string constant and its value is “user_cancelled”
                        //Code here will execute if the the user pressed the cancel button during the payment process.
                        // here merchant can show the payment failed message.
                    } else if (result.contains(PWEStaticDataModel.TXN_ERROR_SERVER_ERROR_CODE)) {
                        Toast.makeText(AddMoney.this, "Server Error", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                    } else if (result.contains(PWEStaticDataModel.TXN_ERROR_TXN_NOT_ALLOWED_CODE)) {
                        Toast.makeText(AddMoney.this, "Transaction Not Allowed", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    } else if (result.contains(PWEStaticDataModel.TXN_BANK_BACK_PRESSED_CODE)) {
                        Toast.makeText(AddMoney.this, "BANK_BACK_PRESSED", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                    } else {
                        Toast.makeText(AddMoney.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                        transactionModel.setTransactionStatus("Failed");
                        intent.putExtra("amt",amt);
                        intent.putExtra("status",false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        // Here the value of result is “payment_failed” or “error_noretry” or “retry_fail_error”
                        //Code here will execute if payment is failed some other reasons.
                        // here merchant can show the payment failed message.
                    }
                    transactionsref.child(pushkey).setValue(transactionModel);

                } catch (Exception e) {
                    //Handle exceptions here
                }
            }
        }
    }


}
