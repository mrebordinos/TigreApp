package com.mimotic.tigre.views.fotos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mimotic.tigre.R;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.db.datasource.PhotosDataSource;
import com.mimotic.tigre.tools.TigreFragment;

import java.util.ArrayList;

public class FotosMapFragment extends TigreFragment {

    MapView mapView;
    GoogleMap googleMap;

    private ArrayList<LatLng> fotosCoords = new ArrayList<>();


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
        photosDataSource.close();
    }


    private void drawPoints(){

        for(int i=0; i<fotosCoords.size(); i++){
                MarkerOptions markerOptions = new MarkerOptions().icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                googleMap.addMarker(markerOptions.position(fotosCoords.get(i)).title("foto " + i));

        }

    }

}
