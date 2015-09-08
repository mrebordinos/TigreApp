package com.mimotic.tigre.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mimotic.tigre.R;
import com.mimotic.tigre.common.settings.SettingsConstants;
import com.mimotic.tigre.tools.TigreFragment;

public class ConfigFragment extends TigreFragment{

    protected static final String TAG = "ConfigFragment";

    int colorSelected = 0;
    int grosorSelected = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.config_view, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.configuracion);

//        final String[] colores = getResources().getStringArray(R.array.array_colores);
//        final String[] grosores = getResources().getStringArray(R.array.array_grosor);

        final EditText inputTime = (EditText)rootView.findViewById(R.id.input_time);
        final EditText inputDistance = (EditText)rootView.findViewById(R.id.input_distance);
        inputTime.setText(SettingsConstants.getTiempo(getActivity()) + "");
        inputDistance.setText(SettingsConstants.getDistancia(getActivity()) + "");


        Spinner spinnerColor = (Spinner)rootView.findViewById(R.id.combo_color);
        spinnerColor.setSelection(SettingsConstants.getColor(getActivity()));
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinnerGrosor = (Spinner)rootView.findViewById(R.id.combo_grosor);
        spinnerGrosor.setSelection(SettingsConstants.getGrosor(getActivity()));
        spinnerGrosor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grosorSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        Button btnSave = (Button)rootView.findViewById(R.id.btn_save_config);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tiempo = Integer.parseInt(inputTime.getText().toString());
                int distancia = Integer.parseInt(inputDistance.getText().toString());

                SettingsConstants.setConfig(getActivity(), tiempo, distancia, colorSelected, grosorSelected);

                Toast.makeText(getActivity(), "Configuración guardada", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }





}
