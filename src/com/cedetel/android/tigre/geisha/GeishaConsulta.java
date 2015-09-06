package com.cedetel.android.tigre.geisha;

import java.lang.reflect.Array;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.geisha.functiona.ServicioEnRegiones;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.RegionBaseInfo;

// CONSULTA DE INFORMACION GEOGRAFICA DE GEISHA

public class GeishaConsulta extends Activity {

	private TextView modoZona;
	private TextView coordenadas;
	private Spinner mSpinner5;
	private Spinner mSpinner6;
	private Spinner mSpinner7;
	Array mArray;

	private String seleccion;

	String nombreZona = null;
	private String zona;
	private int idzona;
	private String servicios;
	private int idservicios;
	private int distancia;
	
	int[] idServicios;
	private int itemPosition = 0;
	private int oldItemPosition = 0;
	private int idZona = 1;
	private int a = 0;
	RegionBaseInfo[] aRBI = null;
	String[] aZonasName = null;
	Vector zonas=new Vector();
	private String[] aDistancia = { "3 km.", "1 km.", "500 metros", "100 metros" };
	boolean first = true;
	GeishaConsulta miObjeto = null;

	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);

	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		miObjeto = this;
		loadUserConfig();
		setContentView(R.layout.consulta_info_geo);

		modoZona = (TextView) findViewById(R.id.modo);
		modoZona.setText(seleccion);
		
		coordenadas = (TextView)findViewById(R.id.coordenadas);
		
		
		

		if (seleccion.equals("Autodetectar")) {
			 LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			 Location myLocation = lm.getCurrentLocation("gps");

			Double lat = myLocation.getLatitude();
			Double lng = myLocation.getLongitude();			
				
//			 double lat = 41.6;
//			 double lng = -4.7;
			
			
			Vector<POI> nearestZone = com.cedetel.android.tigre.webservice.GeishaWS
							.searchNearestPoi(lat, lng, "cedetel", "cedetel", 1);
			int aNearestZone_id = nearestZone.get(0).getId_region();
			coordenadas.setText("Latitud: " + lat + "\n" + "Longitud: " + lng);
			/*zonas = com.cedetel.android.tigre.webservice.GeishaWS.getZones(0);
			String[] aZonas = new String[zonas.size()];

			for (int i = 0; i < zonas.size(); i++) {
				aZonas[i] = ((RegionBaseInfo) zonas.get(i)).getNombre_region();
				if (((RegionBaseInfo) zonas.get(i)).getId_region() == Integer.valueOf(aNearestZone_id)) {
					idZona = ((RegionBaseInfo) zonas.get(i)).getId_region();
				}
			}*/
			
			//Obtenemos un vector con todas las zonas
			zonas = com.cedetel.android.tigre.webservice.GeishaWS.getZones(0);
			//Declaramos la longitud de los vectores que corresponderá al número de regiones esxistentes
			aRBI = new RegionBaseInfo[zonas.size()];
			aZonasName = new String[zonas.size()];
			int a=0;
			//Rellenamos los vectores con los resultados obtenidos
			for (int i = 0; i < zonas.size(); i++) {
				//Vector de objetos RegionBaseInfo (que contendrán toda la información)
				aRBI[i] = (RegionBaseInfo) zonas.get(i);
				//Vector que contiene el nombre de las regiones
				aZonasName[i] = ((RegionBaseInfo) zonas.get(i)).getNombre_region();
				//Si coinciden seleccionamos la region
				if (((RegionBaseInfo) zonas.get(i)).getId_region() == Integer.valueOf(aNearestZone_id)) {
					idZona = ((RegionBaseInfo) zonas.get(i)).getId_region();
					a=i;
				}
			}
			

			mSpinner5 = (Spinner) findViewById(R.id.spinner5);

			ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, aZonasName);
			adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			mSpinner5.setAdapter(adapter5);
			mSpinner5.setSelection( a    /*idZona - 1*/);
			mSpinner5.setOnItemSelectedListener(selectZona);

		} else if (seleccion.equals("Manual")) {
			coordenadas.setVisibility(View.GONE);
			//Obtenemos un vector con todas las zonas
			zonas = com.cedetel.android.tigre.webservice.GeishaWS.getZones(0);
			//Declaramos la longitud de los vectores que corresponderá al número de regiones esxistentes
			aRBI = new RegionBaseInfo[zonas.size()];
			aZonasName = new String[zonas.size()];
			
			//Rellenamos los vectores con los resultados obtenidos
			for (int i = 0; i < zonas.size(); i++) {
				//Vector de objetos RegionBaseInfo (que contendrán toda la información)
				aRBI[i] = (RegionBaseInfo) zonas.get(i);
				//Vector que contiene el nombre de las regiones
				aZonasName[i] = ((RegionBaseInfo) zonas.get(i)).getNombre_region();
				//Si coinciden seleccionamos la region
				if (((RegionBaseInfo) zonas.get(i)).getNombre_region().equals(nombreZona)) {
					idZona = ((RegionBaseInfo) zonas.get(i)).getId_region();
				}
			}
			
			mSpinner5 = (Spinner) findViewById(R.id.spinner5);
			ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, aZonasName);
			adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			mSpinner5.setAdapter(adapter5);
			mSpinner5.setOnItemSelectedListener(selectZona);
		}

		mSpinner6 = (Spinner) findViewById(R.id.spinner6);

		mSpinner7 = (Spinner) findViewById(R.id.spinner7);
		ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, aDistancia);
		adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner7.setAdapter(adapter7);
		mSpinner7.setOnItemSelectedListener(selectDistancia);

		Button botonConsulta = (Button) findViewById(R.id.botonConsulta);
		botonConsulta.setOnClickListener(pulsarMostrar);
	}

	
	private final boolean loadUserConfig() {
		SharedPreferences config = getSharedPreferences("RedirectData", 0);
		seleccion = config.getString("seleccion", null); // Configuracion: manual o autodetectar
		return true;
	}
	
	private OnClickListener pulsarMostrar = new OnClickListener() {
		public void onClick(View view) {
			SharedPreferences miConsult = getSharedPreferences("RedirectData",
					0);
			SharedPreferences.Editor editor = miConsult.edit();
			editor.putString("zona", zona);
			editor.putInt("idzona", idzona);
			editor.putString("servicios", servicios);
			editor.putInt("idservicio", idservicios);
			editor.putInt("distancia", distancia);
			if (editor.commit()) {
				setResult(RESULT_OK);
			}
			Intent i = new Intent();
			i.setClassName("com.cedetel.android.tigre",
					"com.cedetel.android.tigre.geisha.GeishaListaConsulta");
			startActivity(i);
			finish();
		}
	};

	private OnItemSelectedListener selectZona = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {
			
			position = mSpinner5.getSelectedItemPosition();
			//Cogemos el identificador de la zona que se ha selccionado 
			int id_zona_selected = aRBI[position].getId_region();
			//Cargamos los servicios correspondientes a esa zona
			Vector<ServicioEnRegiones> servicios = com.cedetel.android.tigre.webservice.GeishaWS
						.getAvailableServicesByZone(id_zona_selected, null, null);
			//Listamos los servicios devueltos
			String[] aServicios = new String[servicios.size()];
			idServicios = new int[servicios.size()];
			for (int i=0; i<servicios.size(); i++){
				ServicioEnRegiones ser = servicios.get(i);
				aServicios[i] = ser.getNombre_servicio();
				idServicios[i] = ser.getIdServicio();
			}
			ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(miObjeto,android.R.layout.simple_spinner_item, aServicios);
			adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner6.setAdapter(adapter6);
			mSpinner6.setOnItemSelectedListener(selectServicios);

			itemPosition = mSpinner5.getSelectedItemPosition();
			TextView a = (TextView) mSpinner5.getChildAt(0);
			SpinnerAdapter bh = mSpinner5.getAdapter();
			bh.getItem(0);

			nombreZona = mSpinner5.getSelectedItem().toString();
			
			zona = mSpinner5.getSelectedItem().toString();
			idzona = id_zona_selected;
			
		}

		public void onNothingSelected(AdapterView arg0) {
			idzona = 0;
		}

	};
	
	
	private OnItemSelectedListener selectServicios = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {
			position = mSpinner6.getSelectedItemPosition();
			
			servicios = mSpinner6.getSelectedItem().toString();		
			idservicios = idServicios[position];
			
		}

		public void onNothingSelected(AdapterView arg0) {
			idservicios = 0;
		}
	};

	
	private OnItemSelectedListener selectDistancia = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {
			position = mSpinner7.getSelectedItemPosition();			
			switch(position){
			case 0:
				distancia = 3000; 
				break;
			case 1:	
				distancia = 1000; 				
				break;
			case 2:
				distancia = 500; 				
				break;
			case 3:
				distancia = 100; 				
				break;
			}
		}

		public void onNothingSelected(AdapterView arg0) {
			distancia = 1000;
		}
	};

	  @Override
		 public boolean onKeyDown(int keyCode, KeyEvent event)
		 {
			 switch (keyCode)
			 {
			 	case KeyEvent.KEYCODE_BACK:
			 		Intent intent = new Intent();
			 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
			 		startActivity(intent);
			 		finish();
			 		break;
			 }
			 return false;
		 }

}
