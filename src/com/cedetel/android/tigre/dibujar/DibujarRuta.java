package com.cedetel.android.tigre.dibujar;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Menu.Item;
import android.widget.Toast;

import com.cedetel.android.tigre.NuevaRuta;
import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.R.array;
import com.cedetel.android.tigre.R.drawable;
import com.cedetel.android.tigre.R.string;
import com.cedetel.android.tigre.db.FotoDB;
import com.cedetel.android.tigre.db.PuntoDB;
import com.cedetel.android.tigre.db.RutaDB;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;

// DIBUJA LAS RUTAS YA GUARDADAS

public class DibujarRuta extends MapActivity {
	
	private MapView mRuta;

	private Long mRutaId;
	private RutaDB mDbHelper;
	
	private FotoDB mDbFoto;
	private PuntoDB mDbPunto;
	
	private String rutaColor;
	private String puntoColor;

	private static final int ACTIVITY_EDIT=1;
	
	private static final int INFORMACION = Menu.FIRST;
	private static final int INTERES = Menu.FIRST + 1;
//    private static final int CLEAR_POINTS = Menu.FIRST + 2;
    private static final int FOTOS = Menu.FIRST + 2;
	private static final int EXIT = Menu.FIRST + 3;
    private static final int CAPAS = Menu.FIRST + 4;
    private static final int ZOOM = Menu.FIRST + 5;
    
    protected OverlayController myOverlayController = null;
    protected List<Point> ruta = new ArrayList<Point>();
    protected List<Point> ipuntos = new ArrayList<Point>();
    protected List<String> inombres = new ArrayList<String>();
    protected List<String> iiconos = new ArrayList<String>();
    private List<Point> fotos = new ArrayList<Point>();
    private Bitmap PIN_INT = null;
	private final android.graphics.Point PIN_HOTSPOT = new android.graphics.Point(5,29);
	
	private int minLatitude = (int) (+81 * 1E6);
	private int maxLatitude = (int) (-81 * 1E6);
	private int minLongitude = (int) (+181 * 1E6);
	private int maxLongitude = (int) (-181 * 1E6);
	
	@Override 
    public void onCreate(Bundle icicle) { 
        super.onCreate(icicle); 
        mRuta = new MapView(this); 
    	loadConfig();    	
		
        mDbFoto = new FotoDB(this);
        mDbFoto.open();
        
        mDbPunto = new PuntoDB(this);
        mDbPunto.open();
        
        mDbHelper = new RutaDB(this);
	    mDbHelper.open();
	    
	    if (icicle == null) {
			mRutaId = null;
		} else {
			mRutaId = icicle.getLong(RutaDB.KEY_ROWID);
		}

		if (mRutaId == null) {
			Bundle extras = getIntent().getExtras();
			mRutaId = extras != null ? extras.getLong(RutaDB.KEY_ROWID) : null;
		}
		
		// Para obtener los puntos donde hay fotos		
	    PuntosFotos();
	    // Para obtener los puntos de interes
	    PuntosInteres();	    
	    
	    Cursor uncursor = mDbHelper.fetchRoute(mRutaId);
	    startManagingCursor(uncursor);
	    puntoColor = uncursor.getString(uncursor.getColumnIndex(RutaDB.KEY_COLOR));
		
	    myOverlayController = this.mRuta.createOverlayController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
		myOverlayController.add(myLocationOverlay, true); 
		
	    FileInputStream fIn;
	        
		try {
			fIn = openFileInput("Ruta_"+mRutaId+".txt");
			InputStreamReader isr = new InputStreamReader(fIn);
		    
			String readString = org.apache.commons.io.IOUtils.toString(fIn);
			
			//CharBuffer miCadena = CharBuffer.allocate(0);
			//miCadena.
						
//			isr.read(miCadena);
//			//miCade
//		    char[] inputBuffer = new char[1000]; 
//		    isr.read(inputBuffer); 
//		    //isr.toString()
//		    String readString = isr.toString(); 
		    
		    String[] cosa = readString.split("Punto");
		    int a,b;
		    double latitud,longitud;
		    
		    for(int i=0;i<cosa.length;i++)
		    {
		    	String miPunto = cosa[i];
		    	//Si realmente hemos cogido un punto
		    	if(miPunto.contains("Latitud"))
		    	{
		    		//Parseamos la información del punto.
		    		a = miPunto.indexOf("Latitud");
		    		b = miPunto.indexOf(";",a); //Coge el primer ";" después de Latitud
		    		latitud = Double.parseDouble(miPunto.substring(a+9,b));
		    		
		    		a = miPunto.indexOf("Longitud");
		    		b = miPunto.indexOf(";",a); //Coge el primer ";" después de Longitud
		    		longitud = Double.parseDouble(miPunto.substring(a+10,b));
		    		
		    		Point point1 = new Point((int) (latitud * 1000000),(int) (longitud * 1000000));
		    		ruta.add(point1);
		    		
		    		int latitude = point1.getLatitudeE6();
	    			int longitude = point1.getLongitudeE6();
	    			
	    			if (latitude != 0 && longitude != 0){            	            	
	    	                minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
	    	                maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;

	    	                minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
	    	                maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
	    			}
	    			
	    			MapController mc = mRuta.getController();

	    	        mc.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));
	    	        
	    	        mc.animateTo(new Point((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
	    	 
		    	}
		    }
		  

		} catch (Exception e) {
			Toast.makeText(this, "No existe esa ruta", Toast.LENGTH_SHORT );
//			e.printStackTrace();
		}        	
			
		setContentView(mRuta); 
	}	
	
	private void PuntosFotos() {
		Cursor mCursor = mDbFoto.fetchPhotoByRoute(mRutaId);
	    startManagingCursor(mCursor);
	    
	    if((mCursor.count() == 0) || !mCursor.first()){
	    	
	    }else {	    	
		    do{
		    	int latitude = mCursor.getInt(mCursor.getColumnIndex(FotoDB.KEY_LAT));
		    	int longitude = mCursor.getInt(mCursor.getColumnIndex(FotoDB.KEY_LONG));
		    	fotos.add(new Point(latitude, longitude));
		    } 
		    while(mCursor.next());
	    }
	}
	
	private void PuntosInteres() {
		Cursor pCursor = mDbPunto.fetchIpuntoByRoute(mRutaId);
	    startManagingCursor(pCursor);
	    
	    if((pCursor.count() == 0) || !pCursor.first()){
	    	
	    }else {	    	
		    do{
		    	int latitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_LAT));
		    	int longitude = pCursor.getInt(pCursor.getColumnIndex(PuntoDB.KEY_LONG));
		    	ipuntos.add(new Point(latitude, longitude));
		    	String ptitulo = pCursor.getString(pCursor.getColumnIndex(PuntoDB.KEY_TITLE));
		    	inombres.add(ptitulo);
		    	String iconoP = pCursor.getString(pCursor.getColumnIndex(PuntoDB.KEY_ICON));
		    	iiconos.add(iconoP);
		    } 	
		    while(pCursor.next());
	    }
	}
	
	private final boolean loadConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);
		rutaColor = configuracion.getString("lineaColor", null);
