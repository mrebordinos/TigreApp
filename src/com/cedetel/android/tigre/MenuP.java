package com.cedetel.android.tigre;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//		 		MENU PRINCIPAL DE TIGRE

public class MenuP extends ListActivity{
	private String servicio;
	public String[] myMenu = new String[6];
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        loadConfig();
        if (servicio.equals("si")) {
        	myMenu[0] = "Ir a Ruta";
        	myMenu[1] = "Mis Rutas";
        	myMenu[2] = "Camara";
        	myMenu[3] = "Ir a Geisha";
        	myMenu[4] = "Opciones";
        	myMenu[5] = "Salir";
        }else{
        	myMenu[0] = "Nueva Ruta";
        	myMenu[1] = "Mis Rutas";
        	myMenu[2] = "Camara";
        	myMenu[3] = "Ir a Geisha";
        	myMenu[4] = "Opciones";
        	myMenu[5] = "Salir";
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myMenu));

	
    }
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		getListView().obtainItemId(position);
		if (position == 0){	
			loadConfig();
			if (servicio.equals("si")) {
				Intent intent1 = new Intent();
				intent1.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MapaRutaActual");
				startActivity(intent1);
        		//Toast.makeText(this, R.string.yaHayRuta, Toast.LENGTH_LONG).show();
				finish();
			}else {
				Intent intent1 = new Intent();
				intent1.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.NuevaRuta");
				startActivity(intent1);
				finish();
			}
		}else if (position == 1) {
			Intent intent2 = new Intent();
        	intent2.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MisRutas");
        	startActivity(intent2);
        	finish();
		}else if (position == 2) {
			Intent intent3 = new Intent();
			intent3.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MenuCamara");
			startActivity(intent3);
			finish();
		}else if (position == 3){
			Intent intent4 = new Intent();
			intent4.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.geisha.GeishaMenu");
        	startActivity(intent4);
        	finish();
		}else if (position == 4){
			Intent intent5 = new Intent();
			intent5.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.OpcionesTigre");
        	startActivity(intent5);
        	finish();
		}else {
			this.finish();
		}
        
	}

	private final boolean loadConfig() {
		SharedPreferences config = getSharedPreferences("RedirectData", 0);
		servicio = config.getString("status", "no");
		return true;
	}
	
}