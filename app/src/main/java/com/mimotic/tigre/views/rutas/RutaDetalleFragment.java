package com.mimotic.tigre.views.rutas;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mimotic.tigre.R;
import com.mimotic.tigre.common.utils.Utils;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.datasource.PuntosDataSource;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.tools.TigreFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RutaDetalleFragment extends TigreFragment{

    protected static final String TAG = "RutaDetalleFragment";

    private int rutaId = 0;
    private Ruta mRuta;

    private ArrayList<GeoPunto> puntos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setHasOptionsMenu(true);

//        String title = getArguments().getString("title");
        rutaId = getArguments().getInt("idruta");

        View rootView = inflater.inflate(R.layout.ruta_detail_view, container, false);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        getRutaData();

        TextView titulo = (TextView)rootView.findViewById(R.id.titulo);
        TextView descripcion = (TextView)rootView.findViewById(R.id.descripcion);
        TextView tags = (TextView)rootView.findViewById(R.id.tags);
        TextView fecha = (TextView)rootView.findViewById(R.id.fecha);
        TextView duracion = (TextView)rootView.findViewById(R.id.duracion);
        TextView distancia = (TextView)rootView.findViewById(R.id.distancia);
        TextView velocidad = (TextView)rootView.findViewById(R.id.velocidad);

        titulo.setText(mRuta.getTitle());
        descripcion.setText(mRuta.getDescription());
        tags.setText(mRuta.getTags());
        fecha.setText(Utils.timeMillisOutputFormat(mRuta.getTimestampStart()) + " - " +
                        Utils.timeMillisOutputFormat(mRuta.getTimestampEnd()));

        long duration = mRuta.getTimestampEnd() - mRuta.getTimestampStart();
        duracion.setText(Utils.differenceBetweenDates(duration));

        float distance = calculateDistance();
        distancia.setText(distance + " metros");

        long seconds = duration / 1000;
        float speed = distance / seconds;
        speed = (float) (speed * 3.6);

        velocidad.setText(Utils.round(speed,2) + " km/h");

        return rootView;
    }



    private void getRutaData(){
        RutaDataSource rutaDataSource = new RutaDataSource(getActivity());
        rutaDataSource.open();
        mRuta = rutaDataSource.getRutaById(rutaId);
        rutaDataSource.close();

        getPoints();
    }


    private void getPoints(){
        PuntosDataSource puntosDataSource = new PuntosDataSource(getActivity());
        puntosDataSource.open();
        puntos = puntosDataSource.getPuntosByRutaId(rutaId);
        puntosDataSource.close();
    }


    private float calculateDistance(){

        float distance = 0;

        Location locationOld = new Location("");
        Location locationNew = new Location("");
        for(int i=0; i<puntos.size(); i++) {

            if(i==0){
                locationOld.setLatitude(puntos.get(i).getLatitude());
                locationOld.setLongitude(puntos.get(i).getLongitude());
            }else{
                locationNew.setLatitude(puntos.get(i).getLatitude());
                locationNew.setLongitude(puntos.get(i).getLongitude());

                distance += locationNew.distanceTo(locationOld);

                locationOld = locationNew;
            }

        }

        distance = Utils.round(distance, 2);

        return distance;
    }


}
