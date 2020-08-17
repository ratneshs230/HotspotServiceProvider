package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.LinkAddress;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hotspot.hotspotserviceprovider.modelClasses.ShopDetailModel;
import com.hotspot.hotspotserviceprovider.modelClasses.bankModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class   SendToBank extends AppCompatActivity implements View.OnClickListener {

    RecyclerView bankRecycler;
    LinearLayoutManager linearLayoutManager;
    TextView bankAdd;
FirebaseRecyclerAdapter adapter;
String phone;
View bankdetailLayout;
String TAG="SendToBank";
    EditText bankHolderName, bankBranchName, bankIfsc, bankAcNo;
    Button uploadBank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_bank);

        SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        phone = pref.getString("Phone", "");

        bankRecycler=findViewById(R.id.bankRecycler);
        bankAdd=findViewById(R.id.bankAdd);
        bankdetailLayout=findViewById(R.id.bankdetailLayout);
        uploadBank=findViewById(R.id.uploadBank);
        bankAcNo = findViewById(R.id.bankAcNo);
        bankBranchName = findViewById(R.id.bankBranchName);
        bankHolderName = findViewById(R.id.bankHolderName);
        bankIfsc = findViewById(R.id.bankIfsc);


        bankAdd.setOnClickListener(this);
        uploadBank.setOnClickListener(this);

        linearLayoutManager=new LinearLayoutManager(this);
        bankRecycler.setLayoutManager(linearLayoutManager);

        fetchBankDetails();

    }

    private void fetchBankDetails() {
        try {
            Query query = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("BankDetails");
            FirebaseRecyclerOptions<bankModel> options = new FirebaseRecyclerOptions.Builder<bankModel>()
                    .setQuery(query, bankModel.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<bankModel, ViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull bankModel model) {

                    holder.setaccountHolder(model.getAccHolder());
                    holder.setbankIfsc(model.getAccIFSC());
                    holder.setaccountNumber(model.getAccNumber());
                    holder.setBranchName(model.getAccBranch());

                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        Intent intent=new Intent(SendToBank.this,RequestMoneyActivity.class);
                        startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_cardview, parent, false);
                    return new ViewHolder(view);
                }
            };
            bankRecycler.setAdapter(adapter);
            adapter.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView accountNumber,bankIfsc,accountHolder,mAddress,branchName;
        ConstraintLayout root;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            accountNumber=itemView.findViewById(R.id.accountNumber);
            bankIfsc=itemView.findViewById(R.id.bankIfsc);
            accountHolder=itemView.findViewById(R.id.accountHolder);
            root= itemView.findViewById(R.id.root);
            branchName=itemView.findViewById(R.id.branchName);
            Log.w(TAG, "viewHolderClass=>");


        }
 public ConstraintLayout getRoot() {
            return root;
        }

        public void setRoot(ConstraintLayout root) {
            this.root = root;
        }
        public void setVerification(String string){
            branchName.setText(string);


        }
        public void setaccountNumber(String string) {
            accountNumber.setText(string);
        }
        public void setbankIfsc(String string){
            bankIfsc.setText(string);
        }
        public void setaccountHolder(String string) {
            accountHolder.setText(string);
        }
        public void setBranchName(String string){
            branchName.setText(string);

                }

    }

    private void storeBankDB() {
        String name = bankHolderName.getText().toString().trim();
        String ifsc = bankIfsc.getText().toString().trim();
        String branch = bankBranchName.getText().toString().trim();
        String account = bankAcNo.getText().toString().trim();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Documents").child("BankDetails");
        String pushkey=ref.push().getKey();

        Map<String, Object> bankdetails = new HashMap<>();
        bankdetails.put("AccHolder", name);
        bankdetails.put("AccIFSC", ifsc);
        bankdetails.put("AccBranch", branch);
        bankdetails.put("AccNumber", account);

        ref.child(pushkey).updateChildren(bankdetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SendToBank.this, "Added Bank Account", Toast.LENGTH_SHORT).show();
                bankdetailLayout.setVisibility(View.INVISIBLE);



            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bankAdd:{

                bankdetailLayout.setVisibility(View.VISIBLE);

                break;
            }
            case R.id.uploadBank:{
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
                    storeBankDB();
                }
                break;
            }
        }
    }
}