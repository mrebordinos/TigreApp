package com.cedetel.android.tigre;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentReceiver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Menu.Item;
import android.widget.Toast;

import com.cedetel.android.tigre.db.PuntoDB;
import com.cedetel.android.tigre.geisha.gis.Agrupacion;
import com.cedetel.android.tigre.geisha.gis.AgrupacionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.Ruta;
import com.cedetel.android.tigre.geisha.gis.RutaBaseInfo;
import com.cedetel.android.tigre.geisha.gis.VerticeRuta;
import com.cedetel.android.tigre.gps.GPSservice;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;

// 		MAPA DEL GPS Y LA RUTA EN TIEMPO REAL

public class MapaRuta extends MapActivity {
	
	private MapView mapaRuta;
	
	// MENU DEL MAPA
    private static final int INIT_RUTA = Menu.FIRST;
    private static final int GEISHA = Menu.FIRST + 1;
    private static final int INTERES = Menu.FIRST + 2;
    private static final int REMOVE_POINT = Menu.FIRST + 3;
    private static final int CLEAR_POINTS = Menu.FIRST + 4;
    private static final int EXIT = Menu.FIRST + 5;
    private static final int CAPAS = Menu.FIRST + 6;
    private static final int ZOOM = Menu.FIRST + 7;
//    private static final int SEARCH = Menu.FIRST + 8;
        
    public static final String LOCUPDATE_ACTION = "com.cedetel.android.tigre.gps.LOCATION_UPDATE";
    protected static final String LOCATION_CHANGED_ACTION =	"com.cedetel.android.tigre.gps.LOCATION_CHANGED";
    protected final IntentFilter myIntentFilter = new IntentFilter(LOCUPDATE_ACTION);
    protected final Intent myIntent = new Intent(LOCATION_CHANGED_ACTION);
    protected MyIntentReceiver myIntentReceiver = new MyIntentReceiver();
    
    protected OverlayController myOverlayController = null;
    private String locProviderName = "gps";
    protected Location myLocation = null;
    protected LocationManager myLocationManager = null;
    private NotificationManager mNM;
    
    public List<Location> ruta = new ArrayList<Location>();
    public List<Point> interes = new ArrayList<Point>();
    public List<String> inombres = new ArrayList<String>();
    public List<String> iiconos = new ArrayList<String>();
    
    protected boolean doUpdates = false;
    private boolean start = true;
    private String geishaColor;
    private String rutaColor;
    private String puntoColor;
    private String iconoPunto;
    private long tiempoGPS; //milisegundos
    private long minDistancia; // metros
    private String servicio;
    
    int id_agrup=-1;
	int id_ruta;
	double latitud_E6=0;
	double longitud_E6=0;
    
    private String nombreRuta;
	private Long mRutaId;	
	private String tags;
	private String nombre;
	private String passwd;
	private String seleccion;
	private String guardar;
	private String categoria;
	private int servicioRuta;
	
	private PuntoDB mDbPunto;
 
	private int num_punto= 0;
    
    private Bitmap PIN_INT = null;
	private final android.graphics.Point PIN_HOTSPOT = new android.graphics.Point(5,29);
	
	class MyIntentReceiver extends IntentReceiver {
        @Override
        public void onReceiveIntent(Context context, Intent intent) {
             if(MapaRuta.this.doUpdates)
            	 MapaRuta.this.updateView();
        }
    } 
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mapa);
		loadTigreConfig();	
		loadConfig();
		mapaRuta = (MapView)findViewById(R.id.map);
		
		mDbPunto = new PuntoDB(this);
        mDbPunto.open();
        
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myOverlayController = this.mapaRuta.createOverlayController();
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
        myOverlayController.add(myLocationOverlay, true);   

        updatePosition();
        
        GPSAutoRefreshing();
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
//		categoria = configuracion.getString("categoria", null);
		servicioRuta = configuracion.getInt("servicioRuta", 0);
		tiempoGPS = configuracion.getLong("tiempoGPS", 0);
		minDistancia = configuracion.getLong("minDistancia", 0);
		return true;
	}
	
    private final boolean loadTigreConfig() {
		SharedPreferences config = getSharedPreferences("RedirectData", 0);
			rutaColor = config.getString("lineaColor", null);
			geishaColor = config.getString("color", null); 
			puntoColor = config.getString("puntoColor", null);
			iconoPunto = config.getString("icono", null);
			tiempoGPS = config.getLong("tiempoGPS", 1000);
			minDistancia = config.getLong("minDistancia", 300);			
		return true;
	}
    

    private int[] HextoRGB(String color){
		 
		 int[] colorRGB = new int[3];
		 String colorR = color.substring(0, 2);
		 String colorG = color.substring(2, 4);
		 String colorB = color.substring(4, 6);
		 
		 int r = Integer.parseInt(colorR, 16);
		 int g = Integer.parseInt(colorG, 16);
		 int b = Integer.parseInt(colorB, 16);
	   
		 colorRGB[0]=r;
		 colorRGB[1]=g;
		 colorRGB[2]=b;
	   	   
		 return colorRGB;
	    
	 }
    
