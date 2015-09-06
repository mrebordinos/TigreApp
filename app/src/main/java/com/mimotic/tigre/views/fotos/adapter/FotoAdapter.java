package com.mimotic.tigre.views.fotos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mimotic.tigre.R;
import com.mimotic.tigre.common.utils.Utils;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.Ruta;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FotoAdapter extends ArrayAdapter<Foto> {

    private List<Foto> fotos = new ArrayList<>();
    private Context context;

    public FotoAdapter(Context context, int resource, List<Foto> objects) {
        super(context, resource, objects);
        this.context = context;
        this.fotos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_foto, null);
        }

        final Foto mfoto = fotos.get(position);

        TextView titulo = (TextView) v.findViewById(R.id.title);
        ImageView imagen = (ImageView) v.findViewById(R.id.thumb);

        titulo.setText(Utils.timeMillisOutputFormat(mfoto.getTimestamp()));

        Picasso.with(context).load(mfoto.getUrl()).resize(80, 80).centerCrop().into(imagen);
//        subtitulo.setText(mruta.);

        return v;
    }


}
