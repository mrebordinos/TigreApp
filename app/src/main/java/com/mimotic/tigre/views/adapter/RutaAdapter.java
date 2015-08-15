package com.mimotic.tigre.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mimotic.tigre.R;
import com.mimotic.tigre.model.Ruta;

import java.util.ArrayList;
import java.util.List;

public class RutaAdapter extends ArrayAdapter<Ruta> {

    private List<Ruta> rutas = new ArrayList<>();
    private Context context;

    public RutaAdapter(Context context, int resource, List<Ruta> objects) {
        super(context, resource, objects);
        this.context = context;
        this.rutas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_ruta, null);
        }

        final Ruta mruta = rutas.get(position);

        TextView titulo = (TextView) v.findViewById(R.id.title);
        TextView subtitulo = (TextView) v.findViewById(R.id.subtitle);

        titulo.setText(mruta.getTitle());
//        subtitulo.setText(mruta.);

        return v;
    }


}
