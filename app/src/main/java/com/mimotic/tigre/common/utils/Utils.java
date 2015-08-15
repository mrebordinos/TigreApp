package com.mimotic.tigre.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mimotic.tigre.common.LogTigre;

public class Utils {

    protected static final String TAG = "Utils";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            LogTigre.i(TAG, "Hay conectividad con internet");
            return true;
        }
        return false;
    }


    public static boolean isWifiAvailable(Context context){
        ConnectivityManager connManager1 = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi1 = connManager1
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi1.isAvailable();
    }
}
