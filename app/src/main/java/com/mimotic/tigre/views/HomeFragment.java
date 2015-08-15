package com.mimotic.tigre.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.service.TrackerService;
import com.mimotic.tigre.tools.TigreFragment;

public class HomeFragment extends TigreFragment implements View.OnClickListener{

    protected static final String TAG = "HomeFragment";


    MapView mapView;

    GoogleMap googleMap;


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

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getUiSettings().setTiltGesturesEnabled(true);

            drawLine();
        }


        Button btnInitRuta = (Button)rootView.findViewById(R.id.new_ruta);
        Button btnFinishRuta = (Button)rootView.findViewById(R.id.finish_ruta);
        btnInitRuta.setOnClickListener(this);
        btnFinishRuta.setOnClickListener(this);

        return rootView;
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


    private void drawLine(){
        LatLng casa = new LatLng(37.45, -122.0);
        googleMap.addMarker(new MarkerOptions().position(casa).title("Inicio"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, 10));


        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions//add(casa);
                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
                .add(new LatLng(37.35, -122.0)); // Closes the polyline.

        rectOptions.color(Color.BLUE);
        rectOptions.width(2);
        // Get back the mutable Polyline
        Polyline polyline = googleMap.addPolyline(rectOptions);

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.new_ruta){
            callbackNavigation.loadFragment(new CreateRutaFragment());
        }else if(v.getId()==R.id.finish_ruta){
            getActivity().stopService(new Intent(getActivity(), TrackerService.class));
        }
    }
}
