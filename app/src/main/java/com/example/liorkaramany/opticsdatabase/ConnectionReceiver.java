package com.example.liorkaramany.opticsdatabase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * @author Lior Karamany
 * @version 1.0
 * @since 1.0
 *
 * This class defines a BroadcastReceiver that listens to the connectivity of the application, and shows a dialog if the connection is lost.
 */
public class ConnectionReceiver extends BroadcastReceiver {

    /**
     * A flag which tells if the dialog was already displayed in the activity.
     */
    private boolean hasDisplayed;

    /**
     * A constructor of the ConnectionReceiver class.
     *
     * This function creates a ConnectionReceiver and sets the hasDisplayed flag to false.
     *
     * @return A reference to the created ConnectionReceiver.
     */
    public ConnectionReceiver()
    {
        hasDisplayed = false;
    }

    /**
     * Shows a dialog with an appropriate error message when connection to the internet is lost.
     *
     * @param context the context in which the dialog will be shown.
     * @param intent the intent that is received from the context.
     * @return the view which the ListView displays.
     */
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
