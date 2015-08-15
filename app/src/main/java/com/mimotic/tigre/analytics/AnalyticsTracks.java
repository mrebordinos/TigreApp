package com.mimotic.tigre.analytics;

import android.content.Context;

import com.google.android.gms.analytics.Tracker;
import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.tools.TigreApp;

public class AnalyticsTracks {

    protected static final String TAG = "AnalyticsTracks";

    private Context context;

    public AnalyticsTracks(Context context) {
        this.context = context;
    }


    public void sendScreenToAnalytics(String screen){
        try{
            Tracker tracker = TigreApp.getTrackerUA();

            tracker.setScreenName(screen);

//            tracker.send(trackerSetVariablesScreen(true));

            LogTigre.d(TAG, "Enviar trazas a analytics: Screen - " + screen);
        }catch(Exception e){
            LogTigre.e(TAG, "sendScreenToAnalytics", e);
        }
    }



    public void sendEventToAnalytics(String label){
        try{
            Tracker tracker = TigreApp.getTrackerUA();

//            tracker.send(trackerSetVariablesEvent(true, label));

            LogTigre.d(TAG, "Enviar trazas a analytics: Event - " + label);
        }catch(Exception e){
            LogTigre.e(TAG, "sendEventToAnalytics", e);
        }
    }
}
