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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceUserModel;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class PartnerProfile extends AppCompatActivity implements View.OnClickListener{
    ImageView profileImg,editProfile;
    TextView documentUpload,aboutme,username,share;
    String uid;
    DatabaseReference ref;
    ServiceUserModel model;
    String TAG="Profile";
    String phone;
    TextView Signout,tv_mail,tv_phone;

ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_profile);
try {
    uid = getIntent().getStringExtra("uid");
    Log.w(TAG, "UID recieved=" + uid);
    if (null == uid) {
        SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        uid = pref.getString("uid", "");
        phone=pref.getString("Phone","");

    }
    Log.w(TAG,"PartnerProfile=>"+uid);

    tv_mail=findViewById(R.id.tv_email);
    tv_phone=findViewById(R.id.tv_mobile);
    Signout = findViewById(R.id.signOut);
    progress = findViewById(R.id.progress);
    profileImg = findViewById(R.id.ProfilePicture);
    editProfile = findViewById(R.id.editProfile);
    documentUpload = findViewById(R.id.docUpload);
    username = findViewById(R.id.UserName);
    aboutme = findViewById(R.id.about);

    progress.setVisibility(View.VISIBLE);
    editProfile.setOnClickListener(this);
    documentUpload.setOnClickListener(this);
    aboutme.setOnClickListener(this);
    Signout.setOnClickListener(this);
    model = new ServiceUserModel();

    ref = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone);

    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.w(TAG, "DATASNAPSHOT=>" + dataSnapshot);
            if (dataSnapshot.exists()) {
                progress.setVisibility(View.GONE);
                model = dataSnapshot.getValue(ServiceUserModel.class);

                Picasso.get().load(model.getProfileimage()).into(profileImg);
                username.setText(model.getName());
                tv_mail.setText(model.getMail());
                tv_phone.setText(model.getPhn());
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}catch (Exception e){
    e.printStackTrace();
}

    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.editProfile:{
                Intent intent=new Intent(PartnerProfile.this,UserDetailsEdit.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
                break;
            }

            case R.id.docUpload:{
                Intent intent=new Intent(PartnerProfile.this,DocumentsUpdate.class);
                startActivity(intent);

                break;
            }
            case R.id.about:{
                break;
            }

            case R.id.signOut:{
                FirebaseAuth.getInstance().signOut();
                SharedPreferences pref=getSharedPreferences("PartnerPref",MODE_PRIVATE);
                SharedPreferences.Editor edit=pref.edit();
                edit.clear();
                Intent intent=new Intent(PartnerProfile.this,PhoneNumberActivity.class);
                startActivity(intent);
            }
        }

    }

    }
