package com.cedetel.android.tigre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cedetel.android.tigre.db.GeishaDB;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.Ruta;
import com.google.android.maps.Point;

public class MisPuntosGeisha extends ListActivity {
	
	private GeishaDB mDbHelper;
	private Long mRutaId;
	private String nombre;
	private String passwd;
    protected List<String> inombres = new ArrayList<String>();
	protected List<Point> ipuntos = new ArrayList<Point>();

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mis_puntos);
        loadConfig();
        
        mDbHelper = new GeishaDB(this);
        mDbHelper.open();
        
        if (icicle == null) {
			mRutaId = null;
		} else {
			mRutaId = icicle.getLong(GeishaDB.KEY_ROWID);
		}

		if (mRutaId == null) {
			Bundle extras = getIntent().getExtras();
			mRutaId = extras != null ? extras.getLong(GeishaDB.KEY_ROWID) : null;
		}
		
		Ruta route = com.cedetel.android.tigre.webservice.GeishaWS.getRoute(mRutaId.intValue(), nombre, passwd);

		try {
//			Ruta route = new Ruta();
//			route = (Ruta) r.get(0);
			
			double latitud,longitud;
		    String titulo;
		    	POI p = new POI();
		    	
		    	Iterator iter = route.getPois().iterator();
		    	
                while (iter.hasNext()){
		    	
		    	p =(POI) iter.next();
		    	
		    		latitud = p.getLatitud() ;
		    		
		    		longitud = p.getLongitud();
		    		
		    		titulo = p.getTitulo();
		    		
		    		if(titulo.equals(null)){
		    			titulo = "punto";
		    		}
		    		Point point = new Point((int) (latitud * 1000000),(int) (longitud * 1000000));
		    		ipuntos.add(point);
		    		inombres.add(titulo);
		    }
		        		 

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, inombres));
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {		
		super.onListItemClick(l, v, position, id);
		Point punto = ipuntos.get(position);
		int latitud = punto.getLatitudeE6();
		int longitud = punto.getLongitudeE6();
		String mnombre = inombres.get(position);
		Intent intent = new Intent();
		intent.putExtra("longitud", longitud);
		intent.putExtra("latitud", latitud);
		intent.putExtra("nombre", mnombre);
    	intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.dibujar.DibujarPunto");

    	startActivity(intent);
	}


	private final boolean loadConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);		
		nombre = configuracion.getString("usuario", null);
		passwd = configuracion.getString("passwd", null);
		return true;
 	}
}
