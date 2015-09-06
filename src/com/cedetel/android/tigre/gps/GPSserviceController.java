package com.cedetel.android.tigre.gps;

import java.util.Vector;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.PuntoDB;
import com.cedetel.android.tigre.geisha.gis.Agrupacion;
import com.cedetel.android.tigre.geisha.gis.AgrupacionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.Ruta;
import com.cedetel.android.tigre.geisha.gis.RutaBaseInfo;
import com.cedetel.android.tigre.geisha.gis.VerticeRuta;

// 	A�ADE PUNTOS DE INTERES Y FINALIZA LA RUTA CUANDO YA HEMOS SALIDO DEL MAPA

public class GPSserviceController extends Activity {

	private NotificationManager mNotificationManager;
	private String nombreRuta;
	private Long mRutaId;	
	private String nombre;
	private String passwd;
	private String tags;
	private String seleccion;
	private String guardar;
	private String categoria;
	private String rutaColor;
	private String rutaGrosor;
	private String rutaTransp;
	private String servicio;
	private int servicioRuta;
	
	private PuntoDB mDbPunto;
	
	int id_ruta;
	int id_agrup=-1;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		loadConfig();
		setContentView(R.layout.control_service);
		
		
		  mDbPunto = new PuntoDB(this);
		  mDbPunto.open();
	        
	        
		//Button botonStart = (Button)findViewById(R.id.botonStart);
		//botonStart.setOnClickListener(mStartListener);
		Button botonPunto = (Button)findViewById(R.id.botonPunto);
		botonPunto.setOnClickListener(mPuntoListener);
		
		Button botonCamara = (Button)findViewById(R.id.botonCamara);
		botonCamara.setOnClickListener(mCamaraListener);
		
		Button botonStop = (Button)findViewById(R.id.botonStop);
		botonStop.setOnClickListener(mStopListener);
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	/*private OnClickListener mStartListener = new OnClickListener(){
		public void onClick(View v){
			startService(new Intent(GPSserviceController.this, GPSservice.class), null);
			exit();
		}
	};*/
	private final boolean loadConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);
		nombreRuta = configuracion.getString("titulo", null);
		mRutaId = configuracion.getLong("idRuta", 0);
		tags = configuracion.getString("tags", null);
		nombre = configuracion.getString("usuario", null);
		passwd = configuracion.getString("passwd", null);
		seleccion = configuracion.getString("seleccion", null);
		guardar = configuracion.getString("guardar", null);
		categoria = configuracion.getString("categoria", null);
		rutaColor = configuracion.getString("color", null); 
		rutaGrosor = configuracion.getString("grosor", null);
		rutaTransp = configuracion.getString("transparencia", null);
		servicioRuta = configuracion.getInt("servicioRuta", 0);
		return true;
	}
	
	private OnClickListener mPuntoListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent ();
			intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.PuntoInteres");
			startActivity(intent);
		}		
	};
	
	private OnClickListener mCamaraListener = new OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent ();
			intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MenuCamara");
			startActivity(intent);
		}		
	};
	
	private OnClickListener mStopListener = new OnClickListener(){
		public void onClick(View v){
			servicio = "no";
    		SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
			SharedPreferences.Editor editor = miConfig.edit();
			editor.putString("status", servicio);
			if (editor.commit()) {
				setResult(RESULT_OK);
			}
			
			LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			Location myLocation = lm.getCurrentLocation("gps");

			Vector<POI> nearestZone = com.cedetel.android.tigre.webservice.GeishaWS
					.searchNearestPoi(myLocation.getLatitude(), myLocation.getLongitude(), "cedetel", "cedetel", 1);
			int id_region = nearestZone.get(0).getId_region();
			
			int id_servicio = servicioRuta;			
			
			// Envia agrupacion a Geisha
			
			if(guardar.equals("Al finalizar")){
				
				Agrupacion agrupation=new Agrupacion();
    			AgrupacionBaseInfo abi=new AgrupacionBaseInfo();
    						
    				abi.setId_region(id_region);
    				abi.setId_servicio(id_servicio);
    				abi.setTitulo(nombreRuta);
    				abi.setLogin(nombre);
    				abi.setTags(tags);
    			
				agrupation.setAbi(abi);

				    			
    			Ruta route = new Ruta();    
    			RutaBaseInfo rbi = new RutaBaseInfo();
    			
					rbi.setNombre_ruta(nombreRuta);
					rbi.setLogin(nombre);
					rbi.setLinea_color(rutaColor);																
					rbi.setTags(tags);
					rbi.setId_region(id_region);
					rbi.setId_servicio(id_servicio);
					
				route.setRbi(rbi);				
				
				
				Vector<VerticeRuta> vertices = new Vector<VerticeRuta>();
    			Vector<Ruta> rutas = new Vector<Ruta>();
    			Vector<POI> pois = new Vector<POI>();
				
			     //id_ruta = com.cedetel.android.tigre.webservice.GeishaWS.writeRoute(route, id_agrup, nombre, passwd);

			     
				for (int i=0; i<GPSservice.ruta.size(); i++){
					Location loc = GPSservice.ruta.get(i);
					
					double latitud = loc.getLatitude();
					double longitud = loc.getLongitude();
					double altitud = loc.getAltitude();
					
					VerticeRuta infoVertice = new VerticeRuta();
					infoVertice.setLatitud(latitud);
					infoVertice.setLongitud(longitud);
					//infoVertice.setId_ruta(id_ruta);
					infoVertice.setAltitud(altitud);
					infoVertice.setAPie(0);
					//vertice.setFecha_creacion();
						
					vertices.add(infoVertice);
					
					//int id = com.cedetel.android.tigre.webservice.GeishaWS.addVerticetoRoute(infoVertice, nombre, passwd);
				}
				route.setVertices(vertices);
				
			
				
				Cursor pCursor = mDbPunto.fetchIpuntoByRoute(mRutaId);
			    startManagingCursor(pCursor);
			    
			    if((pCursor.count() == 0) || !pCursor.first()){
			    	
			    }else {	    	
				    do{
				    	POI poi = new POI();
				    	
				    	double latitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_LAT));				    	
				    	double longitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_LONG));
				    	double altitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_ALT)); 
				    	poi.setLatitud(latitude/1E6);
				    	poi.setLongitud(longitude/1E6);
				    	poi.setAltitud(altitude/1E6);
				    	poi.setLogin(nombre);
				    	poi.setTitulo(pCursor.getString(pCursor.getColumnIndex(PuntoDB.KEY_TITLE)));
				    	poi.setHtml(pCursor.getString(pCursor.getColumnIndex(PuntoDB.KEY_BODY)));
				    	poi.setId_region(id_region);
				    	poi.setId_servicio(id_servicio);
				    					    	
				    	//int id_poi = com.cedetel.android.tigre.webservice.GeishaWS.writePoi(poi, nombre, passwd);
				    	
				    	pois.add(poi);
				    	
				    } 	
				    while(pCursor.next());
				    
				    //agrupation.setPois(pois);
				    
			    }
			    
			    
			    //route.setPois(pois);
				
			    rutas.add(route);
			    agrupation.setPois(pois);
			    agrupation.setRutas(rutas);
			    
			    
			    id_agrup = com.cedetel.android.tigre.webservice.GeishaWS.writeAgrupation(agrupation, 0, nombre, passwd);
			    //id_ruta = com.cedetel.android.tigre.webservice.GeishaWS.writeRoute(route, id_agrup, nombre, passwd);

			}
			
			stopService(new Intent(GPSserviceController.this, GPSservice.class));
			mNotificationManager.cancel(R.layout.main);
			finish();
		}
	};
	

}
