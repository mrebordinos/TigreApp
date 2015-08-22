package com.mimotic.tigre.views.fotos;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mimotic.tigre.R;
import com.mimotic.tigre.tools.TigreFragment;
import com.mimotic.tigre.views.MapFragment;
import com.mimotic.tigre.views.rutas.RutaDetalleFragment;
import com.mimotic.tigre.views.rutas.RutaViewFragment;

public class FotosTabsFragment extends TigreFragment{

    protected static final String TAG = "FotosTabsFragment";

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.rutas_tabs, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis fotos");

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("lista").setIndicator("Lista"),
                FotosListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("mapa").setIndicator("Mapa"),
                MapFragment.class, null);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

}
