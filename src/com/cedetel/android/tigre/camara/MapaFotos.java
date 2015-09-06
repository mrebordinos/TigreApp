package com.cedetel.android.tigre.camara;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.FotoDB;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;

public class MapaFotos extends MapActivity {

	private MapView mMapa;
    private MapController mc = null;
    
    private int minLatitude = (int) (+81 * 1E6);
    private int maxLatitude = (int) (-81 * 1E6);
    private int minLongitude = (int) (+181 * 1E6);;
    private int maxLongitude = (int) (-181 * 1E6);;
	
    protected OverlayController myOverlayController = null;
    
    private Bitmap PIN_INT = null;
	private final android.graphics.Point PIN_HOTSPOT = new android.graphics.Point(5,29);
	
	private Long mRowId;
	private FotoDB mDbHelper;
	private List<Point> mPoints = new ArrayList<Point>();
	private Cursor mCursor;
	
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mapa_fotos);
        
        mMapa = new MapView(this); 
        mMapa.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        LinearLayout rl = (LinearLayout) findViewById(R.id.mapaFotos);
        rl.addView(mMapa);
        
        mDbHelper = new FotoDB(this);
        mDbHelper.open();
        
        mRowId = icicle != null ? icicle.getLong(FotoDB.KEY_ROWID) : null;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(FotoDB.KEY_ROWID) : null;
        }
             
        mCursor = mDbHelper.fetchAllPhotos();
        startManagingCursor(mCursor);

        if (mCursor!= null){
        	setupMap();
        	
        		Gallery g = (Gallery) findViewById(R.id.gallery);
        		g.setAdapter(new ImageAdapter(mCursor, this));
        		g.setSelectorSkin(getResources().getDrawable(android.R.drawable.box));
        		g.setOnItemSelectedListener(listener);
        	
        }
               
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
     		
    		for (Point point : mPoints) {	    			    	  
	    		int[] screenCoords = new int[2];
				calculator.getPointXY(point, screenCoords);
				
	    		PIN_INT = BitmapFactory.decodeResource(mMapa.getResources(), R.drawable.camera_icon); 
				canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x, screenCoords[1] - PIN_HOTSPOT.y, paint);
    		}
		}
		
	}
	

    private void setupMap()
    {    
    	mCursor = mDbHelper.fetchAllPhotos();
        startManagingCursor(mCursor);       
        
        while (mCursor.next())
        {
            int latitude = mCursor.getInt(mCursor.getColumnIndex(FotoDB.KEY_LAT));
            int longitude = mCursor.getInt(mCursor.getColumnIndex(FotoDB.KEY_LONG));
       
            if (latitude != 0 && longitude != 0)
            {            	            	
                minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
                maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;

                minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
                maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;

                mPoints.add(new Point(latitude, longitude));
            }
        }

        mc = mMapa.getController();

        mc.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));
        
        // Centrado en el medio de todos los puntos
        mc.animateTo(new Point((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
 
        myOverlayController = this.mMapa.createOverlayController();
        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
        myOverlayController.add(myLocationOverlay, true); 
        
    }

    
    public OnItemSelectedListener listener = new OnItemSelectedListener(){
    	public void onItemSelected(AdapterView parent, View v, int position, long id)
    	{
    		Cursor tmpCursor = mDbHelper.fetchPhoto(id);
            startManagingCursor(tmpCursor);

    		if (tmpCursor != null)
    		{
	            tmpCursor.first();
	            int latitude = tmpCursor.getInt(tmpCursor.getColumnIndex(FotoDB.KEY_LAT));
	            int longitude = tmpCursor.getInt(tmpCursor.getColumnIndex(FotoDB.KEY_LONG));
	            if (latitude != 0){	            	
	            	MapController mc = mMapa.getController(); 
	            	mc.animateTo(new Point(latitude, longitude));
	            	mc.zoomTo(12);
	            }
	            else{
	            }
	        }
	    }
    
    	public void onNothingSelected(AdapterView arg0){
    	}
    };
    
    

    public boolean onKeyDown(int keyCode, KeyEvent keyEvent)
    {

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
        return super.onKeyDown(keyCode, keyEvent);
    }

    
  
    public class ImageAdapter extends CursorAdapter
    {

        private Context mContext;

        public ImageAdapter(Cursor c, Context context)
        {
            super(c, context);
            mContext = context;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {        	
        	mRowId = cursor.getLong(cursor.getColumnIndex(FotoDB.KEY_ROWID));
        	
        	String titulo = "Foto_"+mRowId.intValue()+".png";
            Bitmap bp = BitmapFactory.decodeFile("/data/data/com.cedetel.android.tigre/files/"+titulo);

            ImageView i = (ImageView) view;
            i.setImageBitmap(bp);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            i.setBackground(android.R.drawable.picture_frame);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            return new ImageView(mContext);
        }

        public float getAlpha(boolean focused, int offset)
        {
            return Math.max(0.2f, 1.0f - (0.2f * Math.abs(offset)));
        }

        public float getScale(boolean focused, int offset)
        {
            return Math.max(0, offset == 0 ? 1.0f : 0.6f);
        }
    }
	
}