//	Dibuja la Ruta sobre el mapa
  protected class MyLocationOverlay extends Overlay {
  	@Override
  	public void draw(Canvas canvas, PixelCalculator calculator, boolean shadow) {
  		super.draw(canvas, calculator, shadow);
  		Paint paint = new Paint();
  		paint.setStyle(Style.FILL);
  		
  		paint.setARGB(255, 255, 255, 255);
  		Point mapCentre = mapaRuta.getMapCenter();
  		
  		canvas.drawText("longitude: " + mapCentre.getLongitudeE6() + ", latitude: " + mapCentre.getLatitudeE6(), 5, 15, paint);
      	
  		if (doUpdates && myLocation != null && locProviderName != null) {    			
  			Double lat = MapaRuta.this.myLocation.getLatitude() * 1E6;
  			Double lng = MapaRuta.this.myLocation.getLongitude() * 1E6;
  			Point point = new Point(lat.intValue(), lng.intValue());
  			
  			MapController mc = mapaRuta.getController();
  	    	mc.setFollowMyLocation(true);
  	    	mc.centerMapTo(point, doUpdates);
  			//mc.animateTo(point);
  	    	setPoint();
  	    	int[] myScreenCoords = new int[2];
  			calculator.getPointXY(point, myScreenCoords);
  			
  			//paint.setARGB(255, 80, 30, 150);
  			paint.setARGB(255, 255, 255, 255);
  			Bitmap PIN_GPS = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.perdida); 
  			canvas.drawBitmap(PIN_GPS, myScreenCoords[0] - PIN_HOTSPOT.x, myScreenCoords[1] - PIN_HOTSPOT.y, paint); 
  			//canvas.drawOval(new RectF(myScreenCoords[0] - 5, myScreenCoords[1] + 5, myScreenCoords[0] + 5, myScreenCoords[1] - 5), paint);
              //canvas.drawText("longitude: " + point.getLongitudeE6() + ", latitude: " + point.getLatitudeE6(), 5, 26, paint);      
  		}
  		
         	int[] prevScreenCoords = new int[2];
         	boolean first = true;
  		
  		for (Location loc : ruta) {
  			Point point = new Point((int) loc.getLatitude(), (int) loc.getLongitude());          
  			int[] screenCoords = new int[2];
  			calculator.getPointXY(point, screenCoords);
  			
  			int[] colorRGB = HextoRGB(puntoColor);
				paint.setARGB(80, colorRGB[0], colorRGB[1], colorRGB[2]);
  			
	    		canvas.drawOval(new RectF(screenCoords[0] - 5, screenCoords[1] + 5, screenCoords[0] + 5, screenCoords[1] - 5), paint);
  			if (!first) {
  				if (rutaColor.equals("Rojo")){
	    				paint.setARGB(80, 255, 0, 0);
	    			}else if (rutaColor.equals("Verde")){
	    				paint.setARGB(80, 0, 255, 0);
	    			}else if (rutaColor.equals("Azul")){
	    				paint.setARGB(80, 0, 0, 255);
	    			}else if (rutaColor.equals("Amarillo")){
	    				paint.setARGB(80, 255, 255, 50);
	    			}else if (rutaColor.equals("Morado")){
	    				paint.setARGB(80, 166, 0, 166);
	    			}else if (rutaColor.equals("Negro")){
	    				paint.setARGB(80, 0, 0, 0);
	    			}else {
	    				paint.setARGB(80, 156, 192, 36);
	    			}
  				canvas.drawLine(prevScreenCoords[0], prevScreenCoords[1], screenCoords[0], screenCoords[1], paint);
  			}
  			prevScreenCoords[0] = screenCoords[0];
  			prevScreenCoords[1] = screenCoords[1];
  			first = false;
  		}
  		
  		abrirPuntoDB();
  		for (int i=0; i<interes.size(); i++) {
  			//Point point = new Point((int) iloc.getLatitude(), (int) iloc.getLongitude()); 
  			Point point = interes.get(i);
  			int[] screenCoords = new int[2];
  			calculator.getPointXY(point, screenCoords);
  			paint.setARGB(255, 255, 255, 255);
			String iconoPunto = iiconos.get(i);
			if (iconoPunto.equals("chincheta roja")){
    			PIN_INT = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.mappin_red); 
			}else if (iconoPunto.equals("chincheta azul")){
    			PIN_INT = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.mappin_blue); 
			}else if (iconoPunto.equals("marca A")){
    			PIN_INT = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.mappinr); 
			}else if (iconoPunto.equals("marca B")){
    			PIN_INT = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.mappiny); 
			}else if (iconoPunto.equals("arbol")){
    			PIN_INT = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.tree_icon); 
			}else if (iconoPunto.equals("flag")){
    			PIN_INT = BitmapFactory.decodeResource(mapaRuta.getResources(), R.drawable.flag_icon); 
			}
	        canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);    	        
	        paint.setARGB(255, 0, 0, 0);
	        String mnombre = inombres.get(i);
	        canvas.drawText(mnombre ,screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);
		
  		}
  		
  	}
  }
    
    
    
