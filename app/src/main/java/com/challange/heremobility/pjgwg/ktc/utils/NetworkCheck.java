package com.challange.heremobility.pjgwg.ktc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by pranjul on 24/2/18.
 */

public class NetworkCheck {

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null )
            return false;
        else{
            if (info.isConnected())
                return true;
            else
                return true;
        }
    }
}
