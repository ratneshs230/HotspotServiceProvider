package com.hotspot.hotspotserviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class RequestStatement extends AppCompatActivity {
    DatabaseReference transactionsref;
    String uid;
    RecyclerView statementRecycler;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<TransactionModel, ViewHolder> adapter;
    String TAG="RequestStatement";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_statement);

        SharedPreferences userPref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
        uid = userPref.getString("uid", "");

        transactionsref= FirebaseDatabase.getInstance().getReference().child("Transactions").child(uid);
        statementRecycler=findViewById(R.id.statementRecycler);

        layoutManager=new LinearLayoutManager(this);
        statementRecycler.setLayoutManager(layoutManager);
        statementRecycler.setHasFixedSize(true);

        getRecyclerData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void getRecyclerData() {
        Query query=FirebaseDatabase.getInstance().getReference().child("Transactions").child(uid);
        Log.w(TAG,"Reference=>"+transactionsref);
        Log.w(TAG,"Query=>"+query);
        FirebaseRecyclerOptions<TransactionModel> options=new FirebaseRecyclerOptions.Builder<TransactionModel>()
                .setQuery(query,TransactionModel.class)
                .build();

       adapter= new FirebaseRecyclerAdapter<TransactionModel, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_cardview,parent,false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull TransactionModel model) {

                holder.setCarddamount(model.getTransactionAmount());

                holder.setTime(model.getTransactionTime());
                holder.setTransactionName(model.getTransactionStatus());


            }
        };
        statementRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout root;
        public TextView dateCard, time,transactionName,closingBalance,Carddamount;
        public ImageView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            root=itemView.findViewById(R.id.root);
            dateCard=itemView.findViewById(R.id.dateCard);
            time=itemView.findViewById(R.id.Cardtime);

            closingBalance=itemView.findViewById(R.id.closingBalance);
            cardImage=itemView.findViewById(R.id.cardImage);
            transactionName=itemView.findViewById(R.id.transactionName);
            Carddamount=itemView.findViewById(R.id.Carddamount);

        }

        public ConstraintLayout getRoot() {
            return root;
        }

        public void setRoot(ConstraintLayout root) {
            this.root = root;
        }


        public void setTime(String string){
            time.setText(string);

        }
        public void setDateCard(String string){
            dateCard.setText(string);
        }
        public void setTransactionName(String string){
            transactionName.setText(string);
        }
        public void setClosingBalance(String string){
            closingBalance.setText(string);
        }
        public void setCarddamount(String string){
            Carddamount.setText(string);
        }
        public void setImg(String string){
                Picasso.get().load(string).into(cardImage);

        }
    }
}