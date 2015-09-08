package com.mimotic.tigre.views.rutas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.common.settings.SettingsConstants;
import com.mimotic.tigre.common.utils.Utils;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.IPoint;
import com.mimotic.tigre.model.db.datasource.IPointsDataSource;
import com.mimotic.tigre.model.db.datasource.PhotosDataSource;
import com.mimotic.tigre.model.db.datasource.PuntosDataSource;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.tools.TigreFragment;
import com.mimotic.tigre.views.fotos.TouchImageActivity;

import java.util.ArrayList;


public class RutaViewFragment extends TigreFragment implements OnMapReadyCallback {

    int rutaId = 0;

    MapView mapView;
    GoogleMap googleMap;

    private ArrayList<GeoPunto> puntos = new ArrayList<>();

    private ArrayList<IPoint> pois = new ArrayList<>();
    private ArrayList<Foto> fotos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

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
        drawPois();
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

        IPointsDataSource ipoisDataSource = new IPointsDataSource(getActivity());
        ipoisDataSource.open();
        pois = ipoisDataSource.getAllIPointByRuta(rutaId);
        ipoisDataSource.close();

        PhotosDataSource fotosDataSource = new PhotosDataSource(getActivity());
        fotosDataSource.open();
        fotos = fotosDataSource.getAllPhotosByRuta(rutaId);
        fotosDataSource.close();
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
                markerOptions.snippet("");
                googleMap.addMarker(markerOptions.position(mpoint).title("Inicio"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mpoint, 15));
            }

            if(i==puntos.size()-1){
                MarkerOptions markerOptions = new MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//                        BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_fin));
                markerOptions.snippet("");
                googleMap.addMarker(markerOptions.position(mpoint).title("Fin"));
            }
        }


        rectOptions.color(Utils.getColor(SettingsConstants.getColor(getActivity())));
        rectOptions.width(Utils.getGrosor(SettingsConstants.getGrosor(getActivity())));
        // Get back the mutable Polyline
        Polyline polyline = googleMap.addPolyline(rectOptions);

    }



    private void drawPois(){

        for(int i=0; i<pois.size(); i++){
            LatLng mpoint = new LatLng(pois.get(i).getLatitude(), pois.get(i).getLongitude());

            MarkerOptions markerOptions = new MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerOptions.snippet("");
            googleMap.addMarker(markerOptions.position(mpoint).title(pois.get(i).getTexto()));
        }


        for(int i=0; i<fotos.size(); i++){
            LatLng mpoint = new LatLng(fotos.get(i).getLatitude(), fotos.get(i).getLongitude());

            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_camara_white));
            markerOptions.snippet(i + "");
            googleMap.addMarker(markerOptions.position(mpoint));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker marker) {

                try {
                    if(!marker.getSnippet().equals("")) {
                        Foto mfoto = fotos.get(Integer.parseInt(marker.getSnippet()));

                        Intent fotoIntent = new Intent(getActivity(), TouchImageActivity.class);
                        fotoIntent.putExtra("imageURL", mfoto.getUrl());
                        startActivity(fotoIntent);
                    }
                }catch (Exception e){}


                return true;
            }

        });

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


}