//  Comienza la ruta
    protected void startRoute(){   
    	doUpdates = true;
   		onResume();
    }
    
    
    
// 	Actualiza la posicion
    private void updatePosition() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Location myLocation = locationManager.getCurrentLocation(locProviderName); 
                		
        Double lat = myLocation.getLatitude() * 1E6;
    	Double lng = myLocation.getLongitude() * 1E6;
    	
    	Point point = new Point(lat.intValue(), lng.intValue());
    	
        MapController mc = mapaRuta.getController();
        mc.zoomTo(15);        
    	mc.centerMapTo(point, false);  //mc.setFollowMyLocation(true);    
    	
    	
    	
	}
    
    
// 	Borra todos los puntos de la ruta
    private boolean clearPoints() {
    	ruta.clear();
    	interes.clear();
    	this.mapaRuta.invalidate();
    	return true;
    }
    
    
// 	Borra el ultimo punto
    private boolean removePoint() {
    	if (!ruta.isEmpty()) {
    		ruta.remove(ruta.size()-1);
    		mapaRuta.invalidate();
    	}
    	return true;
    }
    
    
    @Override
    public void onResume() {
         super.onResume();  //doUpdates = true;
         registerReceiver(myIntentReceiver, myIntentFilter);       
    }

//    @Override
//	protected void onPause() {
//		stopRuta();
//		super.onPause();
//	}
//
//	@Override
//    public void onFreeze(Bundle icicle) {
//    	doUpdates = false;
//    	unregisterReceiver(myIntentReceiver);
//    	super.onFreeze(icicle);
//    }  
    
    
    
// 	Actualiza la vista  
    private void updateView() {
    	myLocation = myLocationManager.getCurrentLocation(locProviderName);	
    	mapaRuta.invalidate();
    	
    	
    }

    
//	Actualiza la señal del GPS cada x tiempo, o cada x distancia
    private void GPSAutoRefreshing() { 
    	
    	LocationProvider provider = (LocationProvider)myLocationManager.getProvider( locProviderName );		  	  
    	this.myLocationManager.requestUpdates(provider, tiempoGPS, minDistancia, new Intent(LOCATION_CHANGED_ACTION)); 
        this.myIntentReceiver = new MyIntentReceiver(); 
   }


