package com.mimotic.tigre.views.rutas;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mimotic.tigre.R;
import com.mimotic.tigre.tools.TigreFragment;

public class RutaTabsFragment extends TigreFragment{

    protected static final String TAG = "RutaTabsFragment";

    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.rutas_tabs, container, false);

        String title = getArguments().getString("title");
        int rutaId = getArguments().getInt("idruta");

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("idruta", rutaId);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("mapa").setIndicator("Mapa"),
                RutaViewFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec("detalle").setIndicator("Detalle"),
                RutaDetalleFragment.class, bundle);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

}
