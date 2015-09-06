package com.cedetel.android.tigre;

import java.util.Vector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cedetel.android.tigre.db.GeishaDB;
import com.cedetel.android.tigre.geisha.functiona.ServicioEnRegiones;
import com.cedetel.android.tigre.geisha.gis.Agrupacion;
import com.cedetel.android.tigre.geisha.gis.AgrupacionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.RegionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.Ruta;

public class GeishaInfo extends Activity{
	
	private GeishaDB mDbHelper;
	private Long mRutaId;
	
	private String nombre;
	private String passwd;
	
	private TextView titulo;
	private TextView usuario;
	private TextView rutas;
	private TextView tags;
	private TextView region;
	private TextView servicio;
	private TextView fecha1;

	private TextView tfecha1;

	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.geisha_info);
        loadConfig();
    
        mDbHelper = new GeishaDB(this);
        mDbHelper.open();
    
        if (icicle == null) {
        	mRutaId = null;
        } else {
        	mRutaId = icicle.getLong(GeishaDB.KEY_ROWID);
        }

        if (mRutaId == null) {
        	Bundle extras = getIntent().getExtras();
        	mRutaId = extras != null ? extras.getLong(GeishaDB.KEY_ROWID) : null;
        }
        
        Agrupacion agrup = com.cedetel.android.tigre.webservice.GeishaWS.getAgrupation(mRutaId.intValue(), nombre, passwd);
//        Ruta route = com.cedetel.android.tigre.webservice.GeishaWS.getRoute(mRutaId.intValue(), nombre, passwd);
//         
//        Ruta route = new Ruta();
//		  route = (Ruta) r.get(0);
		
		AgrupacionBaseInfo abi = new AgrupacionBaseInfo();		
		abi = agrup.getAbi();
		
		titulo = (TextView)findViewById(R.id.titulogeisha);
		titulo.setText(abi.getTitulo());
		
		rutas = (TextView)findViewById(R.id.titulorutas);
		String nomRuta[] = new String[agrup.getRutas().size()];
		for (int i=0; i < agrup.getRutas().size(); i++){
			Ruta ruta = new Ruta();
			ruta = (Ruta)agrup.getRutas().get(i);
			
			nomRuta[i] = ruta.getRbi().getNombre_ruta();
			rutas.append((i+1)+". "+nomRuta[i]+ "\n");
			
		}
		
		
		usuario = (TextView)findViewById(R.id.usuariogeisha);
		usuario.setText(abi.getLogin());	
		
		
		Vector zonas = com.cedetel.android.tigre.webservice.GeishaWS.getZones(0);
		
		region = (TextView)findViewById(R.id.regiongeisha);
		region.setText(((RegionBaseInfo)zonas.get(abi.getId_region())).getNombre_region());
      			
		Vector<ServicioEnRegiones> servicios  = com.cedetel.android.tigre.webservice.GeishaWS.getAvailableServicesByZone(abi.getId_region(), nombre, passwd);
		String[] aServicios = new String[servicios.size()];
		for (int i=0; i<servicios.size(); i++){
			ServicioEnRegiones ser = servicios.get(i);
			aServicios[i] = ser.getNombre_servicio();
		}
		servicio = (TextView)findViewById(R.id.serviciogeisha);
		servicio.setText(aServicios[abi.getId_servicio()]);
				
		tags = (TextView)findViewById(R.id.tagsgeisha);		
		tags.setText(abi.getTags());
			
		tfecha1 = (TextView)findViewById(R.id.tcreageisha);
		fecha1 = (TextView)findViewById(R.id.creageisha);
		if(abi.getFecha_creacion() != null){				
			fecha1.setText(abi.getFecha_creacion().toString());
		}else{
			fecha1.setVisibility(View.GONE);
			tfecha1.setVisibility(View.GONE);
		}
				

	}
	
	private final boolean loadConfig(){
		SharedPreferences configuracion = getSharedPreferences("RedirectData", 0);		
		nombre = configuracion.getString("usuario", null);
		passwd = configuracion.getString("passwd", null);
		return true;
 	}
	
	
}
