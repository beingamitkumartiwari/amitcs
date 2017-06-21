package com.amtee.camscanner.utilities;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by DEVEN SINGH on 2/28/2015.
 */
public class ConnectionCheck {
    Context context;

    public ConnectionCheck(Context context)
    {
        this.context = context;
    }



    public boolean isConnectionAvailable()
    {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         if ((localConnectivityManager.getActiveNetworkInfo() == null) || (!localConnectivityManager.getActiveNetworkInfo().isAvailable()) || (!localConnectivityManager.getActiveNetworkInfo().isConnected()))
            return false;
        else
        return true;
    }
}
