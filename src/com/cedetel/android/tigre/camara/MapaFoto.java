package com.cedetel.android.tigre.camara;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import com.cedetel.android.tigre.db.FotoDB;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;

public class MapaFoto extends MapActivity{
	private MapView mMapa;
	private String mTitle;
	private int latitud;
	private int longitud;

	private static final int EXIT = Menu.FIRST;
	private static final int CAPAS = Menu.FIRST + 1;
	private static final int ZOOM = Menu.FIRST + 2;
    
	protected OverlayController myOverlayController = null;
    private Bitmap PIN_INT = null;
	private final android.graphics.Point PIN_HOTSPOT = new android.graphics.Point(5,29);
	
	private Long mRowId;
	private FotoDB mDbHelper;
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	    mMapa = new MapView(this); 
	     
	    mDbHelper = new FotoDB(this);
	    mDbHelper.open();
	    
	    mRowId = icicle != null ? icicle.getLong(FotoDB.KEY_ROWID) : null;
	    if (mRowId == null) {
	        Bundle extras = getIntent().getExtras();
	        mRowId = extras != null ? extras.getLong(FotoDB.KEY_ROWID) : null;
	    }
	    
	    myOverlayController = this.mMapa.createOverlayController();
	    MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
	    myOverlayController.add(myLocationOverlay, true); 
	    
	    populateFields();
	    Point point = new Point(latitud, longitud);
	    MapController mc = mMapa.getController();
    	mc.zoomTo(16); 
    	mc.centerMapTo(point, false);
    	
        setContentView(mMapa);
	}

	protected class MyLocationOverlay extends Overlay {
		
		@Override
    	public void draw(Canvas canvas, PixelCalculator calculator, boolean shadow) {
    		super.draw(canvas, calculator, shadow);
    		Paint paint = new Paint();
    		paint.setStyle(Style.FILL);
    		paint.setARGB(255, 255, 255, 255);
    		Point mapCentre = mMapa.getMapCenter();
    		canvas.drawText("longitude: " + mapCentre.getLongitudeE6() + ", latitude: " + mapCentre.getLatitudeE6(), 5, 15, paint);
    		
    		Point point = new Point(latitud, longitud);
    		int[] screenCoords = new int[2];
	    	calculator.getPointXY(point, screenCoords);
	    	
	    	PIN_INT = BitmapFactory.decodeResource(mMapa.getResources(), R.drawable.camera_icon); 
	    	canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);
    		
		}
		
	}
	
	private void populateFields() {
	if (mRowId != null) {
        Cursor photo = mDbHelper.fetchPhoto(mRowId);
        startManagingCursor(photo);
        mTitle = photo.getString(photo.getColumnIndex(FotoDB.KEY_TITLE));
        latitud = photo.getInt(photo.getColumnIndex(FotoDB.KEY_LAT));
        longitud = photo.getInt(photo.getColumnIndex(FotoDB.KEY_LONG));            
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
			 new AlertDialog.Builder(MapaFoto.this)
			 	.setIcon(R.drawable.car_yellow)
			 	.setTitle(R.string.menuCapas)
			 	.setItems(R.array.opcionvista, new DialogInterface.OnClickListener(){
			 		public void onClick(DialogInterface dialog, int position) {     
			 			switch(position){
			 			case 0:
			 				mMapa.toggleSatellite();
			 				break;
			 			case 1:
			 				mMapa.toggleTraffic();
			 				break;
			 			}
			 		}
			 	})
			 	.show();
			 	return true;      
	          
		 case ZOOM:
			 new AlertDialog.Builder(MapaFoto.this)
			 	.setIcon(R.drawable.lupa)
			 	.setTitle(R.string.menuZoom)
			 	.setItems(R.array.zoomarray, new DialogInterface.OnClickListener() {
			 		public void onClick(DialogInterface dialog, int position) {     
			 			switch(position){
			 			case 0:
			 				mMapa.getController().zoomTo(mMapa.getZoomLevel()+1);
			 				break;
			 			case 1:
			 				mMapa.getController().zoomTo(mMapa.getZoomLevel()-1);
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
			 mMapa.getController().zoomTo(mMapa.getZoomLevel()+1);
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_O) {
			 mMapa.getController().zoomTo(mMapa.getZoomLevel()-1);
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_S) {
			 mMapa.toggleSatellite();
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_T) {
			 mMapa.toggleTraffic();
			 return true;
		 }else if (keyCode == KeyEvent.KEYCODE_BACK) {
			 finish();
			 return true;
		 }
		 return false;
	 }
}