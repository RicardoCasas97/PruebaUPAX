package com.example.pruebaupax.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

public class InternetConectado extends BroadcastReceiver {

    String TAG ="BroadcastInternetTag";


    @Override
    public void onReceive(final Context context, final Intent intent) {
        isNetworkAvailable(context);

    }


    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_CELLULAR");
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_WIFI");
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_ETHERNET");
                    return true;
                }
            }
        }

        return false;

    }
}