package com.mimotic.tigre.views;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.tools.TigreFragment;


public class MapFragment extends TigreFragment implements OnMapReadyCallback {

    long minTime = 5 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
    long minDistance = 10; // Minimum distance change for update in meters, i.e. 10 meters.


    MapView mapView;

    GoogleMap googleMap;

    Location mCurrentLocation;

    PolylineOptions rectOptions = new PolylineOptions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_view, container, false);

        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        if(mapView!=null) {
            googleMap = mapView.getMap();

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            googleMap.setMyLocationEnabled(true);

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getUiSettings().setTiltGesturesEnabled(true);

            drawLine();
        }


        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(getProviderName(), minTime, minDistance, locationListener);
        return view;

    }

    private String getProviderName() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mapView.onDestroy();
        }catch(Exception e){

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap map) {
//        LatLng casa = new LatLng(40.4451658, -3.7009091);
//        map.addMarker(new MarkerOptions().position(casa).title("Casa"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(casa));
//
//        rectOptions
//                .add(new LatLng(37.35, -122.0))
//                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
//                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
//                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
//                .add(new LatLng(37.35, -122.0)); // Closes the polyline.
//
//// Get back the mutable Polyline
//        Polyline polyline = map.addPolyline(rectOptions);
    }


    private void drawLine(){
//        LatLng casa = new LatLng(40.4451658, -3.7009091);
//        googleMap.addMarker(new MarkerOptions().position(casa).title("Casa"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, 8));

//        rectOptions.add(casa);
//                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
//                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
//                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
//                .add(new LatLng(37.35, -122.0)); // Closes the polyline.

        rectOptions.color(Color.BLUE);
        rectOptions.width(2);
// Get back the mutable Polyline
        Polyline polyline = googleMap.addPolyline(rectOptions);

    }




    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            updateUI();
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



    private void updateUI() {
        LatLng punto = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        rectOptions.add(punto);
        googleMap.addPolyline(rectOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(punto));
    }


}
