
package com.cedetel.android.tigre.gps;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cedetel.android.tigre.MapaRutaActual;
import com.cedetel.android.tigre.PuntoInteres;
import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.geisha.gis.Agrupacion;
import com.cedetel.android.tigre.geisha.gis.AgrupacionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.Ruta;
import com.cedetel.android.tigre.geisha.gis.RutaBaseInfo;
import com.cedetel.android.tigre.geisha.gis.VerticeRuta;

// PROCESO EN BACKGROUND QUE
//								VA GUARDANDO LA RUTA

public class GPSservice extends Service{
	
	public static final String LOCUPDATE_ACTION = "com.cedetel.android.tigre.gps.LOCATION_UPDATE";
	private static final String LOG_TAG = "gps.GPSservice";
	
	// Variables de configuracion
	private String nombreRuta;
	
	private Long mRutaId;	
	private String nombre;
	private String passwd;
	private String seleccion;
	private String guardar;
	private String categoria;
	private String rutaColor;
	private String rutaGrosor;
	private String rutaTransp;
	private String tags;
	private int servicioRuta;
	private Long tiempoGPS;
	private Long minDistancia;
	int i=0;
	int j=1;
	
	private String myFile;
	private String locProviderName = "gps";
	private static GPSservice appGPS = null;
	private static int GPS_NOT = R.layout.simgps;
	private final IBinder mBinder = new myBinder();
	private NotificationManager mNM;
	public static List<Location> ruta = new ArrayList<Location>();
	private Handler mHandler= new Handler();
	
	private int num_punto= 0;
	int id_region;
	int id_agrup=-1;
	int id_ruta;
	double latitud=0;
	double longitud=0;
	double altitud = 0;
//	final Runnable mUpdateResults = new Runnable() {
//		public void run(){
//			updateResults();
//		}
//	};
	
