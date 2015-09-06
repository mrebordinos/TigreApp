package com.cedetel.android.tigre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

// 		INICIO DE LA APLICACIÓN TIGRE II

public class TigreII extends Activity {
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        ImageButton botonInicio = (ImageButton)findViewById(R.id.botonInicio);
        botonInicio.setOnClickListener(pulsarBoton);	
    }
    
    private OnClickListener pulsarBoton = new OnClickListener() {
    	public void onClick (View v) {
    		Intent intent = new Intent();
    		intent.setClassName("com.cedetel.android.tigre", "com.cedetel.android.tigre.MenuP");
    		startActivity(intent);
    		finish();    		
    	}
    };
}