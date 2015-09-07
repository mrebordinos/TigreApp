package com.mimotic.tigre.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.common.settings.SettingsConstants;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.db.datasource.PuntosDataSource;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.service.TrackerService;
import com.mimotic.tigre.tools.TigreFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends TigreFragment implements View.OnClickListener{

    protected static final String TAG = "HomeFragment";

    MapView mapView;

    GoogleMap googleMap;

    private ArrayList<GeoPunto> puntos = new ArrayList<>();

    private LinearLayout btnInitRuta;
    private LinearLayout btnFinishRuta;

    private Location mCurrentLocation;

    boolean isRoutin = false;

    PolylineOptions rectOptions = new PolylineOptions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.home_view, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        mapView = (MapView) rootView.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        if(mapView!=null) {
            googleMap = mapView.getMap();

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            googleMap.setMyLocationEnabled(true);

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getUiSettings().setTiltGesturesEnabled(true);

//            drawLine();
        }

        btnInitRuta = (LinearLayout)rootView.findViewById(R.id.new_ruta);
        btnFinishRuta = (LinearLayout)rootView.findViewById(R.id.finish_ruta);
        LinearLayout btnTakePhoto = (LinearLayout)rootView.findViewById(R.id.take_photo);
        btnInitRuta.setOnClickListener(this);
        btnFinishRuta.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

        setButtons();

        getRuta();
        drawLine();
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


    private void setButtons(){
        isRoutin = SettingsConstants.getIsRouting(getActivity());

        if(isRoutin){
            btnInitRuta.setVisibility(View.GONE);
            btnFinishRuta.setVisibility(View.VISIBLE);

            setLocationListenerUpdates();
        }else{
            btnInitRuta.setVisibility(View.VISIBLE);
            btnFinishRuta.setVisibility(View.GONE);
        }

    }



    private void setLocationListenerUpdates(){
        long minTime = 10 * 1000;
        long minDistance = 10;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(getProviderName(), minTime, minDistance, locationListener);

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


    private void getRuta(){
        RutaDataSource rutaDataSource = new RutaDataSource(getActivity());
        rutaDataSource.open();
        int rutaId = rutaDataSource.getLastRuta();
        rutaDataSource.close();

        if(rutaId>0) {
            PuntosDataSource puntosDataSource = new PuntosDataSource(getActivity());
            puntosDataSource.open();
            puntos = puntosDataSource.getPuntosByRutaId(rutaId);
            puntosDataSource.close();
        }
    }



    private void drawLine(){

//        PolylineOptions rectOptions = new PolylineOptions();

        for(int i=0; i<puntos.size(); i++){
            LatLng mpoint = new LatLng(puntos.get(i).getLatitude(), puntos.get(i).getLongitude());
            rectOptions.add(mpoint);

            if(i==0){
                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_house));
                googleMap.addMarker(markerOptions.position(mpoint).title("Inicio"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mpoint, 15));
            }

            if(!isRoutin) {
                if (i == puntos.size() - 1) {
                    MarkerOptions markerOptions = new MarkerOptions().icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    googleMap.addMarker(markerOptions.position(mpoint).title("Fin"));
                }
            }
        }

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





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mapa, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_vista_mapa) {
            if(googleMap.getMapType()!=GoogleMap.MAP_TYPE_SATELLITE) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }else{
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
            return true;
        }else{
            return (super.onOptionsItemSelected(item));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.new_ruta){
            callbackNavigation.loadFragment(new CreateRutaFragment());
        }else if(v.getId()==R.id.finish_ruta){
            SettingsConstants.setIsRouting(getActivity(), false);
            SettingsConstants.setIdRuta(getActivity(), 0);
            getActivity().stopService(new Intent(getActivity(), TrackerService.class));
            setButtons();
        }else if(v.getId()==R.id.take_photo){
//            dispatchTakePictureIntent();
            ((MainActivity)getActivity()).dispatchTakePictureIntent();
        }else if(v.getId()==R.id.fab){
            addInfoPoint();
        }
    }



    private void addInfoPoint(){

    }





}
