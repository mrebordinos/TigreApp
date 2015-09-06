package com.cedetel.android.tigre;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.ServiceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

// 	OPCIONES DE CONFIGURACION DE TIGRE

public class OpcionesTigre extends Activity{
	
	private Spinner spinnerColorPunto;
	private Spinner spinnerColorLinea;
	private RadioGroup tiempoRadioGroup;
	private RadioGroup distRadioGroup;
	
	private String rutaColor;
	private String puntoColor;
	private String servicio;
	private long tiempoGPS;
	private long minDistancia;
	private CheckBox girar;
	private String[] color = { "Rojo", "Verde", "Azul", "Amarillo", "Morado", "Negro" };
		
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.opciones_tigre);
		
		// Carga la configuracion del usuario 
		loadTigreConfig();
		
		// 
		girar = (CheckBox) findViewById(R.id.checkGirar);
		girar.setOnCheckedChangeListener(checkCh);
		
		// RadioGroup para seleccionar el tiempo de actualizacion del GPS
		tiempoRadioGroup = (RadioGroup) findViewById(R.id.tiempo);
		tiempoRadioGroup.setOnCheckedChangeListener(selectTiempo);
		if (tiempoGPS == 10000 ){
			tiempoRadioGroup.check(R.id.tiempo10);
			tiempoGPS = 10000;
		}else if (tiempoGPS == 30000){
			tiempoRadioGroup.check(R.id.tiempo30);
			tiempoGPS = 30000;
		}else if (tiempoGPS == 60000){
			tiempoRadioGroup.check(R.id.tiempo60);
			tiempoGPS = 60000;
		}else if (tiempoGPS == 300000){
			tiempoRadioGroup.check(R.id.tiempo300);
			tiempoGPS = 300000;
		}
		
		// RadioGroup para seleccionar la distancia minima para actualizar el GPS
		distRadioGroup = (RadioGroup) findViewById(R.id.distancia);
		distRadioGroup.setOnCheckedChangeListener(selectDistancia);
		if (minDistancia == 50 ){
			distRadioGroup.check(R.id.distancia50);
			minDistancia = 50;
		}else if (minDistancia == 100){
			distRadioGroup.check(R.id.distancia100);
			minDistancia = 100;
		}else if (minDistancia == 200){
			distRadioGroup.check(R.id.distancia200);
			minDistancia = 200;
		}else if (minDistancia == 500){
			distRadioGroup.check(R.id.distancia500);
			minDistancia = 500;
		}
		
		// RadioGroup para seleccionar el icono para se�alizar los puntos de interes
		
		
		// Spinner para seleccionar el color de los puntos de la ruta
		spinnerColorPunto = (Spinner) findViewById(R.id.spinnerColorPunto);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, color);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerColorPunto.setAdapter(adapter1);
		if(puntoColor.equals("ff0000")){
			spinnerColorPunto.setSelection(0);
		}else if(puntoColor.equals("00ff00")){
			spinnerColorPunto.setSelection(1);
		}else if(puntoColor.equals("0000ff")){
			spinnerColorPunto.setSelection(2);
		}else if(puntoColor.equals("ffff00")){
			spinnerColorPunto.setSelection(3);
		}else if(puntoColor.equals("A600A6")){
			spinnerColorPunto.setSelection(4);
		}else{
			spinnerColorPunto.setSelection(5);
		}
		spinnerColorPunto.setOnItemSelectedListener(selectColorPunto);
		
		// Spinner para seleccionar el color de la linea que une los puntos de la ruta
		spinnerColorLinea = (Spinner) findViewById(R.id.spinnerColorLinea);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, color);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerColorLinea.setAdapter(adapter2);
		if(rutaColor.equals("Rojo")){
			spinnerColorLinea.setSelection(0);
		}else if(rutaColor.equals("Verde")){
			spinnerColorLinea.setSelection(1);
		}else if(rutaColor.equals("Azul")){
			spinnerColorLinea.setSelection(2);
		}else if(rutaColor.equals("Amarillo")){
			spinnerColorLinea.setSelection(3);
		}else if(rutaColor.equals("Morado")){
			spinnerColorLinea.setSelection(4);
		}else {
			spinnerColorLinea.setSelection(5);
		}
		spinnerColorLinea.setOnItemSelectedListener(selectColorLinea);
		
		// Boton para guardar la configuracion
		Button botonGuardar = (Button)findViewById(R.id.botonGuardarTigre);
		botonGuardar.setOnClickListener(pulsar);
		
		// Boton para borrar la configuracion
		Button botonBorrar = (Button)findViewById(R.id.botonBorrarTigre);
		botonBorrar.setOnClickListener(pulsarBorrar); 
		
		Button botonReset = (Button)findViewById(R.id.botonReset);
		botonReset.setOnClickListener(reset);

	}
	
	
	private android.widget.CompoundButton.OnCheckedChangeListener checkCh = new android.widget.CompoundButton.OnCheckedChangeListener(){

		public void onCheckedChanged(CompoundButton arg0, boolean girado) {
			if (girado == true){
				try {
					android.view.IWindowManager windowService = android.view.IWindowManager.Stub.asInterface(ServiceManager.getService("window")); 
					windowService.setOrientation(1);
					girar.setChecked(girado);
				} catch (DeadObjectException e) {
					e.printStackTrace();
				}
			} else if (girado == false){
				try {
					android.view.IWindowManager windowService = android.view.IWindowManager.Stub.asInterface(ServiceManager.getService("window")); 
					windowService.setOrientation(0);
					girar.setChecked(girado);
				} catch (DeadObjectException e) {
					e.printStackTrace();
				}
			}
		}		
	};
	
	
	private OnCheckedChangeListener selectTiempo = new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup mRadioGroup, int arg1) {
			int id = mRadioGroup.getCheckedRadioButtonId();
			if (id == R.id.tiempo10) {
				tiempoGPS = 10000;
			}else if (id == R.id.tiempo30) {
				tiempoGPS = 30000;
			} else if (id == R.id.tiempo60) {
				tiempoGPS = 60000;
			} else if (id == R.id.tiempo300){
				tiempoGPS = 300000;
			} else {
				tiempoGPS = 100000;
			}
		}
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		}
	};
	
	private OnCheckedChangeListener selectDistancia = new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup mRadioGroup, int arg1) {
			int id = mRadioGroup.getCheckedRadioButtonId();
			if (id == R.id.distancia50) {
				minDistancia = 50;
			}else if (id == R.id.distancia100) {
				minDistancia = 100;
			} else if (id == R.id.distancia200) {
				minDistancia = 200;
			} else if (id == R.id.distancia500){
				minDistancia = 500;
			} 
		}
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		}
	};
	
	
	
	
	private OnItemSelectedListener selectColorPunto = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position, long id) {
			position = spinnerColorPunto.getSelectedItemPosition();		
//			puntoColor = spinnerColorPunto.getSelectedItem().toString();
			switch(position){
				case 0:
					puntoColor = "ff0000"; // Rojo
					break;
				case 1:	
					puntoColor = "00ff00"; // Verde
					break;
				case 2:
					puntoColor = "0000ff"; // Azul
					break;
				case 3:
					puntoColor = "ffff00"; // Amarillo
					break;
				case 4:
					puntoColor = "A600A6"; // Morado
					break;
				case 5:
					puntoColor = "000000"; // Negro
					break;
			
			}
		}

		public void onNothingSelected(AdapterView arg0) {
			puntoColor = null;
		}
	};
	
	
	private OnItemSelectedListener selectColorLinea = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position, long id) {
			position = spinnerColorLinea.getSelectedItemPosition();
			rutaColor = spinnerColorLinea.getSelectedItem().toString();
		}
		public void onNothingSelected(AdapterView arg0) {
			rutaColor = null;
		}
	};
	
	
	
	private OnClickListener pulsar = new OnClickListener(){
		public void onClick(View view) {
			SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
            SharedPreferences.Editor editor = miConfig.edit();
            editor.putString("lineaColor", rutaColor);
            editor.putString("puntoColor", puntoColor);
            editor.putLong("tiempoGPS", tiempoGPS);
            editor.putLong("minDistancia", minDistancia);
            if (editor.commit()) {
                setResult(RESULT_OK);
            }
            Intent intent = new Intent();
	 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
	 		startActivity(intent);
	 		finish();
		}
	};
	
	private OnClickListener pulsarBorrar = new OnClickListener(){
		public void onClick(View view) {
			SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
	        SharedPreferences.Editor editor = miConfig.edit();
	        editor.putString("lineaColor", "Rojo");
	        editor.putString("puntoColor", "00ff00");
	        editor.putString("icono", "chincheta roja");
	        editor.putLong("tiempoGPS", 10000);
	        editor.putLong("minDistancia", 500);
	        if (editor.commit()) {
	            setResult(RESULT_OK);
	        }
	        Intent intent = new Intent();
	 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
	 		startActivity(intent);
	 		finish();
			}
		};
	
	private OnClickListener reset = new OnClickListener(){
		public void onClick(View view) {
			servicio = "no";
    		SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
			SharedPreferences.Editor editor = miConfig.edit();
			editor.putString("status", servicio);
			if (editor.commit()) {
				setResult(RESULT_OK);
			}
			Intent intent = new Intent();
	 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
	 		startActivity(intent);
	 		finish();
		}
	};
	
	
	private final boolean loadTigreConfig() {
		SharedPreferences config = getSharedPreferences("RedirectData", 0);
			rutaColor = config.getString("lineaColor", "Rojo");
			puntoColor = config.getString("puntoColor", "00ff00");			
			tiempoGPS = config.getLong("tiempoGPS", 1000);
			minDistancia = config.getLong("minDistancia", 300);
		return true;
	}
	
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	 {
		 switch (keyCode)
		 {
		 	case KeyEvent.KEYCODE_BACK:
		 		Intent intent = new Intent();
		 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
		 		startActivity(intent);
		 		finish();
		 		break;
		 }
		 return false;
	 }
	
	
}