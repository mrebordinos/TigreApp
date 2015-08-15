package com.mimotic.tigre.tools;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class TigreApp extends Application {

    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    private static Context context;

    public void onCreate(){
        super.onCreate();
        TigreApp.context = getApplicationContext();

        analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker("UA-XXXXXXXX-X");

    }

    public static Context getAppContext() {
        return TigreApp.context;
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }

    public static GoogleAnalytics analytics() {
        return analytics;
    }

    public static Tracker getTrackerUA() {
        return tracker;
    }


}
