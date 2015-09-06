package com.cedetel.android.tigre.camara;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cedetel.android.tigre.R;

public class MenuCamara extends ListActivity{
	
	private String servicio;
	
	 @Override
	 public void onCreate(Bundle icicle) {
		 super.onCreate(icicle);
		
		 setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myMenu));
		 loadConfig();
	 }
	    
	 private String[] myMenu = {
			 "Nueva Foto", "Mis Fotos", "Salir"};
	    	
	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
		 getListView().obtainItemId(position);
		 if ( position == 0){
			 loadConfig();
			 if (servicio.equals("no")) {
				 new AlertDialog.Builder(MenuCamara.this)
				 .setTitle(R.string.dialogoFoto)
				 .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					 public void onClick(DialogInterface dialog, int whichButton) {
						 
						 Intent intent = new Intent();
						 intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.NuevaFoto");
						 startActivity(intent);	        				
					 }
				 })
				 .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					 public void onClick(DialogInterface dialog, int whichButton) {	        					        			
						//finish();
					 }
				 })
				 .show();
			 }else {
				 Intent intent1 = new Intent();
				 intent1.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.NuevaFoto");
				 startActivity(intent1);
			 }
			 
		 }else if (position == 1) {
			 Intent intent2 = new Intent();
			 intent2.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MisFotos");
			 startActivity(intent2);
		 }else {
			 this.finish();
		 }
	            
	 }
	    	
	   
	 private final boolean loadConfig() {
		 SharedPreferences config = getSharedPreferences("RedirectData", 0);
		 servicio = config.getString("status", "no");
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
