package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hotspot.hotspotserviceprovider.modelClasses.ProductsModel;
import com.squareup.picasso.Picasso;

public class ManageProducts extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton add;
    String TAG="Manage Product";
    String ProductName,ProductPrice,productDesc,productName;
    String phone;
    RecyclerView productsRecycler;
    LinearLayoutManager layoutManager;
    ProductsModel model;
    String uid;

    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        try {
            SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            uid=pref.getString("uid","");
            phone = pref.getString("Phone", "");
            if (phone.equals("")) {
                phone = "unknown";
            }
            model=new ProductsModel();

            productsRecycler=findViewById(R.id.productsRecycler);
             add = findViewById(R.id.floatingActionButton);
            add.setOnClickListener(this);

            layoutManager = new LinearLayoutManager(this);
            productsRecycler.setLayoutManager(layoutManager);

        fetchProducts();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public  void fetchProducts(){

        Query query= FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("sellerUid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w(TAG,"DAtasnapshot=>"+dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerOptions<ProductsModel> options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(query, ProductsModel.class)
                .build();
        Log.w(TAG, "query=>" + query);
        adapter = new FirebaseRecyclerAdapter<ProductsModel, ViewHolder>(options) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_cardview, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final ProductsModel model) {

                Log.w(TAG, "model=>"+model.getProductImage());
                holder.setProductDesc(model.getProductDescription());
                holder.setProductname(model.getProductName());
                holder.setProductPrice(model.getProductPrice());
                holder.setProduct_img(model.getProductImage());
                holder.deleteProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.w(TAG,"deleteProductClicked");
                        FirebaseDatabase.getInstance().getReference().child("Products").child(model.getProductPushkey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ManageProducts.this,"Product Removed",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG,"exceptin=>"+e);
                            }
                        });
                    }
                });
                holder.editProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

        };

        productsRecycler.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice,productDesc;
        ImageView product_img,deleteProducts,editProducts;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            product_img=itemView.findViewById(R.id.productImage);
            productDesc=itemView.findViewById(R.id.productDesc);
            deleteProducts=itemView.findViewById(R.id.deleteProducts);
            editProducts=itemView.findViewById(R.id.editProducts);

            Log.w(TAG, "viewHolderClass=>");


        }

        public void setProductname(String string) {
            productName.setText(string);
        }
        public void setProductDesc(String string){
            productDesc.setText(string);

        }

        public void setProductPrice(String price) {

            productPrice.setText(price);
        }


        public void setProduct_img(String img){
            Picasso.get().load(img).into(product_img);
        }

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floatingActionButton:{
                    Intent intent=new Intent(ManageProducts.this,AddProduct.class);
                    startActivity(intent);
                    break;
            }
        }
    }

}