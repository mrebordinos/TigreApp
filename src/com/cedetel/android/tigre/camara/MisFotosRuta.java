package com.cedetel.android.tigre.camara;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu.Item;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.FotoDB;

public class MisFotosRuta extends ListActivity {
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
//    private static final int MAP_PHOTO = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int SHOW_PHOTO = Menu.FIRST + 2;
    private static final int EDIT_PHOTO = Menu.FIRST + 3;
    
    private String nombreFoto;
    private Long mFotoId;
    private FotoDB mDbHelper;
    
    private Long mRutaId;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        mDbHelper = new FotoDB(this);
        mDbHelper.open();
        
        if (icicle == null) {
			mRutaId = null;
		} else {
			mRutaId = icicle.getLong(FotoDB.KEY_ROUTE);
		}

		if (mRutaId == null) {
			Bundle extras = getIntent().getExtras();
			mRutaId = extras != null ? extras.getLong(FotoDB.KEY_ROUTE) : null;
		}
				

        
        fillData();
    }
    
    private void fillData() {
    	Cursor mCursor = mDbHelper.fetchPhotoByRoute(mRutaId);
        startManagingCursor(mCursor);
        if ((mCursor.count() == 0) || !mCursor.first()){
        	Toast.makeText(MisFotosRuta.this, "No hay Fotos", Toast.LENGTH_LONG);
        }else {
        	ListAdapter adapter = new PhotoListAdapter(this, mCursor);
        	setListAdapter(adapter);
        }
//        String[] from = new String[]{FotoDB.KEY_TITLE};
//        int[] to = new int[]{R.id.text1};
//        
//        SimpleCursorAdapter fotos = new SimpleCursorAdapter(this, R.layout.ruta_row, fotosCursor, from, to);
//        setListAdapter(fotos);
    }
    
    private class PhotoListAdapter extends CursorAdapter
    {

        public PhotoListAdapter(Context context, Cursor cursor)
        {
            super(cursor, context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
    		nombreFoto = cursor.getString(cursor.getColumnIndex(FotoDB.KEY_TITLE));
    		mFotoId = cursor.getLong(cursor.getColumnIndex(FotoDB.KEY_ROWID));
    		
    		String titulo = "Foto_"+mFotoId.intValue()+".png";
            Bitmap bp = BitmapFactory.decodeFile("/data/data/com.cedetel.android.tigre/files/"+titulo);
            
            ImageView iv = (ImageView) view.findViewById(R.id.thumb);
            iv.setImageBitmap(bp);

            TextView tvn = (TextView) view.findViewById(R.id.nombreFoto);
            tvn.setText(cursor, cursor.getColumnIndex(FotoDB.KEY_TITLE));

            TextView tv = (TextView) view.findViewById(R.id.tagFoto);
            tv.setText(cursor, cursor.getColumnIndex(FotoDB.KEY_TAG));

            view.setPadding(0, 6, 0, 6);
            view.setHorizontalFadingEdgeEnabled(true);
        }
        
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            return getViewInflate().inflate(R.layout.mis_fotos, null, null);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SHOW_PHOTO, R.string.menu3foto, R.drawable.tomar);
//        menu.add(0, MAP_PHOTO, R.string.menu2foto, R.drawable.world);
        menu.add(0, EDIT_PHOTO, R.string.menuEditar, R.drawable.dibujar);
        menu.add(0, DELETE_ID, R.string.menuBorrar, R.drawable.papelera);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, Item item) {
        switch(item.getId()) {
        case SHOW_PHOTO:
        	Intent intent = new Intent();
        	intent.putExtra(FotoDB.KEY_ROWID, getListView().getSelectedItemId());
        	intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.VerFoto");
        	startActivity(intent);
            return true;
//        case MAP_PHOTO:
//        	Intent i = new Intent();
//        	i.putExtra(FotoDB.KEY_ROWID, getListView().getSelectedItemId());
//        	i.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MapaFotos");
//        	startActivity(i);
//        	return true;
        case EDIT_PHOTO:
        	Intent in = new Intent(this, NuevaFoto.class);
		    in.putExtra(FotoDB.KEY_ROWID, getListView().getSelectedItemId());
		    startSubActivity(in, ACTIVITY_EDIT);
        	return true;
        case DELETE_ID:
        	new AlertDialog.Builder(MisFotosRuta.this)
    		.setTitle("�Esta seguro que desea borrar la foto?")
    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				long d = getListView().getSelectedItemId();
    	        	Cursor cursor = mDbHelper.fetchPhoto(d);
    	    		startManagingCursor(cursor);
    	    		String m = cursor.getString(cursor.getColumnIndex(FotoDB.KEY_TITLE));
    	        	// Borra tambien el archivo .png
    	    		deleteFile(m+"_"+d+".png");
    	            mDbHelper.deletePhoto(d);
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

    private void createPhoto() {
        Intent i = new Intent(this, NuevaFoto.class);
        startSubActivity(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, VerFoto.class);
        i.putExtra(FotoDB.KEY_ROWID, id);
//        setResult(Activity.MODE_WORLD_WRITEABLE);
        startSubActivity(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
        super.onActivityResult(requestCode, resultCode, data, extras);
        fillData();
    }
}