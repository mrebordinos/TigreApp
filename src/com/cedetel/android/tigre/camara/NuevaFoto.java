package com.cedetel.android.tigre.camara;

import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.FotoDB;
import com.cedetel.android.tigre.db.RutaDB;

//	GUARDA LAS FOTOS: EL TITULO, ETIQUETAS, LA RUTA, LA POSICION Y LA HORA

public class NuevaFoto extends Activity {
	
	private static final int CAMERA_REQUEST = 0;
	private EditText mTitle;
	private EditText mTag;
	private Long mRowId;
	private TextView mTitleRoute;
	
	private FotoDB mDbHelper;
	private RutaDB mDbRoute;
	
	private String nombreRuta;
	private long mRutaId;
	private String servicio;
	
	private Bitmap pic;
	
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
                
        mDbHelper = new FotoDB(this);
        mDbHelper.open();
        
        mDbRoute = new RutaDB(this);
        mDbRoute.open();
        
        setContentView(R.layout.nueva_foto);
        
        mTitle = (EditText) findViewById(R.id.Ptitle);
        mTag = (EditText) findViewById(R.id.Ptag);
        mTitleRoute = (TextView)findViewById(R.id.Rtitle);
        
        
        Button botonFoto = (Button)findViewById(R.id.botonFoto);
        botonFoto.setOnClickListener(pulsarB);  
        
        Button botonActualizar = (Button)findViewById(R.id.botonFotoA);
        botonActualizar.setOnClickListener(pulsarA);
        
        mRowId = icicle != null ? icicle.getLong(FotoDB.KEY_ROWID) : null;
        
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(FotoDB.KEY_ROWID) : null;           
        }
        
        if (icicle == null && mRowId == null){
        	Intent camera = new Intent(this, Camara.class);
            startSubActivity(camera, CAMERA_REQUEST);   	
        }
        
        loadState();
        if (servicio.equals("no") && mRowId==null){
        	mRutaId = 0;
        	nombreRuta = "NO ESTA EN NINGUNA RUTA";
        	mTitleRoute.setText(nombreRuta);
        }else if (servicio.equals("si")){
        	loadConfig();
        	mTitleRoute.setText(nombreRuta);
        }
        
        if (mRowId != null){
        	botonFoto.setVisibility(View.GONE); 	
        }else {
        	botonActualizar.setVisibility(View.GONE);
        }
        populateFields();
        
    }
	
	
	private final boolean loadConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);
		nombreRuta = configuracion.getString("titulo", null);
		mRutaId = configuracion.getLong("idRuta", 0);
		return true;
 	}
	
	private final boolean loadState(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);
		servicio = configuracion.getString("status", "no");
		return true;
 	}
	
	protected void onActivityResult(int requestCode, int resultCode,
            String data, Bundle extras)
    {
		if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
            	case CAMERA_REQUEST:

                pic = (Bitmap) extras.getParcelable("bitmap");
               
                    // Get the view in order to automatically update the picture
//                    mImage = (ImageView) this.findViewById(R.id.thumb);
//                    mImage.setImageBitmap(pic);
//                    mImage.invalidate();
                            
                    }         
        }
		else if (resultCode == Activity.RESULT_CANCELED)
        {
            finish();
        }else if (resultCode == Activity.MODE_WORLD_WRITEABLE){
        	populateFields();
        }
    }
	
	
	private OnClickListener pulsarB = new OnClickListener(){
		public void onClick(View view) {			
            if (mTitle.getText().toString().equals("")){
        		Toast.makeText(NuevaFoto.this, R.string.ponTituloFoto, Toast.LENGTH_SHORT).show();
            }
            else {
    			saveState();
    			try
                {                   
                	String tituloFoto = "Foto_"+mRowId+".png";
                    OutputStream outStream = NuevaFoto.this.openFileOutput(tituloFoto, Context.MODE_WORLD_WRITEABLE);
                    pic.compress(CompressFormat.PNG, 100, outStream);
                    outStream.close();
                } catch (Exception e){
                }
	            finish();            
            }
        }
	};
	
	
	private OnClickListener pulsarA = new OnClickListener(){
		public void onClick(View view) {			
            if (mTitle.getText().toString().equals("")){
        		Toast.makeText(NuevaFoto.this, R.string.ponTituloFoto, Toast.LENGTH_SHORT).show();
            }
            else {
    			saveState();    			
	            finish();            
            }
        }
	};
	
	
	private void populateFields() {			
    	if (mRowId != null) {
            Cursor photo = mDbHelper.fetchPhoto(mRowId);
            startManagingCursor(photo);
            mTitle.setText(photo.getString(photo.getColumnIndex(FotoDB.KEY_TITLE)));
            mTag.setText(photo.getString(photo.getColumnIndex(FotoDB.KEY_TAG)));
            Long id = photo.getLong(photo.getColumnIndex(FotoDB.KEY_ROUTE));
            if (id.intValue()== 0) {
            	mTitleRoute.setText("NO ESTA EN NINGUNA RUTA");
            }else {
            	Cursor ruta = mDbRoute.fetchRoute(photo.getLong(photo.getColumnIndex(FotoDB.KEY_ROUTE)));
            	startManagingCursor(ruta);
            	mTitleRoute.setText(ruta.getString(ruta.getColumnIndex(RutaDB.KEY_TITLE)));
            }
    	}
    }
	
    
    private void saveState() {
        String title = mTitle.getText().toString();
        String tag = mTag.getText().toString();
        
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Location myLocation = locationManager.getCurrentLocation("gps");           		
        Double lat = myLocation.getLatitude() * 1E6;
    	Double lng = myLocation.getLongitude() * 1E6;
    	int latitud = lat.intValue();
    	int longitud = lng.intValue();
    	long date = myLocation.getTime();
    	
        if (mRowId == null) {
            long id = mDbHelper.createPhoto(title, tag, latitud, longitud, date, mRutaId);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updatePhoto(mRowId, title, tag);
        }
    }
    
    
}