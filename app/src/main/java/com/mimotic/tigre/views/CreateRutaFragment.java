package com.mimotic.tigre.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mimotic.tigre.R;
import com.mimotic.tigre.common.settings.SettingsConstants;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.service.TrackerService;
import com.mimotic.tigre.tools.TigreFragment;

public class CreateRutaFragment extends TigreFragment {

    protected static final String TAG = "CreateRutaFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.create_ruta_view, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);


        final EditText inputRuta = (EditText)rootView.findViewById(R.id.input_title_ruta);
        final EditText inputDescrip = (EditText)rootView.findViewById(R.id.input_descrip_ruta);
        final EditText inputTags = (EditText)rootView.findViewById(R.id.input_tags_ruta);

        Button btnInitRuta = (Button)rootView.findViewById(R.id.btn_init);
        btnInitRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleRuta = inputRuta.getText().toString().trim();
                String descripRuta = inputDescrip.getText().toString().trim();
                String tagsRuta = inputTags.getText().toString().trim();

                Ruta ruta = new Ruta();
                ruta.setTitle(titleRuta);
                ruta.setDescription(descripRuta);
                ruta.setTags(tagsRuta);
                ruta.setState(1);
                ruta.setTimestampStart(System.currentTimeMillis());

                RutaDataSource rutaDataSource = new RutaDataSource(getActivity());
                rutaDataSource.open();
                int idRuta = rutaDataSource.createRuta(ruta);
                rutaDataSource.close();

                SettingsConstants.setIsRouting(getActivity(), true);
                SettingsConstants.setIdRuta(getActivity(), idRuta);

                Intent intent = new Intent(getActivity(), TrackerService.class);
                getActivity().startService(intent);

                callbackNavigation.loadFragment(new HomeFragment(), false);
            }
        });


        return rootView;
    }
}
