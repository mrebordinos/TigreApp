package com.mimotic.tigre.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.mimotic.tigre.R;
import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.common.settings.SettingsConstants;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.datasource.PuntosDataSource;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.views.MainActivity;

public class TrackerService extends Service {

    private Thread workerThread = null;

//    long minTime = 10 * 1000; // Minimum time interval for update in seconds, i.e. 10 seconds.
//    long minDistance = 10; // Minimum distance change for update in meters, i.e. 10 meters.

    int idRuta = -1;

    private NotificationManager mNM;
    private int NOTIFICATION = R.string.service_started;

    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LogTigre.i(getClass().getSimpleName(), "Creating service");

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(workerThread == null || !workerThread.isAlive()){
            workerThread = new Thread(new Runnable() {
                public void run() {
                    startRoute();
                }
            });
            workerThread.start();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void startRoute(){
        RutaDataSource rutaDataSource = new RutaDataSource(this);
        rutaDataSource.open();
        idRuta = rutaDataSource.getIdRutaInProgress();
        rutaDataSource.close();

        long minTime = SettingsConstants.getTiempo(this) * 1000;
        long minDistance = SettingsConstants.getDistancia(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Looper.prepare();
        locationManager.requestLocationUpdates(getProviderName(), minTime, minDistance, locationListener);
        Looper.loop();
    }




    private String getProviderName() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //location;
            if(idRuta>-1){
                GeoPunto mpunto = new GeoPunto();
                mpunto.setIdRuta(idRuta);
                mpunto.setLatitude(location.getLatitude());
                mpunto.setLongitude(location.getLongitude());
                mpunto.setAltitude(location.getAltitude());

                PuntosDataSource puntosDataSource = new PuntosDataSource(TrackerService.this);
                puntosDataSource.open();
                puntosDataSource.insertPoint(mpunto);
                puntosDataSource.close();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.huella_small, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_started),
                text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mNM.cancel(NOTIFICATION);
        locationManager.removeUpdates(locationListener);
        RutaDataSource rutaDataSource = new RutaDataSource(this);
        rutaDataSource.open();
        rutaDataSource.finishRuta(idRuta);
        rutaDataSource.close();
    }
}
