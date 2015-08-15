package com.mimotic.tigre.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mimotic.tigre.R;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.tools.TigreFragment;
import com.mimotic.tigre.views.adapter.RutaAdapter;

import java.util.ArrayList;
import java.util.List;

public class RutasListFragment extends TigreFragment{

    protected static final String TAG = "RutasListFragment";

    private List<Ruta> rutas = new ArrayList<>();

    private RutaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.rutas_list_view, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis rutas");

        ListView rutasList = (ListView)rootView.findViewById(R.id.listview);
        rutasList.setOnItemClickListener(listadoListener);

        getRutas();

        adapter = new RutaAdapter(getActivity(), R.layout.row_ruta, rutas);
        rutasList.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(rutas.size()==0){
            getRutas();

            adapter = new RutaAdapter(getActivity(), R.layout.row_ruta, rutas);
            adapter.notifyDataSetChanged();
        }
    }

    private void getRutas(){
        RutaDataSource rutaDataSource = new RutaDataSource(getActivity());
        rutaDataSource.open();
        rutas = rutaDataSource.getAllRutas();
        rutaDataSource.close();
    }


    private AdapterView.OnItemClickListener listadoListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Ruta mruta = rutas.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("title", mruta.getTitle());
            bundle.putInt("idruta", mruta.getId());
            Fragment fragment = new RutaViewFragment();
            fragment.setArguments(bundle);
            callbackNavigation.loadFragment(fragment);
        }
    };




}
