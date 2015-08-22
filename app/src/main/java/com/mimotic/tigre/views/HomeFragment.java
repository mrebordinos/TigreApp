package com.mimotic.tigre.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
        boolean isRoutin = SettingsConstants.getIsRouting(getActivity());

        if(isRoutin){
            btnInitRuta.setVisibility(View.GONE);
            btnFinishRuta.setVisibility(View.VISIBLE);
        }else{
            btnInitRuta.setVisibility(View.VISIBLE);
            btnFinishRuta.setVisibility(View.GONE);
        }

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
//        LatLng casa = new LatLng(37.45, -122.0);
//        googleMap.addMarker(new MarkerOptions().position(casa).title("Inicio"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casa, 10));
//
//
//        PolylineOptions rectOptions = new PolylineOptions();
//        rectOptions//add(casa);
//                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
//                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
//                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
//                .add(new LatLng(37.35, -122.0)); // Closes the polyline.
//
//        rectOptions.color(Color.BLUE);
//        rectOptions.width(2);
//        // Get back the mutable Polyline
//        Polyline polyline = googleMap.addPolyline(rectOptions);


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
//                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_fin));
                googleMap.addMarker(markerOptions.position(mpoint).title("Fin"));
            }
        }

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
            SettingsConstants.setIsRouting(getActivity(), false);
            SettingsConstants.setIdRuta(getActivity(), 0);
            getActivity().stopService(new Intent(getActivity(), TrackerService.class));
            setButtons();
        }else if(v.getId()==R.id.take_photo){
//            dispatchTakePictureIntent();
            ((MainActivity)getActivity()).dispatchTakePictureIntent();
        }
    }


//    static final int REQUEST_IMAGE_CAPTURE = 123;

//    private void dispatchTakePictureIntent() {
////        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
////            getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////        }
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                LogTigre.e("Photo", "Error guardando imagen", ex);
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }
//
//
//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "IMG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//        File tigreFolder = new File(storageDir + "/tigrapp");
//        if(!tigreFolder.exists()){
//            tigreFolder.mkdir();
//        }
//
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                tigreFolder      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }


}