// 	Añade un punto de interes
    private void interestPoint() {
    	
    	Intent intent = new Intent ();
		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.PuntoInteres");
		startActivity(intent);
					
    }	
    
    private void abrirPuntoDB(){
    	
    	Cursor pCursor = mDbPunto.fetchIpuntoByRoute(mRutaId);
	    startManagingCursor(pCursor);
	    
	    if((pCursor.count() == 0) || !pCursor.first()){
	    	
	    }else {	    	
		    do{			    	
		    	int latitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_LAT));
		    	int longitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_LONG));
		    	Point iloc = new Point(latitude, longitude);			    	
		    	interes.add(iloc);
		    	String ptitulo = pCursor.getString(pCursor.getColumnIndex(PuntoDB.KEY_TITLE));
		    	inombres.add(ptitulo);
		    	String iconoP = pCursor.getString(pCursor.getColumnIndex(PuntoDB.KEY_ICON));
		    	iiconos.add(iconoP);
		    } 	
		    while(pCursor.next());
		    					  				    
	    }
    }
    
// 	Coge el punto situado en el centro del mapa y lo añade a la ruta
    private boolean setPoint() {
    	Point pCentro = mapaRuta.getMapCenter();
    	
    	latitud_E6=pCentro.getLatitudeE6();
    	longitud_E6=pCentro.getLongitudeE6();
    	    	
    	Location locCentro = new Location();
    	locCentro.setLatitude(latitud_E6);
    	locCentro.setLongitude(longitud_E6);
    	locCentro.setTime(System.currentTimeMillis());
    	ruta.add(locCentro);
    	mapaRuta.invalidate();
    	
    	return true;
    }
    
    
// 	Para de guardar la ruta y sale del mapa
    private void stopRuta() {
    	doUpdates = false;	
    	MapController myMapControl = mapaRuta.getController();
    	myMapControl.stopAnimation(true);
    	myMapControl.setFollowMyLocation(doUpdates);
    	finish();
    }
    
    
    
// 	Crea el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);    	
    	menu.add(0, INIT_RUTA, R.string.menu8mapa, R.drawable.pasos);
        menu.add(0, GEISHA, R.string.menu3, R.drawable.connect);
        menu.add(0, INTERES, R.string.menu11mapa, R.drawable.mappin_red);
        menu.add(0, CLEAR_POINTS, R.string.menuBorrar, R.drawable.papelera);
        menu.add(0, EXIT, R.string.menuSalir, R.drawable.close);
        menu.add(0, REMOVE_POINT, R.string.menu13mapa, R.drawable.back);
        menu.add(0, CAPAS, R.string.menuCapas, R.drawable.world);
        menu.add(0, ZOOM, R.string.menuZoom, R.drawable.lupa);
//        menu.add(0, SEARCH, R.string.menuBuscar, R.drawable.flag_icon);
        return true;
    }
    
// 	Cambia el boton dependiendo de si tiene que iniciar o parar la ruta
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {	
		if(start==false){ 
            menu.findItem(INIT_RUTA).setIcon(R.drawable.save);
            menu.findItem(INIT_RUTA).setTitle("Terminar ruta");
		}else if (start==true){ 
            menu.findItem(INIT_RUTA).setIcon(R.drawable.pasos);
            menu.findItem(INIT_RUTA).setTitle("Iniciar ruta");
		}
        return super.onPrepareOptionsMenu(menu);
	}
    
