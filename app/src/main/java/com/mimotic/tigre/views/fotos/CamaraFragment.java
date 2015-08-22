package com.mimotic.tigre.views.fotos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mimotic.tigre.R;
import com.mimotic.tigre.tools.TigreFragment;

public class CamaraFragment extends TigreFragment{

    protected static final String TAG = "CamaraFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.default_view, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Camara");

        return rootView;
    }





}
