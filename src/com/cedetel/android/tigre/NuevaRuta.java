package com.cedetel.android.tigre;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cedetel.android.tigre.db.RutaDB;
import com.cedetel.android.tigre.geisha.functiona.ServicioEnRegiones;
import com.cedetel.android.tigre.geisha.gis.POI;

// 		AÑADE NOMBRE, COMENTARIOS Y ETIQUETAS A LA RUTA QUE VAMOS A CREAR 

public class NuevaRuta extends Activity {
	
	private EditText mTitle;
	private EditText mBody;
	private EditText mTag;
	private TextView mText;
	private Spinner mSpinner;
	private Long mRowId;
	private RutaDB mDbHelper;
	private String mTituloRuta;
	private String rutaColor;
	private String puntoColor;
	private String guardar;
	private int servicioRuta;
	int[] idServicios;
	
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        loadConfig();
        mDbHelper = new RutaDB(this);
        mDbHelper.open();
        
        setContentView(R.layout.nueva_ruta);
        
        mTitle = (EditText) findViewById(R.id.title);
        mBody = (EditText) findViewById(R.id.body);
        mTag = (EditText) findViewById(R.id.tag);
        mText = (TextView) findViewById(R.id.textServicios);
        mSpinner = (Spinner)findViewById(R.id.mspinner);
          
        Button botonRuta = (Button)findViewById(R.id.botonRuta);
        botonRuta.setOnClickListener(pulsarB);  
        
        Button botonActualizar = (Button)findViewById(R.id.botonActualizar);
        botonActualizar.setOnClickListener(pulsarA);
        
     
        
        if (guardar.equals("No guardar")) {
        	mText.setVisibility(View.GONE);
        	mSpinner.setVisibility(View.GONE);
        }else{        	
        	LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        	Location myLocation = lm.getCurrentLocation("gps");

        	Vector<POI> nearestZone = com.cedetel.android.tigre.webservice.GeishaWS
						.searchNearestPoi(myLocation.getLatitude(), myLocation.getLongitude(), "cedetel", "cedetel", 1);
        	int id_zone = nearestZone.get(0).getId_region();
       
        	Vector<ServicioEnRegiones> servicios = com.cedetel.android.tigre.webservice.GeishaWS
        					.getAvailableServicesByZone(id_zone, null, null);
        	String[] aServicios = new String[servicios.size()];
        	idServicios = new int[servicios.size()];
			for (int i=0; i<servicios.size(); i++){
				ServicioEnRegiones ser = servicios.get(i);
				aServicios[i] = ser.getNombre_servicio();
				idServicios[i] = ser.getIdServicio();
			}
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aServicios);
        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	mSpinner.setAdapter(adapter);
        	mSpinner.setOnItemSelectedListener(selectServicios);
        }
        
		mRowId = icicle != null ? icicle.getLong(RutaDB.KEY_ROWID) : null;
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(RutaDB.KEY_ROWID) : null;
        }
        
        if (mRowId != null){
        	botonRuta.setVisibility(View.GONE); 
        	mText.setVisibility(View.GONE);
        	mSpinner.setVisibility(View.GONE);
        }else {
        	botonActualizar.setVisibility(View.GONE);	
        } 
        
        populateFields();

    }
	
	private OnClickListener pulsarB = new OnClickListener(){
		public void onClick(View view) {
			
            if (mTitle.getText().toString().equals("")){
        		Toast.makeText(NuevaRuta.this, R.string.ponTitulo, Toast.LENGTH_SHORT).show();
            }
            else {
    			saveState();
	            SharedPreferences tituloRuta = getSharedPreferences("RedirectData", 0);
	            SharedPreferences.Editor editor = tituloRuta.edit();
	            editor.putString("titulo", mTitle.getText().toString());
	            editor.putString("tags", mTag.getText().toString());
	            editor.putInt("servicioRuta", servicioRuta);
	            editor.putLong("idRuta", mRowId);
	            if (editor.commit()) {
	                setResult(RESULT_OK);
	            }
	            finish();            
	            Intent inten = new Intent ();
				inten.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MapaRuta");
				startActivity(inten);
            }
        }
	};
	
	private OnClickListener pulsarA = new OnClickListener(){
		public void onClick(View view) {
			
            if (mTitle.getText().toString().equals("")){
        		Toast.makeText(NuevaRuta.this, R.string.ponTitulo, Toast.LENGTH_SHORT).show();
            }
            else {
    			saveState();	           
	            finish();            	           
            }
        }
	};
	
	private OnItemSelectedListener selectServicios = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {
			position = mSpinner.getSelectedItemPosition();
			servicioRuta = idServicios[position];
		
			//servicioRuta = mSpinner.getSelectedItem().toString();		
			
		}

		public void onNothingSelected(AdapterView arg0) {
			
		}
	};
	
	private final boolean loadTitulo(){
        SharedPreferences tituloRuta = getSharedPreferences("RedirectData", 0);
        mTituloRuta = tituloRuta.getString("titulo", null);
        if (mTituloRuta != null) {
            mTitle.setText(mTituloRuta);
            return true;
        }
        return false;
    }
	
	private final boolean loadConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);
		rutaColor = configuracion.getString("lineaColor", null);
		puntoColor = configuracion.getString("puntoColor", null);
		guardar = configuracion.getString("guardar", null);
		return true;
 	}
	
	private void populateFields() {
    	if (mRowId != null) {
            Cursor route = mDbHelper.fetchRoute(mRowId);
            startManagingCursor(route);
            mTitle.setText(route.getString(route.getColumnIndex(RutaDB.KEY_TITLE)));
            mBody.setText(route.getString(route.getColumnIndex(RutaDB.KEY_BODY)));
            mTag.setText(route.getString(route.getColumnIndex(RutaDB.KEY_TAG)));
            
    	}
    }
	
//	@Override
//    protected void onFreeze(Bundle outState) {
//        super.onFreeze(outState);
//        outState.putLong(RutaDB.KEY_ROWID, mRowId);
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
        String title = mTitle.getText().toString();
        String body = mBody.getText().toString();
        String tag = mTag.getText().toString();;
        String color = puntoColor;
        if (mRowId == null) {
            long id = mDbHelper.createRoute(title, body, tag, color);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateRoute(mRowId, title, body, tag);
        }
    }
}
