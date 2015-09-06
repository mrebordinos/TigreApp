package com.cedetel.android.tigre.camara;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.FotoDB;

public class VerFoto extends Activity{

	private FotoDB mDbHelper;
	private Long mFotoId;

	private ImageView foto;
	public Window mWind;

	@Override 
    public void onCreate(Bundle icicle) { 
        super.onCreate(icicle); 
        setContentView(R.layout.ver_foto);
        
//        foto = new ImageView(this);
        mDbHelper = new FotoDB(this);
	    mDbHelper.open();
	     
	    if (icicle == null) {
			mFotoId = null;
		} else {
			mFotoId = icicle.getLong(FotoDB.KEY_ROWID);
		}

		if (mFotoId == null) {
			Bundle extras = getIntent().getExtras();
			mFotoId = extras != null ? extras.getLong(FotoDB.KEY_ROWID) : null;
		}			
		   	
    	String titulo = "Foto_"+mFotoId.intValue()+".png";
        Bitmap bp = BitmapFactory.decodeFile("/data/data/com.cedetel.android.tigre/files/"+titulo);

        foto = (ImageView) findViewById(R.id.imagen);
        foto.setImageBitmap(bp);
        
//        mWind.setTitle(nombreFoto);
        
        Button botonSituar = (Button)findViewById(R.id.botonSituar);
		botonSituar.setOnClickListener(pulsar);
		
	}
	
	private OnClickListener pulsar = new OnClickListener(){
		public void onClick(View view) {
			Intent intent = new Intent();
        	intent.putExtra(FotoDB.KEY_ROWID, mFotoId);
			intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.camara.MapaFoto");
			startActivity(intent);
			finish();
			}
		};
		
}