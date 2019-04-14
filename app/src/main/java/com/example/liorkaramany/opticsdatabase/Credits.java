package com.example.liorkaramany.opticsdatabase;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines an activity which shows the credits for the application.
 */
public class Credits extends AppCompatActivity {

    /**
     * The ConnectionReceiver listener that listens to the connectivity of the application.
     */
    ConnectionReceiver connectionReceiver;

    /**
     * Initializes the activity and the connectionReceiver.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        connectionReceiver = new ConnectionReceiver();
    }

    /**
     * Registers the receiver when the application resumes the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(connectionReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Unregisters the receiver when the application stops the activity.
     */
    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(connectionReceiver);
    }

    /**
     * Goes back to the previous activity.
     */
    public void back(View view) {
        finish();
    }
}
