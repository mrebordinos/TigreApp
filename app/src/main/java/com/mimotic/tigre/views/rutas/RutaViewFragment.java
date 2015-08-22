package com.mimotic.tigre.views.rutas;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.db.datasource.PuntosDataSource;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.tools.TigreFragment;

import java.util.ArrayList;


public class RutaViewFragment extends TigreFragment implements OnMapReadyCallback {

    int rutaId = 0;

    MapView mapView;
    GoogleMap googleMap;

    private ArrayList<GeoPunto> puntos = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_view, container, false);

        String title = getArguments().getString("title");
        rutaId = getArguments().getInt("idruta");

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        if(mapView!=null) {
            googleMap = mapView.getMap();

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            googleMap.setMyLocationEnabled(true);

            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getUiSettings().setTiltGesturesEnabled(true);

        }

        return view;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

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


    private void getRuta(){
        PuntosDataSource puntosDataSource = new PuntosDataSource(getActivity());
        puntosDataSource.open();
        puntos = puntosDataSource.getPuntosByRutaId(rutaId);
        puntosDataSource.close();
    }



    @Override
    public void onMapReady(GoogleMap map) {
    }


    private void drawLine(){
        PolylineOptions rectOptions = new PolylineOptions();

        for(int i=0; i<puntos.size(); i++){
            LatLng mpoint = new LatLng(puntos.get(i).getLatitude(), puntos.get(i).getLongitude());
            rectOptions.add(mpoint);

            if(i==0){
                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_house));
                googleMap.addMarker(markerOptions.position(mpoint).title("Inicio"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mpoint, 15));
            }

            if(i==puntos.size()-1){
                MarkerOptions markerOptions = new MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//                        BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_fin));
                googleMap.addMarker(markerOptions.position(mpoint).title("Fin"));
            }
        }

        rectOptions.color(Color.BLUE);
        rectOptions.width(2);
        // Get back the mutable Polyline
        Polyline polyline = googleMap.addPolyline(rectOptions);

    }


}
