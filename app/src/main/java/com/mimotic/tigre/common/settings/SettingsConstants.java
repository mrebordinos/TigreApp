package com.mimotic.tigre.common.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsConstants {

    public static final String APP_PREFERENCES = "appPreferences";
    public static final String PREF_APP_IS_ROUTING = "isRoutin";
    public static final String PREF_APP_ID_RUTA = "idRuta";

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



    public static boolean getIsRouting(Context context){
        SharedPreferences myPrefs = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        boolean isRoutin = myPrefs.getBoolean(PREF_APP_IS_ROUTING, false);
        return isRoutin;
    }

    public static void setIsRouting(Context context, boolean isRoutin){
        SharedPreferences myPrefs = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(PREF_APP_IS_ROUTING, isRoutin);
        prefsEditor.commit();
    }


    public static int getIdRuta(Context context){
        SharedPreferences myPrefs = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        int idRuta = myPrefs.getInt(PREF_APP_ID_RUTA, 0);
        return idRuta;
    }

    public static void setIdRuta(Context context, int idRuta){
        SharedPreferences myPrefs = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putInt(PREF_APP_ID_RUTA, idRuta);
        prefsEditor.commit();
    }


}