//		puntoColor = configuracion.getString("puntoColor", null);
//		iconoPunto = configuracion.getString("icono", null);
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
	
	protected class MyLocationOverlay extends Overlay {
		
		@Override
    	public void draw(Canvas canvas, PixelCalculator calculator, boolean shadow) {
    		super.draw(canvas, calculator, shadow);
    		Paint paint = new Paint();
    		paint.setStyle(Style.FILL);
    		paint.setARGB(255, 255, 255, 255);
    		Point mapCentre = mRuta.getMapCenter();
    		canvas.drawText("longitude: " + mapCentre.getLongitudeE6() + ", latitude: " + mapCentre.getLatitudeE6(), 5, 15, paint);

    		int[] prevScreenCoords = new int[2];
    		boolean first = true;
    		
    		for (Point point : ruta) {
    			int size = ruta.size();
    			paint.setARGB(255, 255, 255, 255);
    			canvas.drawText("Tamaño ruta = " + size, 5, 26, paint);
    			//PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappin_blue); 
    			//canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint); 
    			int[] screenCoords = new int[2];
    			calculator.getPointXY(point, screenCoords);
    			   			
    			int[] colorRGB = HextoRGB(puntoColor);
				paint.setARGB(80, colorRGB[0], colorRGB[1], colorRGB[2]);
    			canvas.drawOval(new RectF(screenCoords[0] - 5, screenCoords[1] + 5, screenCoords[0] + 5, screenCoords[1] - 5), paint);
    			

    			if (first==true){
    				paint.setARGB(255, 255, 255, 255);
    				PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.house_icon); 
        			canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint); 
    			}
    			else if(!first) {
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
		
    		for (int i =0; i<ipuntos.size(); i++){
    		//for (Point point : ipuntos) {
    			//Point point = new Point((int) iloc.getLatitude(), (int) iloc.getLongitude());          
    			Point point = ipuntos.get(i);
    			int[] screenCoords = new int[2];
    			calculator.getPointXY(point, screenCoords);
    			paint.setARGB(255, 255, 255, 255);
    			String iconoPunto = iiconos.get(i);
    			if (iconoPunto.equals("chincheta roja")){
        			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappin_red); 
    			}else if (iconoPunto.equals("chincheta azul")){
        			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappin_blue); 
    			}else if (iconoPunto.equals("marca A")){
        			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappinr); 
    			}else if (iconoPunto.equals("marca B")){
        			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappiny); 
    			}else if (iconoPunto.equals("arbol")){
        			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.tree_icon); 
    			}else if (iconoPunto.equals("flag")){
        			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.flag_icon); 
    			}
    	        canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);    	        
    	        paint.setARGB(255, 0, 0, 0);
    	        String mnombre = inombres.get(i);
    	        canvas.drawText(mnombre ,screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);
    		
    		}    	
    		
    		for (int i=0; i<fotos.size(); i++) {	    	
    			Point point = fotos.get(i);
	    		int[] screenCoords = new int[2];
				calculator.getPointXY(point, screenCoords);
				paint.setARGB(255, 255, 255, 255);
	    		PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.camera_icon); 
				canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);
    		}
		}
	}
		 
	private boolean clearPoints() {
		ruta.clear();
		ipuntos.clear();
		inombres.clear();
		mRuta.invalidate();
		return true;
	}
	
	 private boolean exit() {
	    	this.finish();
	        return true;
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 super.onCreateOptionsMenu(menu);
	      
		 menu.add(0, INFORMACION, R.string.menu1dibujar, R.drawable.dibujar);
		 menu.add(0, INTERES, R.string.menu2dibujar, R.drawable.mappin_red);
//		 menu.add(0, CLEAR_POINTS, R.string.menuBorrar, R.drawable.papelera);
		 menu.add(0, FOTOS, R.string.menu1foto, R.drawable.camera);
		 menu.add(0, CAPAS, R.string.menuCapas, R.drawable.world);
		 menu.add(0, ZOOM, R.string.menuZoom, R.drawable.lupa);
		 menu.add(0, EXIT, R.string.menuSalir, R.drawable.close);
		 if(fotos.size()==0){
			 menu.setItemShown(FOTOS, false);
		 }
		 if(ipuntos.size()==0){
			 menu.setItemShown(INTERES, false);
		 }
		 return true;
	 }
	  
	 @Override
	 public boolean onMenuItemSelected(int featureId, Item item) {
		 switch(item.getId()) {
		 case INFORMACION:
			 Intent i = new Intent(this, NuevaRuta.class);
		     i.putExtra(RutaDB.KEY_ROWID, mRutaId);
		     startSubActivity(i, ACTIVITY_EDIT);
			 return true;
		 case INTERES:
//			 interestPoint();
			 Intent intent = new Intent ();
			 intent.putExtra(PuntoDB.KEY_ROUTE, mRutaId);
			 intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MisPuntosRuta");
			 startActivity(intent);
			 return true;
//		 case CLEAR_POINTS:
//			 clearPoints();
//			 return true;
		 case FOTOS:
			 Intent intent2 = new Intent ();
			 intent2.putExtra(FotoDB.KEY_ROUTE, mRutaId);
			 intent2.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MisFotosRuta");
			 startActivity(intent2);
			 return true;
		 case EXIT:
			 exit();
			 return true;
		 case CAPAS:
			 new AlertDialog.Builder(DibujarRuta.this)
			 	.setIcon(R.drawable.car_yellow)
			 	.setTitle(R.string.menuCapas)
			 	.setItems(R.array.opcionvista, new DialogInterface.OnClickListener(){
			 		public void onClick(DialogInterface dialog, int position) {     
			 			switch(position){
			 			case 0:
			 				mRuta.toggleSatellite();
			 				break;
			 			case 1:
			 				mRuta.toggleTraffic();
			 				break;
			 			}
			 		}
			 	})
			 	.show();
			 	return true;      
	          
		 case ZOOM:
			 new AlertDialog.Builder(DibujarRuta.this)
			 	.setIcon(R.drawable.lupa)
			 	.setTitle(R.string.menuZoom)
			 	.setItems(R.array.zoomarray, new DialogInterface.OnClickListener() {
			 		public void onClick(DialogInterface dialog, int position) {     
			 			switch(position){
			 			case 0:
			 				mRuta.getController().zoomTo(mRuta.getZoomLevel()+1);
			 				break;
			 			case 1:
			 				mRuta.getController().zoomTo(mRuta.getZoomLevel()-1);
			 				break;
			 			}
			 		}
			 	})
			 	.show();
	      	 	return true;
		 }
	            	 
		 return super.onMenuItemSelected(featureId, item);
	 }
	  
	 public boolean onKeyDown (int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_I) {
			 mRuta.getController().zoomTo(mRuta.getZoomLevel()+1);
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_O) {
			 mRuta.getController().zoomTo(mRuta.getZoomLevel()-1);
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_S) {
			 mRuta.toggleSatellite();
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_T) {
			 mRuta.toggleTraffic();
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_BACK){
			 finish();
			 return true;
		 }
		 return false;
	 }
			
}
