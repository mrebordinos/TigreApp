package com.cedetel.android.tigre;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.cedetel.android.tigre.db.PuntoDB;
import com.google.android.maps.Point;

// 		GUARDA LOS PUNTOS DE INTERES: EL TITULO, COMENTARIOS, LA RUTA, LA POSICION Y LA HORA

public class PuntoInteres extends Activity{
	
	private EditText mPtitle;
	private EditText mBody;
	private RadioGroup iconoRadioGroup;
	private RadioGroup iconoRadioGroup2;
	public PuntoDB mDbHelper;
	private Long mRowId;	
	private Long mRutaId;

	private String locProviderName = "gps";
	//private String nombrePunto;
	private String iconoPunto = "chincheta roja";

	public static List<Location> interes = new ArrayList<Location>();
	public static List<String> inombres = new ArrayList<String>();
	public static List<String> iiconos = new ArrayList<String>();
    
	public Location myLocation;
	//public Point mpoint;
	
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        loadRuta();
        
        mDbHelper = new PuntoDB(this);
        mDbHelper.open();
        setContentView(R.layout.interes);
        
        mPtitle = (EditText) findViewById(R.id.titulo);
        mBody = (EditText) findViewById(R.id.texto);
       
        Button botonG = (Button)findViewById(R.id.botonGuardar);
        botonG.setOnClickListener(pulsarGuardar);
        
        mRowId = icicle != null ? icicle.getLong(PuntoDB.KEY_ROWID) : null;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(PuntoDB.KEY_ROWID) : null;
        }
        
        iconoRadioGroup = (RadioGroup) findViewById(R.id.iconoRadio);
		iconoRadioGroup.setOnCheckedChangeListener(selectIcono);
		
		iconoRadioGroup2 = (RadioGroup) findViewById(R.id.iconoRadio2);
		iconoRadioGroup2.setOnCheckedChangeListener(selectIcono);

        populateFields();
        
	}

	
	 private final boolean loadRuta(){
			SharedPreferences config = getSharedPreferences("RedirectData", 0);
			mRutaId = config.getLong("idRuta", 0);
			return true;
	 }
	 
	private OnClickListener pulsarGuardar = new OnClickListener(){
		public void onClick(View v) {			
	        saveState();
			loadRuta();
            finish();            
		}        		
	};

	private OnCheckedChangeListener selectIcono = new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup iconoRadioGroup, int arg1) {
			int id = iconoRadioGroup.getCheckedRadioButtonId();
			if (id == R.id.chinroja) {
				iconoPunto = "chincheta roja";
			}else if (id == R.id.chinazul) {
				iconoPunto = "chincheta azul";
			} else if (id == R.id.marcaa) {
				iconoPunto = "marca A";
			} else if (id == R.id.marcab){
				iconoPunto = "marca B";
			}else if (id == R.id.arbol){
				iconoPunto = "arbol";
			}else if (id == R.id.flag){
				iconoPunto = "flag";
			}
		}
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		}
	};
	
	
	private void populateFields() {
    	if (mRowId != null) {
            Cursor ipunto = mDbHelper.fetchIpunto(mRowId);
            startManagingCursor(ipunto);
            mPtitle.setText(ipunto.getString(ipunto.getColumnIndex(PuntoDB.KEY_TITLE)));
            mBody.setText(ipunto.getString(ipunto.getColumnIndex(PuntoDB.KEY_BODY)));
            String icono = ipunto.getString(ipunto.getColumnIndex(PuntoDB.KEY_ICON));
            
            if (icono.equals("chincheta roja")){
            	iconoRadioGroup.check(R.id.chinroja);
            }else if (icono.equals("chincheta azul")){
            	iconoRadioGroup.check(R.id.chinazul);
            }else if (icono.equals("marca A")){
            	iconoRadioGroup.check(R.id.marcaa);
            }else if (icono.equals("marca B")){
            	iconoRadioGroup2.check(R.id.marcab);
            }else if (icono.equals("arbol")){
            	iconoRadioGroup2.check(R.id.arbol);
            }else {
            	iconoRadioGroup2.check(R.id.flag);
            }
    	}
    }
	
	protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
	    	super.onActivityResult(requestCode, resultCode, data, extras);
	}
	 
//	@Override
//    protected void onFreeze(Bundle outState) {
//        super.onFreeze(outState);
//        outState.putLong(PuntoDB.KEY_ROWID, mRowId);
//    }
//    
//    @Override
//    protected void onPause() {
//        super.onPause();
//        saveState();
//    }
//    
//    @Override
//    protected void onResume() {
//        super.onResume();
//        populateFields();
//    }
    
    private void saveState() {
        String title = mPtitle.getText().toString();
        String body = mBody.getText().toString();
        inombres.add(title);
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        myLocation = locationManager.getCurrentLocation(locProviderName);          
        interes.add(myLocation);
        Double lat = myLocation.getLatitude()*1E6;
    	Double lng = myLocation.getLongitude()*1E6;
    	Double alt = myLocation.getAltitude()*1E6; 
    	int latitud = lat.intValue();
    	int longitud = lng.intValue();
    	int altitud = alt.intValue();
    	//mpoint = new Point(latitud, longitud);
    	long date = myLocation.getTime();
        Long route = mRutaId;
        String icono = iconoPunto;
        iiconos.add(icono);
        
        if (mRowId == null) {
            long id = mDbHelper.createIpunto(title, body, latitud, longitud, altitud, date, icono, route);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateIpuntos(mRowId, title, body, icono);
        }
    }
	
}