// 	Lo que hace cada elemento del menu
	@Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        switch(item.getId()) {
        case INIT_RUTA:        	
        	// Inicia la ruta y saca un dialogo para salir o no del mapa
        	if (start == true){
        		servicio = "si";
        		SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
				SharedPreferences.Editor editor = miConfig.edit();
				editor.putString("status", servicio);
				if (editor.commit()) {
					setResult(RESULT_OK);
				}
				new AlertDialog.Builder(MapaRuta.this)
        		.setTitle(R.string.dialog)
        		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        				
        				Intent intent = new Intent();
        				intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.gps.GPSservice");
        				startService(intent, null);
        				startRoute();
        			}
        		})
        		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        				
        				Intent intent = new Intent();
        				intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.gps.GPSservice");
        				startService(intent, null);
        				finish();
        			}
        		})
        		.show();
        		start = false;
        		return true;
        	}
        	
        	// Para la ruta y sale del mapa
        	else if (start == false){
        		servicio = "no";
        		SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
				SharedPreferences.Editor editor = miConfig.edit();
				editor.putString("status", servicio);
				if (editor.commit()) {
					setResult(RESULT_OK);
				}
				loadConfig();
				
				LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				Location myLocation = lm.getCurrentLocation("gps");

				Vector<POI> nearestZone = com.cedetel.android.tigre.webservice.GeishaWS
						.searchNearestPoi(myLocation.getLatitude(), myLocation.getLongitude(), "cedetel", "cedetel", 1);
				int id_region = nearestZone.get(0).getId_region();
				
				int id_servicio = servicioRuta;			
				
				// Envia agrupacion a Geisha al finalizar la ruta
				
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
						rbi.setLinea_color(geishaColor);																
						rbi.setTags(tags);
						rbi.setId_region(id_region);
						rbi.setId_servicio(id_servicio);
						
					route.setRbi(rbi);				
					
					
					Vector<VerticeRuta> vertices = new Vector<VerticeRuta>();
	    			Vector<Ruta> rutas = new Vector<Ruta>();
	    			Vector<POI> pois = new Vector<POI>();
					
			     
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
					    					    	    	
					    	pois.add(poi);
					    	
					    } 	
					    while(pCursor.next());
					    					  				    
				    }
				 
				    rutas.add(route);
				    agrupation.setPois(pois);
				    agrupation.setRutas(rutas);
				    
				    
				    id_agrup = com.cedetel.android.tigre.webservice.GeishaWS.writeAgrupation(agrupation, 0, nombre, passwd);

				}
				
        		stopService(new Intent(MapaRuta.this, GPSservice.class));
        		mNM.cancel(R.layout.main);
        		Toast.makeText(this, R.string.stopService, Toast.LENGTH_SHORT).show();
        		stopRuta();
        		start = true;
        		return true;
        	}
        	
        // Accede al menu del geisha
        case GEISHA:
        	Intent intent = new Intent();
			intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.geisha.GeishaMenu");
			startActivity(intent);
        	return true;
        	
        // Añade un punto de interes
        case INTERES:
        	interestPoint();       
        	return true;
        	
        // Sale del mapa
        case EXIT:
        	finish();
        	return true;
        	
        // Borra el ultimo punto
        case REMOVE_POINT:	
        	removePoint();
        	return true;
        	
        // Borra toda la ruta
        case CLEAR_POINTS:
        	clearPoints();
        	return true; 
        
        // Cambia la vista entre satelite/mapa y añade la capa de trafico 
        case CAPAS:
        	new AlertDialog.Builder(MapaRuta.this)
            .setIcon(R.drawable.car_yellow)
            .setTitle(R.string.menuCapas)
            .setItems(R.array.opcionvista, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int position) {                        
                    switch(position){
                    case 0:
                    	mapaRuta.toggleSatellite();
                   	 break;
                    case 1:
                    	mapaRuta.toggleTraffic();
                   	 break;
                    }
                }
            })
            .show();
//        	CheckBox c = new CheckBox(this); 
//            this.addContentView(c, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));      	
        	return true;      
        
        // Zoom del mapa
        case ZOOM:
        	new AlertDialog.Builder(MapaRuta.this)
        	.setIcon(R.drawable.lupa)
        	.setTitle(R.string.menuZoom)
        	.setItems(R.array.zoomarray, new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int position) {                          
        			switch(position){
        			case 0:
        				mapaRuta.getController().zoomTo(mapaRuta.getZoomLevel()+1);
        				break;
        			case 1:
        				mapaRuta.getController().zoomTo(mapaRuta.getZoomLevel()-1);
        				break;
        			}
        		}
        	})
        	.show();
        	return true;
        
//        case SEARCH:
//        	startSearch(null, null); 
//        	return true;
        }
        return super.onMenuItemSelected(featureId, item);
	}
	
	// Acceso por teclado de algunos controles del mapa
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_I) {
			mapaRuta.getController().zoomTo(mapaRuta.getZoomLevel()+1);
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_O) {
			mapaRuta.getController().zoomTo(mapaRuta.getZoomLevel()-1);
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_S) {
			mapaRuta.toggleSatellite();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_T) {
			mapaRuta.toggleTraffic();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_R) {
			removePoint();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
	 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
	 		startActivity(intent);
	 		finish();
			return true;
		 }
		return false;
	}
        
}