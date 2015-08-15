package com.mimotic.tigre.common.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsConstants {

    public static final String APP_PREFERENCES = "appPreferences";
//    public static final String PREF_APP_NOTIF_PUSH = "pushNotif";


    public static final String GCM_PREFERENCES = "gcmPrefs";
    public static final String PREF_GCM_REG_ID = "registration_id";
    public static final String PREF_GCM_APP_VERSION = "appVersion";

    public static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(GCM_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void storeRegistrationId(Context context, int appVersion, String regId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                GCM_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_GCM_REG_ID, regId);
        editor.putInt(PREF_GCM_APP_VERSION, appVersion);
        editor.commit();
    }

}