	public class myBinder extends Binder {
        GPSservice getService() {
            return GPSservice.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	 
	@Override
	protected void onCreate() {
		loadConfig();
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		setIcon(R.drawable.huella_small, R.string.statusText2, false);
		appGPS = this;
		Toast.makeText(this, R.string.startService, Toast.LENGTH_SHORT).show();
				
		activateLocationUpdates(locProviderName);
		/*saveResults();
		Intent intent = new Intent();
		intent.setLaunchFlags(Intent.NEW_TASK_LAUNCH);
		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.gps.GPSserviceController");
		startActivity(intent);
		*/
	}
	
	public static GPSservice getActivityReference() {
		  return appGPS;
	}
	
	public void handleUpdateEvent( String action, Bundle extras ) {
		  if( LOCUPDATE_ACTION.equals( action ) ) {
			if( extras == null )
				Log.e( LOG_TAG, "handleUpdateEvent: extras==null" );
			final Location l = (Location)extras.get( "location" );
	        mHandler.post(new Runnable() {
	           public void run() {
				  saveResults( l );
	           }
	        });
		  }
	}
	
	
	private void activateLocationUpdates( String locProviderName ) {
	      LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		  LocationProvider locProvider = (LocationProvider)lm.getProvider( locProviderName );
		  
		  Location loc = lm.getCurrentLocation( locProviderName );
		  saveResults( loc );
		  Intent fireIntent = new Intent( LOCUPDATE_ACTION );
		  lm.requestUpdates( locProvider,tiempoGPS,minDistancia,fireIntent );
	}
	
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
		tiempoGPS = configuracion.getLong("tiempoGPS", 0);
		minDistancia = configuracion.getLong("minDistancia", 0);
		return true;
	}
	
	
	private void saveResults( Location mLoc ){
		try {
			mLoc.getLatitude();
			mLoc.getLongitude();
			mLoc.setTime(System.currentTimeMillis());
			ruta.add(mLoc);
			
			myFile="Ruta_"+ mRutaId +".txt";		
			PrintStream mprint = new PrintStream(this.openFileOutput(myFile, MODE_APPEND));
			
			int a = 0;
			
			//for (int i=0;  Location loc : ruta) {	
				Location loc = ruta.get(i);
				mprint.print("Punto " + i + "\n");
				mprint.print("Latitud: " + loc.getLatitude() + "; ");
				mprint.print("Longitud: " + loc.getLongitude() + "; ");
				mprint.print("Tiempo: " + loc.getTime() + ";\n");
				
				
				//Si sest� conectado en tiempo real a Geisha vamos guardando los vertices en GEISHA
				latitud = loc.getLatitude();
				longitud = loc.getLongitude();
				altitud = loc.getAltitude();
				
				if(guardar.equals("En tiempo real")){
					//if (a==0){
					//int id_region = 2;
					int id_servicio = servicioRuta;
					
		    		if(num_punto==0){
		    			
		    			Vector<POI> nearestZone = com.cedetel.android.tigre.webservice.GeishaWS
		    					.searchNearestPoi(loc.getLatitude(), loc.getLongitude(), "cedetel", "cedetel", 1);
		    			id_region = nearestZone.get(0).getId_region();
		    			  
		    			// Al ser el primer punto creamos la agrupacion y le pasamos la informacion (titulo,
		    			// zona, servicio..., los datos de la Ruta y el primer v�rtice) y nos devolver� el 
		    			// identificador de la agrupaci�n insertada
		    			Agrupacion agrupation=new Agrupacion();
		    			AgrupacionBaseInfo abi=new AgrupacionBaseInfo();
		    			//VerticeRuta vert=new VerticeRuta();
		    					    			
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
														
							Vector vVertices=new Vector();
								VerticeRuta vertice = new VerticeRuta();
								vertice.setLatitud(latitud);
								vertice.setLongitud(longitud);
								vertice.setAltitud(250);
								vertice.setAPie(0);
								//vertice.setFecha_creacion();
								
								vVertices.add(vertice);
						
							route.setVertices(vVertices);
							
						route.setRbi(rbi);				
						
						
						 id_agrup = com.cedetel.android.tigre.webservice.GeishaWS.writeAgrupation(agrupation, 0, nombre, passwd);
					     id_ruta = com.cedetel.android.tigre.webservice.GeishaWS.writeRoute(route, id_agrup, nombre, passwd);
						

		    		}else{
		    			
		    			VerticeRuta infoVertice = new VerticeRuta();
		    			infoVertice.setLatitud(latitud);
		    			infoVertice.setLongitud(longitud);
		    			infoVertice.setId_ruta(id_ruta);
		    			infoVertice.setAltitud(altitud);
		    			infoVertice.setAPie(0);
						//vertice.setFecha_creacion();
						
								    			
		    			int id = com.cedetel.android.tigre.webservice.GeishaWS.addVerticetoRoute(infoVertice, nombre, passwd);

		    		}		    				    				    		    		 

		    		if(PuntoInteres.interes.size()==j) {
		    		
		    			POI poi = new POI();
				    	Location iloc = PuntoInteres.interes.get(j-1);
				    	double latitude = iloc.getLatitude();				    	
				    	double longitude = iloc.getLongitude();
				    	double altitude = iloc.getAltitude();
				    	poi.setLatitud(latitude);
				    	poi.setLongitud(longitude);
				    	poi.setAltitud(altitude);
				    	poi.setLogin(nombre);
				    	poi.setTitulo(PuntoInteres.inombres.get(j-1));
				    	poi.setId_agrupacion(id_agrup);
				    	poi.setId_region(id_region);
				    	poi.setId_servicio(id_servicio);
				    					    	    	
				    	int id_poi = com.cedetel.android.tigre.webservice.GeishaWS.writePoi(poi, nombre, passwd);
				    					    			    		    	
				    	j++;
		    		}
		    		
		    	}
		    	num_punto=1;
					
		    	i++; 
				
			
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
//	private void updateResults(){
//		 LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		 LocationProvider locProvider = (LocationProvider)lm.getProvider( locProviderName );		 
//		 Intent fireIntent = new Intent( LOCUPDATE_ACTION );
//		 lm.requestUpdates( locProvider,tiempoGPS,minDistancia,fireIntent );
//	}

	@Override
	protected void onDestroy() { 
		super.onDestroy();
		
		mNM.cancel(GPS_NOT);
		Toast.makeText(this, R.string.stopService, Toast.LENGTH_SHORT).show();
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Intent fireIntent = new Intent( LOCUPDATE_ACTION );
		lm.removeUpdates( fireIntent );
	}
	
	private void setIcon(int iconId, int textId, boolean showTicker) {        
    	Intent contentIntent = new Intent(this, GPSserviceController.class);
        Intent appIntent = new Intent(this, MapaRutaActual.class);
        CharSequence text = getText(textId);
        String tickerText = showTicker ? getString(textId) : null;

        mNM.notify( R.layout.main, 
                   new Notification(this, iconId, tickerText, System.currentTimeMillis(), 
                       getText(R.string.statusText1), text, contentIntent, R.drawable.tigre, 
                       getText(R.string.app_name), appIntent));
    }
	
	
}