package com.mimotic.tigre.common;

import android.util.Log;

public class LogTigre {

    // TODO Poner a ERROR para produccion
    private static final int logLevel=Log.DEBUG;


    public static final void d(String tag, String message){
        if(Log.DEBUG >= logLevel){
            Log.d(tag, message);
        }
    }

    public static final void d(String tag, String message, Throwable thr){
        if(Log.DEBUG >= logLevel){
            Log.d(tag, message, thr);
        }
    }


    public static final void i(String tag, String message){
        if(Log.INFO >= logLevel){
            Log.i(tag, message);
        }
    }


    public static final void w(String tag, String message){
        if(Log.WARN >= logLevel){
            Log.w(tag, message);
        }
    }


    public static final void e(String tag, String message){
        if(Log.ERROR >= logLevel){
            try{
                Log.e(tag, message);
            }catch(NullPointerException e){
            }
        }
    }

    public static final void e(String tag, String message, Throwable thr){
        if(Log.ERROR >= logLevel){
            try{
                Log.e(tag, message, thr);
            }catch(NullPointerException e){
            }
        }
    }

}
