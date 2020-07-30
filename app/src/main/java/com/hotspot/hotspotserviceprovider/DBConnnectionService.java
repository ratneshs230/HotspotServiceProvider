package com.hotspot.hotspotserviceprovider;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

public class DBConnnectionService extends Service {

    String uid;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        
        checkDatabaseUpdate();
        return null;
    }

    private void checkDatabaseUpdate() {
        FirebaseDatabase.getInstance().getReference().child("Drive").child(uid);
    }


}
