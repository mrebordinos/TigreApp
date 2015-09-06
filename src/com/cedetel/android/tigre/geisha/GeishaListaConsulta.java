package com.cedetel.android.tigre.geisha;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.GeishaDB;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.RutaBaseInfo;

// LISTA DE LOS RESULTADOS DE LA BUSQUEDA

public class GeishaListaConsulta extends Activity{
	private int idzona;
	private int idservicios;
	private int distancia;
	private String nombre;
	private String passwd;
	private TextView titleRutas;
	private TextView titlePuntos;
	
	private ArrayList<String> rutasGeisha = new ArrayList<String>(); 
	private ArrayList<String> puntosGeisha = new ArrayList<String>(); 
	private ArrayList<Double> puntosLat = new ArrayList<Double>(); 
	private ArrayList<Double> puntosLng = new ArrayList<Double>(); 
	
	private ListView resultadoRutas;
	private ListView resultadoPuntos;
	
	private GeishaDB mDbGhelper;
	 
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mi_consulta);
        loadConsulta();    
        
        resultadoRutas = (ListView) findViewById(R.id.rutaslist);
        resultadoPuntos = (ListView) findViewById(R.id.puntoslist);
                
        titleRutas = (TextView) findViewById(R.id.resultadoRutas);
        titlePuntos = (TextView) findViewById(R.id.resultadoPuntos);
        titleRutas.setText("Rutas encontradas");
        titlePuntos.setText("Puntos encontrados");

        mDbGhelper = new GeishaDB(this);
        mDbGhelper.open();
        mDbGhelper.deleteAllRoutes();
		 
        Vector<RutaBaseInfo> aRutas = com.cedetel.android.tigre.webservice.GeishaWS
        								.searchRoutes(idservicios, idzona, "", "", "", "", nombre, "");
        
        for (int i=0; i<aRutas.size(); i++){		
        	mDbGhelper.createRoute(aRutas.get(i).getId_ruta(), aRutas.get(i).getNombre_ruta());
        }
			
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location myLocation = lm.getCurrentLocation("gps");
//		  Double lat = myLocation.getLatitude();
//		  Double lng = myLocation.getLongitude();	
//        Vector<POI> puntos = com.cedetel.android.tigre.webservice.GeishaWS
//        								.searchNearPois(lat.floatValue(), lng.floatValue(), distancia, nombre, passwd, idservicios);
        
        Vector<POI> puntos = com.cedetel.android.tigre.webservice.GeishaWS
        								.searchPois(idservicios, idzona, nombre, passwd, "", "", "", "");
		
		String[] aPuntos = new String[puntos.size()];
			
		for (int j=0; j<puntos.size(); j++)
		{
			puntosGeisha.add(puntos.get(j).getTitulo());
			puntosLat.add(puntos.get(j).getLatitud());
			puntosLng.add(puntos.get(j).getLongitud());
		}

//      ListAdapter listAdapter1 = new ArrayAdapter<String>(this,R.layout.elemento, rutasGeisha); 
//      resultadoRutas.setAdapter(listAdapter1); 
		resultadoRutas.setOnItemClickListener(clickRutas);
		
        ListAdapter listAdapter2 = new ArrayAdapter<String>(this,R.layout.elemento, puntosGeisha); 
        resultadoPuntos.setAdapter(listAdapter2); 
        resultadoPuntos.setOnItemClickListener(clickPuntos);
        
        fillData();
	}
	
	private void fillData() {
        Cursor cursor = mDbGhelper.fetchAllRoutes();
        startManagingCursor(cursor);

        String[] from = new String[]{GeishaDB.KEY_TITLE};
        int[] to = new int[]{R.id.text1};
        
        SimpleCursorAdapter routes = new SimpleCursorAdapter(this, R.layout.elemento, cursor, from, to);
        resultadoRutas.setAdapter(routes);
    }
	
	
	private OnItemClickListener clickRutas = new OnItemClickListener(){
		public void onItemClick(AdapterView arg0, View arg1, int position, long id) {
			Intent intent = new Intent();
			intent.putExtra(GeishaDB.KEY_ROWID, id);
			intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.dibujar.DibujarGeishaRuta");
			startActivity(intent);
		}	
	};
	
	
	private OnItemClickListener clickPuntos = new OnItemClickListener(){

		public void onItemClick(AdapterView arg0, View arg1, int position, long id) {
			Intent intent = new Intent();
			intent.putExtra("longitud", puntosLng.get(position));
			intent.putExtra("latitud", puntosLat.get(position));
			intent.putExtra("nombre", puntosGeisha.get(position));
			intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.dibujar.DibujarPunto");
			startActivity(intent);
		}		
	};
	
	
	private final boolean loadConsulta(){
        SharedPreferences consulta = getSharedPreferences("RedirectData", 0);
        nombre = consulta.getString("usuario", "Acceso Anónimo");
        passwd = consulta.getString("passwd", null);
		idzona = consulta.getInt("idzona", 0);
		idservicios = consulta.getInt("idservicios", 0);
		distancia = consulta.getInt("distancia", 1000);
		return true;
	}	
	
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	 {
		 switch (keyCode)
		 {
		 	case KeyEvent.KEYCODE_BACK:
		 		Intent intent = new Intent();
		 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.geisha.GeishaConsulta");
		 		startActivity(intent);
		 		finish();
		 		break;
		 }
		 return false;
	 }
	 
	 
}
