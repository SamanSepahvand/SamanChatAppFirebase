package com.samansepahvand.samanchapapp.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.samansepahvand.samanchapapp.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FCM";
    String newToken="null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getToken();

    }

    private String getToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            Log.e(TAG, newToken);
        });

        return newToken;
    }

}