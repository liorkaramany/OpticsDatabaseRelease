package com.example.liorkaramany.opticsdatabase;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Credits extends AppCompatActivity {

    ConnectionReceiver connectionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        connectionReceiver = new ConnectionReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(connectionReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(connectionReceiver);
    }

    public void back(View view) {
        finish();
    }
}
