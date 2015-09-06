package com.cedetel.android.tigre.geisha;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.cedetel.android.tigre.R;

// CONFIGURACION DE GEISHA

public class GeishaConfig extends Activity implements
		RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

	private EditText mUser;
	private EditText mPass;
	
	private String nombre;
	private String passwd;
	private String seleccion;
	private String guardar;
	private String categoria;
	private String rutaColor;
	private String rutaGrosor;
	private String rutaTransp;

	private RadioGroup mRadioGroup1;
	private RadioGroup mRadioGroup2;
	private Spinner mSpinner1;
	private Spinner mSpinner2;
	private Spinner mSpinner3;
	private Spinner mSpinner4;

	private String[] aCategRuta = { "Ruta Privada", "Ruta Publica" };
	private String[] aColor = { "Rojo", "Verde", "Azul", "Amarillo", "Morado", "Negro" };
	private String[] aGrosor = { "----", "------" };
	private String[] aTransparencia = { "---", "-----", "-------" };

	private boolean esVisible = true;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.configuracion);
		loadUserConfig();

		mUser = (EditText) findViewById(R.id.user);
		if (nombre == null)
			mUser.setText("");
		else
			mUser.setText(nombre);
		mPass = (EditText) findViewById(R.id.password);
		mPass.setText(passwd);

		mUser.addTextChangedListener(watcher);
		mPass.addTextChangedListener(watcher);

		//mUser.setOnFocusChangeListener(userChangeListener);
		//mPass.setOnFocusChangeListener(userChangeListener);
		
		// Opciones para la seleccion de zona
		mRadioGroup1 = (RadioGroup) findViewById(R.id.zona);
		mRadioGroup1.setOnCheckedChangeListener(selectZona);
		if (seleccion == null){
			mRadioGroup1.check(R.id.autodetectar);
			seleccion = "Autodetectar";
		}else if (seleccion.equals("Autodetectar")){
			mRadioGroup1.check(R.id.autodetectar);
			seleccion = "Autodetectar";
		}else if (seleccion.equals("Manual")){
			mRadioGroup1.check(R.id.manual);
			seleccion = "Manual";
		}
		
		// Opciones para guardar las rutas
		mRadioGroup2 = (RadioGroup) findViewById(R.id.guardar);	
		mRadioGroup2.setOnCheckedChangeListener(selectGuardar);
		if (guardar == null){
			mRadioGroup2.check(R.id.intheend);
			guardar = "Al finalizar";
		}else if (guardar.equals("Al finalizar")){
			mRadioGroup2.check(R.id.intheend);
			guardar = "Al finalizar";
		}else if (guardar.equals("En tiempo real")){
			mRadioGroup2.check(R.id.realtime);
			guardar = "En tiempo real";
		}else if (guardar.equals("No guardar")){
			mRadioGroup2.check(R.id.nosave);
			guardar = "No guardar";
		}
		
		mSpinner1 = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, aCategRuta);
		adapter1
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner1.setAdapter(adapter1);
		mSpinner1.setOnItemSelectedListener(selectCateg);

		mSpinner2 = (Spinner) findViewById(R.id.spinner2);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, aColor);
		adapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner2.setAdapter(adapter2);
		mSpinner2.setOnItemSelectedListener(selectColor);

		mSpinner3 = (Spinner) findViewById(R.id.spinner3);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, aGrosor);
		adapter3
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner3.setAdapter(adapter3);
		mSpinner3.setOnItemSelectedListener(selectGrosor);

		mSpinner4 = (Spinner) findViewById(R.id.spinner4);
		mSpinner4.setEmptyView(new View(mSpinner4.getContext()));
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, aTransparencia);
		adapter4
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner4.setAdapter(adapter4);
		mSpinner4.setOnItemSelectedListener(selectTransp);

		if (nombre == null) {
			hacerInvisible(true);
		}

		// mSpinner4.invalidate();
		// mSpinner4.postInvalidate();

		// mSpinner4.setEmptyView(new View(mSpinner4.getContext()));
		// mSpinner4.setEnabled(false);

		Button botonConfig = (Button) findViewById(R.id.botonConfig);
		botonConfig.setOnClickListener(pulsarGuardar);
		Button botonBorrar = (Button) findViewById(R.id.botonBorrar);
		botonBorrar.setOnClickListener(pulsarBorrar);

	}

	private final boolean loadUserConfig() {
		SharedPreferences config = getSharedPreferences("RedirectData", 0);
		nombre = config.getString("usuario", null);
		passwd = config.getString("passwd", null);
		seleccion = config.getString("seleccion", null);
		guardar = config.getString("guardar", null);
		return true;
	}

	private OnClickListener pulsarGuardar = new OnClickListener() {
		public void onClick(View view) {

			if (mUser.getText().toString().equals("")) {
				SharedPreferences miConfig = getSharedPreferences(
						"RedirectData", 0);
				SharedPreferences.Editor editor = miConfig.edit();
				editor.putString("usuario", null);
				editor.putString("passwd", "");
				editor.putString("seleccion", seleccion);
				editor.putString("guardar", "");
				editor.putString("categoria", "");
				editor.putString("color", rutaColor);
				editor.putString("grosor", rutaGrosor);
				editor.putString("transparencia", rutaTransp);
				if (editor.commit()) {
					setResult(RESULT_OK);
				}
				finish();

			} else {

				boolean userOK = com.cedetel.android.tigre.webservice.GeishaWS
						.isUserCorrect(mUser.getText().toString(), mPass
								.getText().toString());

				if (userOK == true) {

					SharedPreferences miConfig = getSharedPreferences(
							"RedirectData", 0);
					SharedPreferences.Editor editor = miConfig.edit();
					editor.putString("usuario", mUser.getText().toString());
					editor.putString("passwd", mPass.getText().toString());
					editor.putString("seleccion", seleccion);
					editor.putString("guardar", guardar);
					editor.putString("categoria", categoria);
					editor.putString("color", rutaColor);
					editor.putString("grosor", rutaGrosor);
					editor.putString("transparencia", rutaTransp);
					if (editor.commit()) {
						setResult(RESULT_OK);
					}
					Intent intent4 = new Intent();
					intent4.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.geisha.GeishaMenu");
		        	startActivity(intent4);
					finish();

				} else {
					Toast.makeText(GeishaConfig.this, R.string.error_user,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	private OnClickListener pulsarBorrar = new OnClickListener(){
	public void onClick(View view) {
		SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
        SharedPreferences.Editor editor = miConfig.edit();
        editor.putString("usuario", null);
        editor.putString("passwd", null);
        editor.putString("seleccion", null);
        editor.putString("guardar", null);
        editor.putString("categoria", null);
        editor.putString("color", null);
        editor.putString("grosor", null);
        editor.putString("transparencia", null);
        if (editor.commit()) {
            setResult(RESULT_OK);
        }
		finish();
		}
	};
	
private OnCheckedChangeListener selectZona = new OnCheckedChangeListener() {
		
		public void onCheckedChanged(RadioGroup mRadioGroup1, int arg1) {
			int id = mRadioGroup1.getCheckedRadioButtonId();

			if (id == R.id.autodetectar) {
				seleccion = "Autodetectar";
			} else if (id == R.id.manual) {
				seleccion = "Manual";
			} else {
				seleccion = null;
			}

		}
	};

	private TextWatcher watcher = new TextWatcher() {

	
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}


		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			if(arg1<=2) //numero de letras de nombre o usuario
			{
				if (esVisible) {
					hacerInvisible(true);
				}
				return;
			}

			// Comprobamos si hay usuario y passwd.
			boolean userOK = com.cedetel.android.tigre.webservice.GeishaWS
					.isUserCorrect(mUser.getText().toString(), mPass.getText()
							.toString());

			if (userOK == true) {
				if (!esVisible) {
					hacerInvisible(false);
				}
			} else {
				if (esVisible) {
					hacerInvisible(true);
				}
			}
			return;
		}

	};

	/*
	 * private OnFocusChangeListener userChangeListener = new
	 * OnFocusChangeListener() {
	 * 
	 * @Override public void onFocusChanged(View arg0, boolean arg1) { //
	 * if(arg1) // sE supone que se activa el focus // else //arg1=false; se
	 * pierde el focus. if (!arg1) // arg1=false; se pierde el focus. { if
	 * (mUser.getText().toString().equals("") ||
	 * mPass.getText().toString().equals("")) { hacerInvisible(true); return; } //
	 * Comprobamos si hay usuario y passwd. boolean userOK =
	 * com.cedetel.android.tigre.webservice.GeishaWS
	 * .isUserCorrect(mUser.getText().toString(), mPass .getText().toString());
	 * 
	 * if (userOK == true) { hacerInvisible(false); }else{ hacerInvisible(true); }
	 * return; } } };
	 */
	
	private OnCheckedChangeListener selectGuardar = new OnCheckedChangeListener() {

		public void onCheckedChanged(RadioGroup mRadioGroup2, int arg1) {
			int id = mRadioGroup2.getCheckedRadioButtonId();

			if (id == R.id.nosave) {
				guardar = "No guardar";
			} else if (id == R.id.realtime) {
				guardar = "En tiempo real";
			} else if (id == R.id.intheend) {
				guardar = "Al finalizar";
			} else {
				guardar = null;
			}

		}
	};

	
	private OnItemSelectedListener selectCateg = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {

			position = mSpinner1.getSelectedItemPosition();
			categoria = mSpinner1.getSelectedItem().toString();

		}


		public void onNothingSelected(AdapterView arg0) {
			categoria = null;
		}
	};

	
	private OnItemSelectedListener selectColor = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {

			position = mSpinner2.getSelectedItemPosition();
			
			switch(position){
			case 0:
				rutaColor = "ff0000";
				break;
			case 1:	
				rutaColor = "00ff00";
				break;
			case 2:
				rutaColor = "0000ff";
				break;
			case 3:
				rutaColor = "ffff00";
				break;
			case 4:
				rutaColor = "A600A6";
				break;
			case 5:
				rutaColor = "000000";
				break;
			}
		}
		

		public void onNothingSelected(AdapterView arg0) {
			rutaColor = null;
		}
	};

	
	private OnItemSelectedListener selectGrosor = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {

			position = mSpinner3.getSelectedItemPosition();
			rutaGrosor = mSpinner3.getSelectedItem().toString();

		}


		public void onNothingSelected(AdapterView arg0) {
			rutaGrosor = null;
		}
	};

	
	private OnItemSelectedListener selectTransp = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View t, int position,
				long id) {

			position = mSpinner4.getSelectedItemPosition();
			rutaTransp = mSpinner4.getSelectedItem().toString();

		}

		public void onNothingSelected(AdapterView arg0) {
			rutaTransp = null;
		}
	};


	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

	}


	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		// TODO Auto-generated method stub

	}


	public void onNothingSelected(AdapterView parent) {
		// TODO Auto-generated method stub

	}

	private void hacerInvisible(boolean invisible) {
		TextView tv0 = (TextView) findViewById(R.id.tv0);
		TextView tv1 = (TextView) findViewById(R.id.TxtSpinner1);
		TextView tv2 = (TextView) findViewById(R.id.TxtSpinner2);
		TextView tv3 = (TextView) findViewById(R.id.TxtSpinner3);
		TextView tv4 = (TextView) findViewById(R.id.TxtSpinner4);
		TextView tv5 = (TextView) findViewById(R.id.opcionesGuardar);

		int visibilidad = 0;
		if (invisible) {
			visibilidad = RadioGroup.GONE;
			esVisible = false;
		} else {
			visibilidad = RadioGroup.VISIBLE;
			esVisible = true;
		}

		mRadioGroup2.setVisibility(visibilidad);
		mSpinner1.setVisibility(visibilidad);
		mSpinner2.setVisibility(visibilidad);
		mSpinner3.setVisibility(visibilidad);
		mSpinner4.setVisibility(visibilidad);

		tv0.setVisibility(visibilidad);
		tv1.setVisibility(visibilidad);
		tv2.setVisibility(visibilidad);
		tv3.setVisibility(visibilidad);
		tv4.setVisibility(visibilidad);
		tv5.setVisibility(visibilidad);

		return;
	}

}
