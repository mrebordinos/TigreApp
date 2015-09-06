package com.cedetel.android.tigre.geisha;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedetel.android.tigre.R;

// MENU DEL GEISHA

public class GeishaMenu extends ListActivity {

	private String nombre;
	private String seleccion;
	private String guardar;
	private String categoria;
	private String rutaColor;
	private String rutaGrosor;
	private String rutaTransp;
	private String estado;

	private String zona;
	private String servicios;
	private int distancia;

	private boolean activado;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		loadUserConfig();
		if (activado == true) {
			estado = " Activado";
		} else if (activado == false) {
			estado = " Desactivado";
		}
		setListAdapter(new SpeechListAdapter(this));
	}

	private final boolean loadUserConfig() {
		SharedPreferences configuracion = getSharedPreferences("RedirectData",
				0);
		nombre = configuracion.getString("usuario", "Acceso An�nimo");
		if (nombre.equals("")) {
			nombre = "Acceso An�nimo";
		}
		seleccion = configuracion.getString("seleccion", null);
		guardar = configuracion.getString("guardar", null);
		categoria = configuracion.getString("categoria", null);
		rutaColor = configuracion.getString("color", null);
		rutaGrosor = configuracion.getString("grosor", null);
		rutaTransp = configuracion.getString("transparencia", null);
		activado = configuracion.getBoolean("estado", false);

		zona = configuracion.getString("zona", null);
		servicios = configuracion.getString("servicios", null);
		distancia = configuracion.getInt("distancia", 1000);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		((SpeechListAdapter) getListAdapter()).toggle(position);
		// Si est� desactivado y es la posicion 1 queremos que aparezca lo de
		// Salir y funcione como tal
		if (activado == false && position == 1) {
			position = 3;
		}
		if (position == 0) {

		} else if (position == 1) {
			Intent intent2 = new Intent();
			intent2.setClassName("com.cedetel.android.tigre",
					"com.cedetel.android.tigre.geisha.GeishaConfig");
			startActivity(intent2);
			finish();
		} else if (position == 2) {
			if (seleccion==null){
				Toast.makeText(GeishaMenu.this, R.string.error_seleccion, Toast.LENGTH_LONG).show();
			}else {
			Intent intent3 = new Intent();
			intent3.setClassName("com.cedetel.android.tigre",
					"com.cedetel.android.tigre.geisha.GeishaConsulta");
			startActivity(intent3);
			finish();
			}
		} else {
			SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
			SharedPreferences.Editor editor = miConfig.edit();
			editor.putBoolean("estado", activado);
			if (editor.commit()) {
				setResult(RESULT_OK);
			}
			Intent intent = new Intent();
	 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
	 		startActivity(intent);
	 		finish();
		}
	}

	private class SpeechListAdapter extends BaseAdapter {
		public SpeechListAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			if (activado)
				return mTitles.length;
			else
				return 2;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			SpeechView sv;
			// Si est� desactivado y es la posicion 1 queremos que aparezca lo
			// de Salir y funcione como tal
			if (activado == false && position == 1) {
				position = 3;
			}
			String tmpString = "\n	Usuario					----->	" + nombre + "\n"
					+ "\n	Seleccion de zona	----->	" + seleccion + "\n";

			if (convertView == null) {
				if (position == 1 && nombre.equals("Acceso An�nimo")) {
					sv = new SpeechView(mContext, mTitles[position], tmpString,
							mExpanded[position]);
				} else {
					sv = new SpeechView(mContext, mTitles[position],
							mDialogue[position], mExpanded[position]);
				}
			} else {
				sv = (SpeechView) convertView;
				sv.setTitle(mTitles[position]);
				if (position == 1 && nombre.equals("Acceso An�nimo")) {
					sv.setDialogue(tmpString);
				} else {
					sv.setDialogue(mDialogue[position]);
				}
				sv.setExpanded(mExpanded[position]);
			}
			return sv;
		}

		public void toggle(int position) {
			// mExpanded[position] = !mExpanded[position];
			if (position == 0) {
				activado = !activado;
				if (activado) {
					mTitles[0] = " Activado";
				} else {
					mTitles[0] = " Desactivado";
				}
				notifyDataSetChanged();
			}
		}

		private Context mContext;

		private String[] mTitles = { estado, " Configuracion",
				" Consultar Informacion Geografica", " Salir" };
		private String[] mDialogue = {
				"",
				"\n	Usuario					----->	" + nombre + "\n"
						+ "\n	Seleccion de zona	----->	" + seleccion + "\n"
						+ "\n	Guardar	ruta			-----> " + guardar + "\n"
						+ "\n	Categoria de la ruta	-----> " + categoria + "\n",
				"\n	Zona			-----> " + zona + "\n" + "\n	Servicios 	-----> "
						+ servicios + "\n" + "\n	Distancia 	-----> "
						+ distancia + " m. \n", "" };
		private boolean[] mExpanded = { false, true, true, false };
	}

	private class SpeechView extends LinearLayout {
		public SpeechView(Context context, String title, String dialogue,
				boolean expanded) {
			super(context);
			this.setOrientation(VERTICAL);

			mTitle = new TextView(context);
			mTitle.setText(title);
			mTitle.setTextSize(20);
			mTitle.setGravity(0x11);
			// mTitle.setTextColor(11);
			addView(mTitle, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			mDialogue = new TextView(context);
			mDialogue.setText(dialogue);
			mDialogue.setTextSize(15);
			mDialogue.setGravity(0x11);
			// mDialogue.setTextColor(66);
			addView(mDialogue, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			mDialogue.setVisibility(expanded ? VISIBLE : GONE);
		}

		public void setTitle(String title) {
			mTitle.setText(title);
		}

		public void setDialogue(String words) {
			mDialogue.setText(words);
		}

		public void setExpanded(boolean expanded) {
			mDialogue.setVisibility(expanded ? VISIBLE : GONE);
		}

		private TextView mTitle;
		private TextView mDialogue;
	}
	
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	 {
		 switch (keyCode)
		 {
		 	case KeyEvent.KEYCODE_BACK:
		 		SharedPreferences miConfig = getSharedPreferences("RedirectData", 0);
				SharedPreferences.Editor editor = miConfig.edit();
				editor.putBoolean("estado", activado);
				if (editor.commit()) {
					setResult(RESULT_OK);
				}
				Intent intent = new Intent();
		 		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
		 		startActivity(intent);
		 		finish();
		 		break;
		 }
		 return false;
	 }
	
}