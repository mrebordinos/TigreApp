package com.cedetel.android.tigre;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.cedetel.android.tigre.db.GeishaDB;
import com.cedetel.android.tigre.db.RutaDB;
import com.cedetel.android.tigre.geisha.gis.AgrupacionBaseInfo;

// 	LISTA DE LAS RUTAS GUARDADAS EN TIGRE 
//								Y TAMBIEN LAS DE GEISHA SI ESTAMOS CONECTADOS

public class MisRutas extends Activity{
	
    private static final int BORRAR_RUTA = Menu.FIRST;
    private static final int IR_PUNTOS = Menu.FIRST + 1;
    private static final int IR_FOTOS = Menu.FIRST + 2;
    private static final int DIBUJAR_TODAS = Menu.FIRST + 3; 
    
    private RutaDB mDbHelper;
    private GeishaDB mDbGhelper;
    
    private boolean activado;
	private String nombre;
	private String passwd;
	
	private TextView titleRutasGeisha;
	private ListView tigreList;
	private ListView geishaList;

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mis_rutas);
        loadUserConfig();
               
        titleRutasGeisha = (TextView) findViewById(R.id.rutasGeisha);
        tigreList = (ListView) findViewById(R.id.tigrelist);
        geishaList = (ListView) findViewById(R.id.geishalist);
        	
        mDbGhelper = new GeishaDB(this);
        mDbGhelper.open();        
        mDbGhelper.deleteAllRoutes();
        //Obtenemos todas las agrupaciones creadas por el usuario logueado          
        Vector<AgrupacionBaseInfo> vAbi= com.cedetel.android.tigre.webservice.GeishaWS.searchAgrupations(0, 0, nombre, passwd, "", "", nombre, null);          
             
        //Las listamos en el teléfono        
		for (int i=0; i<vAbi.size(); i++){		
			mDbGhelper.createRoute(vAbi.get(i).getId_agrupacion(), vAbi.get(i).getTitulo());
		}
        	
        geishaList.setOnItemClickListener(clickGeisha);
        tigreList.setOnItemClickListener(clickTigre);
       
        
        if (activado == true && nombre!= null && mDbGhelper.fetchAllRoutes().count()!=0){        	
        	geishaList.setVisibility(View.VISIBLE);  
        	titleRutasGeisha.setText("Rutas de Geisha");
        }else if (activado == true && nombre!= null && mDbGhelper.fetchAllRoutes().count()==0){
        	geishaList.setVisibility(View.VISIBLE);  
        	titleRutasGeisha.setText("No tiene rutas en Geisha!");
        }
        else if (activado == false){
        	geishaList.setVisibility(View.INVISIBLE);
        	titleRutasGeisha.setText("No esta conectado a Geisha");
        }else {
        	geishaList.setVisibility(View.INVISIBLE);
        }    	
        
        mDbHelper = new RutaDB(this);
        mDbHelper.open();
        fillData();
        
    }
	
//	private void populateFields() {
//    	if (mRowId != null) {
//            Cursor route = mDbGhelper.fetchRoute(mRowId);
//            startManagingCursor(route);
//            mTitle.setText(route.getString(route.getColumnIndex(RutaDB.KEY_TITLE)));
//          
//    	}
//    }
	
	
    private final boolean loadUserConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);
		activado = configuracion.getBoolean("estado", false);
		nombre = configuracion.getString("usuario", null);
		passwd = configuracion.getString("passwd", null);
		return true;
    }
    
    private void fillData() {
        Cursor rutasCursor = mDbHelper.fetchAllRoutes();
        startManagingCursor(rutasCursor);

        String[] from = new String[]{RutaDB.KEY_TITLE};
        int[] to = new int[]{R.id.text1};
        
        SimpleCursorAdapter routes = new SimpleCursorAdapter(this, R.layout.elemento, rutasCursor, from, to);
        tigreList.setAdapter(routes);
        
        Cursor rutasCursor2 = mDbGhelper.fetchAllRoutes();
        startManagingCursor(rutasCursor2);

        String[] from2 = new String[]{GeishaDB.KEY_TITLE};
        int[] to2 = new int[]{R.id.text1};
        
        SimpleCursorAdapter routesG = new SimpleCursorAdapter(this, R.layout.elemento, rutasCursor2, from2, to2);
        geishaList.setAdapter(routesG);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, IR_PUNTOS, R.string.menu5, R.drawable.btn_star);
        menu.add(0, IR_FOTOS, R.string.menu1foto, R.drawable.camera);
        menu.add(0, DIBUJAR_TODAS, R.string.menu3dibujar, R.drawable.plano);
        menu.add(0, BORRAR_RUTA, R.string.menuBorrar, R.drawable.papelera);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        switch(item.getId()) {
        case BORRAR_RUTA:
        	new AlertDialog.Builder(MisRutas.this)
    		.setTitle("¿Esta seguro que desea borrar la ruta?")
    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				long d = tigreList.getSelectedItemId();
    				Cursor cursor = mDbHelper.fetchRoute(d);
    				startManagingCursor(cursor);    
    				deleteFile("Ruta_"+d+".txt"); // Borra el archivo .txt asociado a la ruta    		    	    		    				
    	            mDbHelper.deleteRoute(d);
    	            // Borrar los puntos de interes de la ruta??
    	            fillData();
    			}
    		})
    		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				
    			}
    		})
    		.show();
            return true;
        case IR_PUNTOS:
        	Intent i = new Intent ();
        	i.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MisPuntos");
			startActivity(i);
			return true;
        case IR_FOTOS:
        	Intent intent = new Intent();
        	intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MisFotos");
        	startActivity(intent);
        	return true;
        case DIBUJAR_TODAS:
        	Intent intent2 = new Intent();
        	intent2.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.dibujar.DibujarTodasRutas");
        	startActivity(intent2);
        	return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
        super.onActivityResult(requestCode, resultCode, data, extras);
        fillData();
    }
    
    private OnItemClickListener clickTigre = new OnItemClickListener(){
		public void onItemClick(AdapterView arg0, View arg1, int position, long id) {
			Intent intent = new Intent();
	        intent.putExtra(RutaDB.KEY_ROWID, id);
        	intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.dibujar.DibujarRuta");
        	startActivity(intent);
        	}		
    };
    
    private OnItemClickListener clickGeisha = new OnItemClickListener(){
		public void onItemClick(AdapterView arg0, View arg1, int position, long id) {
			Intent intent = new Intent();
	        intent.putExtra(GeishaDB.KEY_ROWID, id);
        	intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.dibujar.DibujarGeishaAgrup");
        	startActivity(intent);
        	}		
    };
    
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