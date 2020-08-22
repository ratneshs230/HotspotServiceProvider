package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class ReferralCode extends AppCompatActivity implements View.OnClickListener{
    TextView TvreferralCode;
    LinearLayout shareLayout;
    String TAG="Referral code";
    ImageView partner_share_referral,hotspot_share_referral;
    String phone,uid,referralCode,name;
    private Uri mInvitationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_code);
try {
    SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
    uid = pref.getString("uid", "");
    phone = pref.getString("Phone", "");
    referralCode=pref.getString("referralCode","");
    name=pref.getString("userName","");

    if(uid.equals("") || phone.equals("") || name.equals("") ){
        Intent intent=new Intent(ReferralCode.this,AllServices.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }




    hotspot_share_referral=findViewById(R.id.hotspot_share_referral);
    TvreferralCode = findViewById(R.id.referralCode);
    partner_share_referral = findViewById(R.id.partner_share_referral);

    hotspot_share_referral.setOnClickListener(this);
    partner_share_referral.setOnClickListener(this);

    FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            String refer="Referral Code : "+dataSnapshot.child("referralCode").getValue(String.class);
            TvreferralCode.setText(refer);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}catch (Exception e){
    e.printStackTrace();
}
    }


//    private void createLink() {
//
//        final String sharelink = "https://hotspotserviceprovider.page.link/?" +
//                "link=http://www.hotspotgroup.in/?"+referralCode +
//                "&apn=" + getPackageName() +
//                "&st=" + "Hotspot Partner" +
//                "&sd=" + "Earn Rs25 instantly"+
//                "&si=https://firebasestorage.googleapis.com/v0/b/hotspotproject-e39df.appspot.com/o/partner.png?alt=media&token=82c8c2b5-fd17-4cf6-a04f-b543946d1a44";
//
//
//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                // .setLink(Uri.parse("https://hotspotgroup.in/"))
//                .setLink(Uri.parse(sharelink))
//                .setDomainUriPrefix("https://hotspotserviceprovider.page.link")
//                // Open links with this app on Android
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                .buildDynamicLink();
//
//        Uri dynamicLinkUri = dynamicLink.getUri();
//
//        Log.w(TAG, "dynamicLink=>" + dynamicLinkUri);
//
//        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLongLink(Uri.parse(sharelink))
//                .buildShortDynamicLink()
//                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
//                    @Override
//                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
//                        if (task.isSuccessful()) {
//                            // Short link created
//                            Uri shortLink = task.getResult().getShortLink();
//                            Uri flowchartLink = task.getResult().getPreviewLink();
//
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_SEND);
//                            intent.putExtra(Intent.EXTRA_TEXT, shortLink + "");
//                            intent.setType("text/plain");
//                            startActivity(intent);
//
//                            Log.w(TAG, "PartnerProfile=>" + shortLink);
//                        } else {
//                                Log.w(TAG,"Referral Code=>"+task.getException());
//                            // Error
//                            // ...
//                        }
//                    }
//                });
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.partner_share_referral:{

                createLink();

                break;
            }
            case R.id.hotspot_share_referral:{
                createCustomerLink();
                break;
            }



        }
    }

    private void createCustomerLink() {

        final String sharelink = "https://hotspotCustomer.page.link/?" +
                "link=http://www.hotspotgroup.in/?" +
                "&apn=" + "com.hotspot.hotspot" +
                "&st=" + "Hotspot" +
                "&sd=" + "Earn Rs25 instantly"+
                "&si=https://firebasestorage.googleapis.com/v0/b/hotspotproject-e39df.appspot.com/o/USERpng.png?alt=media&token=70f50287-a395-4b36-acd9-426b5948bdef";
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                // .setLink(Uri.parse("https://hotspotgroup.in/"))
                .setLink(Uri.parse(sharelink))
                .setDomainUriPrefix("https://hotspotCustomer.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Log.w(TAG, "dynamicLink=>" + dynamicLinkUri);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelink))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink + "");
                            intent.setType("text/plain");
                            startActivity(intent);

                            Log.w(TAG, "PartnerProfile=>" + shortLink);
                        } else {
                            Log.w(TAG,"Referral Code=>"+task.getException());
                            // Error
                            // ...
                        }
                    }
                });
    }

    public void createLink() {
        // [START ddl_referral_create_link]

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name=user.getDisplayName();
        String uid = user.getUid();
        String link = "https://hotspotgroup.in/partner/?invitedby" + referralCode;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://hotspotserviceprovider.page.link/?")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.hotspot.hotspotserviceprovider")
                                .setMinimumVersion(125)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        Log.w(TAG,"SHORTDYNAMICLINk=>"+shortDynamicLink);
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        String inv=mInvitationUrl.toString();

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,mInvitationUrl+"");
                            intent.setType("text/plain");
                            startActivity(intent);

                        Log.w(TAG,"InvitationLink=>"+mInvitationUrl);
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"ERROR=>"+e.getMessage());
            }
        });
        // [END ddl_referral_create_link]
    }
}