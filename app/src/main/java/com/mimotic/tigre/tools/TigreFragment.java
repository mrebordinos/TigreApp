package com.mimotic.tigre.tools;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.mimotic.tigre.R;
import com.mimotic.tigre.analytics.AnalyticsTracks;
import com.mimotic.tigre.common.LogTigre;

import org.json.JSONObject;

public class TigreFragment extends Fragment {

    protected static final String TAG = "RobotsFragment";

    protected AnalyticsTracks analytics;

    protected TigreCallback callbackNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        analytics = new AnalyticsTracks(getActivity());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callbackNavigation = (TigreCallback) activity;
        } catch (ClassCastException cce) {
            LogTigre.w(TAG, "El fragment actual se ha adjuntado a un Activity que no implementa la interface RobotsCallback");
            throw cce;
        }

    }



    protected void sendTrazaScreen(String traza, JSONObject variables){
        analytics.sendScreenToAnalytics(traza);
    }


    protected void sendTrazaEvent(String traza, JSONObject variables){
        analytics.sendEventToAnalytics(traza);
    }


    protected void alerta(String textoAlerta) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_un_boton);

        TextView contenido = (TextView) dialog.findViewById(R.id.contenido);
        contenido.setText(textoAlerta);

        Button dialogButton = (Button) dialog.findViewById(R.id.boton_ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


}
