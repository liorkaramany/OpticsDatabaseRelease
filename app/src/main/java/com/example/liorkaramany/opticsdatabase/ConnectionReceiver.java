package com.example.liorkaramany.opticsdatabase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {

    private boolean hasDisplayed;

    public ConnectionReceiver()
    {
        hasDisplayed = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected && !hasDisplayed)
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(context);

            adb.setTitle(context.getString(R.string.error));
            adb.setMessage(context.getString(R.string.no_connection));
            adb.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog ad = adb.create();
            ad.show();

            hasDisplayed = true;
        }
    }
}
