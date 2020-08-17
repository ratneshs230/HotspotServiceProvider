package com.hotspot.hotspotserviceprovider;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private String verificationid;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;
    private Button verify;
    private TextView resend;
    private String phonenumber;
    DatabaseReference ref;
    String TAG="Otp Activity",uid,referrerKey;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

try {

    mAuth = FirebaseAuth.getInstance();

    progressBar = findViewById(R.id.progressbar);
    editText = findViewById(R.id.otp);
    verify = findViewById(R.id.verify);
    resend = findViewById(R.id.resend);

    phonenumber = getIntent().getStringExtra("phonenumber");
    if(getIntent().getStringExtra("referrerKey")!=null)
        referrerKey=getIntent().getStringExtra("referrerKey");

    sendVerificationCode(phonenumber);

    verify.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            String code = editText.getText().toString().trim();

            if ((code.isEmpty() || code.length() < 6)) {
                progressBar.setVisibility(View.GONE);
                editText.setError("Enter code...");
                editText.requestFocus();
                return;
            } else {

                verifyCode(code);
            }

        }
    });
    resend.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(OtpActivity.this, "OTP has been Resend", Toast.LENGTH_LONG).show();
            sendVerificationCode(phonenumber);
        }
    });
}catch (Exception e){
    e.printStackTrace();
}
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                ref = FirebaseDatabase.getInstance().getReference().child("Partner");

                                firebaseUser = mAuth.getCurrentUser();

                                SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putBoolean("Login_State", true);
                                uid = firebaseUser.getUid();
                                Log.w(TAG, "UID" + uid);
                                editor.putString("uid", uid);
                                editor.putString("Phone", phonenumber);
                                editor.apply();
                                Intent intent;
                                boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                if (!isNew ) {
                                    intent = new Intent(OtpActivity.this, AllServices.class);

                                } else {
                                    intent = new Intent(OtpActivity.this, UserDetailsEdit.class);
                                    intent.putExtra("From","otp");

                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("no", phonenumber);
                                if(referrerKey!=null){
                                    intent.putExtra("reffererKey",referrerKey);
                                }
                                intent.putExtra("uid", uid);
                                startActivity(intent);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    });
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }


    };
}
