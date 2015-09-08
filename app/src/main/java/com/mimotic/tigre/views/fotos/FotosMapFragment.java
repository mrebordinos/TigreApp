package com.mimotic.tigre.views.fotos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.IPoint;
import com.mimotic.tigre.model.db.datasource.PhotosDataSource;
import com.mimotic.tigre.tools.TigreFragment;

import java.util.ArrayList;

public class FotosMapFragment extends TigreFragment {

    MapView mapView;
    GoogleMap googleMap;

    private ArrayList<LatLng> fotosCoords = new ArrayList<>();
    private ArrayList<Foto> fotos = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.map_view, container, false);

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

        getFotos();
        drawPoints();
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


    private void getFotos(){
        PhotosDataSource photosDataSource = new PhotosDataSource(getActivity());
        photosDataSource.open();
        fotosCoords = photosDataSource.getAllPhotosCoords();
        fotos = photosDataSource.getAllPhotos();
        photosDataSource.close();
    }


    private void drawPoints(){

        for(int i=0; i<fotosCoords.size(); i++){
                MarkerOptions markerOptions = new MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                markerOptions.snippet(i+"");
                googleMap.addMarker(markerOptions.position(fotosCoords.get(i)).title("foto " + i));
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                Foto mfoto = fotos.get(Integer.parseInt(marker.getSnippet()));

                Intent fotoIntent = new Intent(getActivity(), TouchImageActivity.class);
                fotoIntent.putExtra("imageURL", mfoto.getUrl());
                startActivity(fotoIntent);

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
