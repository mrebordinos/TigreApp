package com.mimotic.tigre.common.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mimotic.tigre.common.LogTigre;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

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


    public static String timeMillisOutputFormat(long milliseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
        Date resultdate = new Date(milliseconds);
        return sdf.format(resultdate);
    }


    public static String differenceBetweenDates(long diff){
        String duration = "";
//        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
//        long days = hours / 24;

        while(seconds > 60){
            seconds = seconds - 60;
        }

        while(minutes > 60){
            minutes = minutes - 60;
        }

        duration = hours + " horas, " + minutes + " minutos, " + seconds + " segundos";

        return duration;
    }


    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    public static String getProviderName(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

}
