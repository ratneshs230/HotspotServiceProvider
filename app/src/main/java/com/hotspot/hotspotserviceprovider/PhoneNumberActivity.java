package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class PhoneNumberActivity extends AppCompatActivity  implements View.OnClickListener{
    EditText phn;
        Button next_login;
        String TAG="PhoneNumber";
        TextView forgotPass;
        CheckBox checkBox1;
    String referrerkey;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

try {
    phn = findViewById(R.id.phn);
    next_login = findViewById(R.id.login_next);

    checkBox1=findViewById(R.id.checkBox1);

    deeplinkReceive();

    next_login.setOnClickListener(this);
}catch (Exception e){
    e.printStackTrace();
}
    }




    @Override
    public void onClick(View view) {
            if( view.getId()==R.id.login_next){

                if(checkBox1.isChecked()){

                    String number = phn.getText().toString().trim();

                    if (number.length() != 10 ) {
                        phn.setError("Valid number is required");
                        phn.requestFocus();
                        return;
                    }

                    String phonenumber = "+" + "91" + number;
                    Log.w(TAG,phonenumber);
                    Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);

                    intent.putExtra("phonenumber", phonenumber);
                    intent.putExtra("referrerKey", referrerkey);

                    startActivity(intent);

                }
                else Toast.makeText(getApplicationContext(),"Agree to terms and conditions",Toast.LENGTH_SHORT).show();
            }
    }

    //handle deeplink

    public void deeplinkReceive(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.w(TAG, "Deeplink=>" + deepLink);
                            String[] referrer = deepLink.toString().split("invitedby");
                            if(referrer!=null){
                                referrerkey=referrer[1].trim();
                                Log.w(TAG, "referrer=>" + referrer+"=>key=>"+referrerkey);

                            }



                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...
                        }
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });


    }

    private void signinwithMail(String mailId, String pass) {


        mAuth.signInWithEmailAndPassword(mailId,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"Login Successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid=user.getUid();
                    Intent intent=new Intent(PhoneNumberActivity.this,AllServices.class);
                    intent.putExtra("uid",uid);
                    startActivity(intent);
//                    login_btn.setEnabled(false);
                }
                else{

                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(PhoneNumberActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();                }
            }
        });

    }

}