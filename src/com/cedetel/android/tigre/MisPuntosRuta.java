package com.cedetel.android.tigre;

import com.cedetel.android.tigre.db.PuntoDB;
import com.cedetel.android.tigre.db.RutaDB;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MisPuntosRuta extends ListActivity {
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private PuntoDB mDbHelper;
    private Long mRutaId;
    
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mis_puntos);
        
        mDbHelper = new PuntoDB(this);
        mDbHelper.open();
        
        if (icicle == null) {
			mRutaId = null;
		} else {
			mRutaId = icicle.getLong(PuntoDB.KEY_ROUTE);
		}

		if (mRutaId == null) {
			Bundle extras = getIntent().getExtras();
			mRutaId = extras != null ? extras.getLong(PuntoDB.KEY_ROUTE) : null;
		}
        
        fillData();
    }
    
    private void fillData() {
        Cursor puntosCursor = mDbHelper.fetchIpuntoByRoute(mRutaId);
    	startManagingCursor(puntosCursor);
            	        	    		
    	String[] from = new String[]{PuntoDB.KEY_TITLE};        	
    	int[] to = new int[]{R.id.text1};
        
    	SimpleCursorAdapter puntos = new SimpleCursorAdapter(this, R.layout.ruta_row, puntosCursor, from, to);
    	setListAdapter(puntos);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//      menu.add(0, INSERT_ID, R.string.menu1punto, R.drawable.btn_star);
        menu.add(0, DELETE_ID, R.string.menuBorrar, R.drawable.papelera);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        switch(item.getId()) {
//        case INSERT_ID:
//        	createIpunto();
//            return true;
        case DELETE_ID:
        	new AlertDialog.Builder(MisPuntosRuta.this)
    		.setTitle("¿Esta seguro que desea borrar el punto de interes?")
    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				 mDbHelper.deleteIpunto(getListView().getSelectedItemId());
    		         fillData();
    			}
    		})
    		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				
    			}
    		})
    		.show();
           
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void createIpunto() {
        Intent i = new Intent(this, PuntoInteres.class);
        startSubActivity(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, PuntoInteres.class);
        i.putExtra(RutaDB.KEY_ROWID, id);
        startSubActivity(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
        super.onActivityResult(requestCode, resultCode, data, extras);
        fillData();
    }
    
}
