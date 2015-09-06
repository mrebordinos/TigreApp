package com.cedetel.android.tigre.dibujar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Menu.Item;

import com.cedetel.android.tigre.R;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;

public class DibujarPunto extends MapActivity{
	
	private MapView mRuta;

    private static final int EXIT = Menu.FIRST;
    private static final int CAPAS = Menu.FIRST + 1;
    private static final int ZOOM = Menu.FIRST + 2;
         
    protected OverlayController myOverlayController = null;
    private Bitmap PIN_INT = null;
	private final android.graphics.Point PIN_HOTSPOT = new android.graphics.Point(5,29);
		
	private String iconoPunto;
	private String mnombre;
	private Double latitud;
	private Double longitud;
	
	int lat;
	int lng;
	
	@Override 
    public void onCreate(Bundle icicle) { 
        super.onCreate(icicle); 
        mRuta = new MapView(this); 
//    	loadConfig();
    	
        myOverlayController = this.mRuta.createOverlayController();
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
        myOverlayController.add(myLocationOverlay, true); 
        
        if (icicle == null) {
			latitud = null;
			longitud = null;
			mnombre = null;
		} else {
			latitud = icicle.getDouble("latitud");
			longitud = icicle.getDouble("longitud");
			mnombre = icicle.getString("nombre");
		}

		if (latitud == null) {
			Bundle extras = getIntent().getExtras();
			latitud = extras != null ? extras.getDouble("latitud") : null;
		}
		
		if (longitud == null) {
			Bundle extras = getIntent().getExtras();
			longitud = extras != null ? extras.getDouble("longitud") : null;
		}
		
		if (mnombre == null) {
			Bundle extras = getIntent().getExtras();
			mnombre = extras != null ? extras.getString("nombre") : null;
		}
	
		Point point = new Point((int)(latitud*1E6),(int)(longitud*1E6));
		
		MapController mc = mRuta.getController();
		mc.animateTo(point);
		mc.zoomTo(14);
		
		setContentView(mRuta);
	}

//	private final boolean loadConfig(){
//		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);		
//		iconoPunto = configuracion.getString("icono", null);		
//		return true;
// 	}
	
	protected class MyLocationOverlay extends Overlay {
		
		@Override
    	public void draw(Canvas canvas, PixelCalculator calculator, boolean shadow) {
			super.draw(canvas, calculator, shadow);
    		Paint paint = new Paint();
    		paint.setStyle(Style.FILL);
    		paint.setARGB(255, 255, 255, 255);
    		Point mapCentre = mRuta.getMapCenter();
    		canvas.drawText("latitude: " + mapCentre.getLatitudeE6()/1E6 + ", longitude: " + mapCentre.getLongitudeE6()/1E6, 5, 15, paint);    	    		    	
    		
    		Point point = new Point((int)(latitud*1E6),(int)(longitud*1E6));
    		
    		int[] screenCoords = new int[2];
    		calculator.getPointXY(point, screenCoords);
    		
//    		if (iconoPunto.equals("chincheta roja")){
//    			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappin_red); 
//    		}else if (iconoPunto.equals("chincheta azul")){
//    			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappin_blue); 
//    		}else if (iconoPunto.equals("marca A")){
//    			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappinr); 
//    		}else if (iconoPunto.equals("marca B")){
//    			PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappiny); 
//    		}
    		PIN_INT = BitmapFactory.decodeResource(mRuta.getResources(), R.drawable.mappiny);
    		paint.setARGB(255, 255, 255, 255);
    		canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);    	        
    		paint.setARGB(255, 0, 0, 0);    		
    		canvas.drawText(mnombre ,screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);
    		    		
		}
	}		 	
		
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, CAPAS, R.string.menuCapas, R.drawable.world);
		menu.add(0, ZOOM, R.string.menuZoom, R.drawable.lupa);
		menu.add(0, EXIT, R.string.menuSalir, R.drawable.close);
		return true;
	}
	  
	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch(item.getId()) {
		
		case EXIT:
			finish();
			return true;
		case CAPAS:
			new AlertDialog.Builder(DibujarPunto.this)
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
			new AlertDialog.Builder(DibujarPunto.this)
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
