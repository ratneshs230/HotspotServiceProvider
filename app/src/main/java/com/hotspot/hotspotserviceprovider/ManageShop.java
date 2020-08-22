package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotspot.hotspotserviceprovider.modelClasses.DoctorModelClass;
import com.hotspot.hotspotserviceprovider.modelClasses.ProductsModel;
import com.hotspot.hotspotserviceprovider.modelClasses.ShopDetailModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class ManageShop extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton addShop;
    RecyclerView shopRecycler,serviceRecycler;
    LinearLayoutManager linearLayoutManager;
    String phone;
    String TAG="ManageShop";
    ProgressBar imageProgress;
    FirebaseRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;
    ShopDetailModel shopDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shop);
        try {
            SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);


            phone = pref.getString("Phone", "");
            shopRecycler = findViewById(R.id.shopRecycler);
            addShop = findViewById(R.id.floatingActionButton);
            serviceRecycler=findViewById(R.id.serviceRecycler);
            shopDetailModel = new ShopDetailModel();
            fetch();
            fetchServices();
            addShop.setOnClickListener(this);
            linearLayoutManager=new LinearLayoutManager(this);
            layoutManager = new LinearLayoutManager(this);
            shopRecycler.setLayoutManager(layoutManager);
            serviceRecycler.setLayoutManager(linearLayoutManager);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fetchServices() {
        try {
            Query query = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Services");
            FirebaseRecyclerOptions<DoctorModelClass> options = new FirebaseRecyclerOptions.Builder<DoctorModelClass>()
                    .setQuery(query, DoctorModelClass.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<DoctorModelClass, ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DoctorModelClass model) {

                holder.setmProviderName(model.getDoctorName());
                holder.setmServiceName(model.getServicType());
                holder.setmServiceImage(model.getDoctorProfilePic());

                }

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_cardview, parent, false);
                    return new ViewHolder(view);
                }
            };
            serviceRecycler.setAdapter(adapter);
            adapter.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ManageShop.this,AllServices.class);
        startActivity(intent);
    }

    private void fetch() {
        try {
            Query query = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("ShopDetails");
            FirebaseRecyclerOptions<ShopDetailModel> options = new FirebaseRecyclerOptions.Builder<ShopDetailModel>()
                    .setQuery(query, ShopDetailModel.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<ShopDetailModel, ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShopDetailModel model) {

                    holder.setVerification(model.getVerificationStatus());
                    holder.setmShopName(model.getShopName());
                    holder.setmOwnerName(model.getOwnerName());
                    holder.setmCategory(model.getShopCategory());
                    holder.setmImageViewShop(model.getShopImage());

                }

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_cardview, parent, false);
                    return new ViewHolder(view);
                }
            };
            shopRecycler.setAdapter(adapter);
            adapter.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mShopName,mOwnerName,mCategory,mAddress,verification,mServiceName,mProviderName;
            ImageView mImageViewShop,mServiceImage;

            public ViewHolder(@NonNull View itemView) {

                super(itemView);
                mProviderName=itemView.findViewById(R.id.tv_provider_name);
                mServiceName= itemView.findViewById(R.id.tv_service_name);
                mServiceImage=itemView.findViewById(R.id.iv_service);

                mShopName=itemView.findViewById(R.id.tv_shop_name);
                mOwnerName=itemView.findViewById(R.id.tv_owner_name);
                mCategory=itemView.findViewById(R.id.tv_category);
//                mAddress=itemView.findViewById(R.id.tv_address);
                mImageViewShop=itemView.findViewById(R.id.iv_shop);
                verification=itemView.findViewById(R.id.verification);
                Log.w(TAG, "viewHolderClass=>");


            }
            public void setVerification(Boolean status){

                if(status){
                    verification.setText("Verified");
                    verification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_checked,0);
                }else {
                    verification.setText("Pending Verification");
                    verification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.outline_report_black_18dp,0);
                }
            }

            public void  setmServiceName(String string) {
                mServiceName.setText(string);
            }
            public void  setmProviderName(String string) {
                mProviderName.setText(string);
            }
            public void setmShopName(String string) {
                mShopName.setText(string);
            }
            public void setmOwnerName(String string){
                mOwnerName.setText(string);
            }
            public void setmCategory(String string) { mCategory.setText(string);
            }
//            public void setmAddress(String string){
//                mAddress.setText(string);
//            }
            public void setmServiceImage(String img){
                Picasso.get().load(img).into(mServiceImage);
            }
            public void setmImageViewShop(String img){
                Picasso.get().load(img).into(mImageViewShop);
            }

        }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floatingActionButton:{
                showChoiceDialog();
                break;
            }

        }

    }

    private void showChoiceDialog() {
        String[] items = {"Add a Shop", "Add a Service"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Select App");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                   Intent intent=new Intent(ManageShop.this,AddShop.class);
                   startActivity(intent);
                }
                if (i == 1) {
                    Intent intent=new Intent(ManageShop.this,AddService.class);
                    startActivity(intent);
                }
            }
        });
        dialog.create().show();
    }
}