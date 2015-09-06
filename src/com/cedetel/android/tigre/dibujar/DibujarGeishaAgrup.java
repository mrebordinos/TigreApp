package com.cedetel.android.tigre.dibujar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Menu.Item;

import com.cedetel.android.tigre.R;
import com.cedetel.android.tigre.db.GeishaDB;
import com.cedetel.android.tigre.geisha.gis.Agrupacion;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.Ruta;
import com.cedetel.android.tigre.geisha.gis.RutaBaseInfo;
import com.cedetel.android.tigre.geisha.gis.VerticeRuta;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;
import com.google.android.maps.Point;

public class DibujarGeishaAgrup extends MapActivity {
	
	private MapView mRuta;

	private Long mRutaId;
	private GeishaDB mDbHelper;

	private String nombre;
	private String passwd;
	private String iconoPunto;
	
	private static final int INFORMACION = Menu.FIRST;
	private static final int INTERES = Menu.FIRST + 1;
	private static final int EXIT = Menu.FIRST + 2;
	private static final int CAPAS = Menu.FIRST + 3;
	private static final int ZOOM = Menu.FIRST + 4;

	protected OverlayController myOverlayController = null;
	private Bitmap PIN_INT = null;
	private final android.graphics.Point PIN_HOTSPOT = new android.graphics.Point(
			5, 29);

	protected List<List> agrupaciones = new ArrayList<List>();
	protected List<Point> ruta = new ArrayList<Point>();
	protected List<Point> ipuntos = new ArrayList<Point>();
	protected List<String> inombres = new ArrayList<String>();
	protected List<String> listacolor = new ArrayList<String>();
	
	private int minLatitude = (int) (+81 * 1E6);
	private int maxLatitude = (int) (-81 * 1E6);
	private int minLongitude = (int) (+181 * 1E6);
	private int maxLongitude = (int) (-181 * 1E6);
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mRuta = new MapView(this);
		loadConfig();

		myOverlayController = this.mRuta.createOverlayController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
		myOverlayController.add(myLocationOverlay, true);

		mDbHelper = new GeishaDB(this);
		mDbHelper.open();

		if (icicle == null) {
			mRutaId = null;
		} else {
			mRutaId = icicle.getLong(GeishaDB.KEY_ROWID);
		}

		if (mRutaId == null) {
			Bundle extras = getIntent().getExtras();
			mRutaId = extras != null ? extras.getLong(GeishaDB.KEY_ROWID)
					: null;
		}

		
		try {

			Agrupacion agrup = com.cedetel.android.tigre.webservice.GeishaWS
					.getAgrupation(mRutaId.intValue(), nombre, passwd);

			// Dibujamos las agrupaciones en la pantalla
			
			int esto = agrup.getRutas().size();
			
			for (int i = 0; i < esto; i++) {
				
				List<Point> ruta = new ArrayList<Point>();
				
				
				Ruta route = new Ruta();
				route = (Ruta) agrup.getRutas().get(i);
				
				RutaBaseInfo rbi = route.getRbi();
				listacolor.add(rbi.getLinea_color());
				
				double latitud, longitud;

				VerticeRuta v = new VerticeRuta();

				Iterator iter = route.getVertices().iterator();

				while (iter.hasNext()) {
					
					v = (VerticeRuta) iter.next();

					latitud = v.getLatitud();

					longitud = v.getLongitud();

					Point point1 = new Point((int) (latitud * 1000000),
							(int) (longitud * 1000000));
					ruta.add(point1);
					
					int latitude = point1.getLatitudeE6();
	    			int longitude = point1.getLongitudeE6();
	    			
	    			if (latitude != 0 && longitude != 0){            	            	
	    	                minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
	    	                maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;

	    	                minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
	    	                maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
	    			}
	    			
	    			MapController mc = mRuta.getController();

	    	        mc.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));
	    	        
	    	        mc.animateTo(new Point((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));				

				}
				agrupaciones.add(ruta);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Agrupacion agrup = com.cedetel.android.tigre.webservice.GeishaWS
			.getAgrupation(mRutaId.intValue(), nombre, passwd);

			int esto = agrup.getRutas().size();
	
			for (int i = 0; i < esto; i++) {
		
				Ruta route = new Ruta();
				route = (Ruta) agrup.getRutas().get(i);
		
				double latitud,longitud;
				String titulo;
				POI p = new POI();
	    	
				Iterator iter = route.getPois().iterator();
	    	
				while (iter.hasNext()){	    	
					
					p =(POI) iter.next();
	    	
					latitud = p.getLatitud() ;
	    			longitud = p.getLongitud();
	    		
					titulo = p.getTitulo();
	    		
					if(titulo.equals(null)){
						titulo = "punto";
					}
					
					Point point = new Point((int) (latitud * 1000000),(int) (longitud * 1000000));
					ipuntos.add(point);
					inombres.add(titulo);
				}		  

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Ruta route =
		 * com.cedetel.android.tigre.webservice.GeishaWS.getRoute(mRutaId
		 * .intValue(), nombre, passwd);
		 * 
		 * try {
		 * 
		 * //Ruta route = new Ruta(); //route = (Ruta) r.get(0);
		 * 
		 * double latitud,longitud;
		 * 
		 * // for(int i=0;i<route.getVertices().size();i++) // { VerticeRuta v =
		 * new VerticeRuta();
		 * 
		 * Iterator iter = route.getVertices().iterator();
		 * 
		 * while (iter.hasNext()){
		 * 
		 * v=(VerticeRuta) iter.next();
		 * 
		 * latitud = v.getLatitud() ;
		 * 
		 * longitud = v.getLongitud();
		 * 
		 * Point point1 = new Point((int) (latitud 1000000),(int) (longitud
		 * 1000000)); ruta.add(point1);
		 * 
		 * int latitude = point1.getLatitudeE6(); int longitude =
		 * point1.getLongitudeE6(); if (latitude != 0 && longitude != 0) {
		 * minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
		 * maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;
		 * 
		 * minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
		 * maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;
		 * }
		 * 
		 * MapController mc = mRuta.getController();
		 * 
		 * mc.zoomToSpan((maxLatitude - minLatitude), (maxLongitude -
		 * minLongitude));
		 * 
		 * mc.animateTo(new Point((maxLatitude + minLatitude) / 2, (maxLongitude
		 * + minLongitude) / 2));
		 * 
		 * } // }
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 * 
		 * try { //Ruta route = new Ruta(); //route = (Ruta) r.get(0);
		 * 
		 * double latitud,longitud; String titulo; POI p = new POI();
		 * 
		 * Iterator iter = route.getPois().iterator();
		 * 
		 * while (iter.hasNext()){
		 * 
		 * p =(POI) iter.next();
		 * 
		 * latitud = p.getLatitud() ;
		 * 
		 * longitud = p.getLongitud();
		 * 
		 * titulo = p.getTitulo();
		 * 
		 * if(titulo.equals(null)){ titulo = "punto"; } Point point = new
		 * Point((int) (latitud 1000000),(int) (longitud 1000000));
		 * ipuntos.add(point); inombres.add(titulo); }
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

		setContentView(mRuta);
	}

	private final boolean loadConfig() {
		SharedPreferences configuracion = getSharedPreferences("RedirectData",0);
		iconoPunto = configuracion.getString("icono", null);
		nombre = configuracion.getString("usuario", null);
		passwd = configuracion.getString("passwd", null);
		return true;
	}
	
	 
	 private int[] HextoRGB(String color){
		 
		 int[] colorRGB = new int[3];
		 String colorR = color.substring(0, 2);
		 String colorG = color.substring(2, 4);
		 String colorB = color.substring(4, 6);
		 
		 int r = Integer.parseInt(colorR, 16);
		 int g = Integer.parseInt(colorG, 16);
		 int b = Integer.parseInt(colorB, 16);
	   
		 colorRGB[0]=r;
		 colorRGB[1]=g;
		 colorRGB[2]=b;
	   	   
		 return colorRGB;
	    
	 }
	 
	 protected class MyLocationOverlay extends Overlay {

		@Override
		public void draw(Canvas canvas, PixelCalculator calculator,
				boolean shadow) {
			super.draw(canvas, calculator, shadow);
			Paint paint = new Paint();
			paint.setStyle(Style.FILL);
			paint.setARGB(255, 255, 255, 255);
			Point mapCentre = mRuta.getMapCenter();
			canvas.drawText("longitude: " + mapCentre.getLongitudeE6()
					+ ", latitude: " + mapCentre.getLatitudeE6(), 5, 15, paint);

			for (int k = 0; k < agrupaciones.size(); k++) {
				int[] prevScreenCoords = new int[2];
				boolean first = true;
				List<Point> unaruta = new ArrayList<Point>();
				unaruta = agrupaciones.get(k);
				String mColor = listacolor.get(k);
				
				for (Point point : unaruta) {
										
					int[] screenCoords = new int[2];
					calculator.getPointXY(point, screenCoords);
					
					int[] colorRGB = HextoRGB(mColor);
					paint.setARGB(80, colorRGB[0], colorRGB[1], colorRGB[2]);	
					
					canvas.drawOval(new RectF(screenCoords[0] - 5,
							screenCoords[1] + 5, screenCoords[0] + 5,
							screenCoords[1] - 5), paint);

					if (first == true) {
						paint.setARGB(255, 255, 255, 255);
						PIN_INT = BitmapFactory.decodeResource(mRuta
								.getResources(), R.drawable.house_icon);
						canvas.drawBitmap(PIN_INT, screenCoords[0]
								- PIN_HOTSPOT.x, screenCoords[1]
								- PIN_HOTSPOT.y, paint);
					} else if (!first) {
						
						canvas.drawLine(prevScreenCoords[0],
								prevScreenCoords[1], screenCoords[0],
								screenCoords[1], paint);
					}

					prevScreenCoords[0] = screenCoords[0];
					prevScreenCoords[1] = screenCoords[1];
					first = false;
				}
			}
			for (int i = 0; i < ipuntos.size(); i++) {
				Point point = ipuntos.get(i);
				int[] screenCoords = new int[2];
				calculator.getPointXY(point, screenCoords);
				if (iconoPunto.equals("chincheta roja")) {
					PIN_INT = BitmapFactory.decodeResource(
							mRuta.getResources(), R.drawable.mappin_red);
				} else if (iconoPunto.equals("chincheta azul")) {
					PIN_INT = BitmapFactory.decodeResource(
							mRuta.getResources(), R.drawable.mappin_blue);
				} else if (iconoPunto.equals("marca A")) {
					PIN_INT = BitmapFactory.decodeResource(
							mRuta.getResources(), R.drawable.mappinr);
				} else if (iconoPunto.equals("marca B")) {
					PIN_INT = BitmapFactory.decodeResource(
							mRuta.getResources(), R.drawable.mappiny);
				}
				paint.setARGB(255, 255, 255, 255);
				canvas.drawBitmap(PIN_INT, screenCoords[0] - PIN_HOTSPOT.x,
						screenCoords[1] - PIN_HOTSPOT.y, paint);
				paint.setARGB(255, 0, 0, 0);
				String mnombre = inombres.get(i);
				canvas.drawText(mnombre, screenCoords[0] - PIN_HOTSPOT.x,
						screenCoords[1] - PIN_HOTSPOT.y, paint);

			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, INFORMACION, R.string.menu1dibujar, R.drawable.dibujar);
		menu.add(0, INTERES, R.string.menu2dibujar, R.drawable.mappin_red);
		menu.add(0, CAPAS, R.string.menuCapas, R.drawable.world);
		menu.add(0, ZOOM, R.string.menuZoom, R.drawable.lupa);
		menu.add(0, EXIT, R.string.menuSalir, R.drawable.close);

		if (ipuntos.size() == 0) {
			menu.setItemShown(INTERES, false);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, Item item) {
		switch (item.getId()) {
		case INFORMACION:
			Intent intent = new Intent();
			intent.putExtra(GeishaDB.KEY_ROWID, mRutaId);
			intent.setClassName("com.cedetel.android.tigre",
					"com.cedetel.android.tigre.GeishaInfo");
			startActivity(intent);
			return true;
		case INTERES:
			Intent intent2 = new Intent();
			intent2.putExtra(GeishaDB.KEY_ROWID, mRutaId);
			intent2.setClassName("com.cedetel.android.tigre",
					"com.cedetel.android.tigre.MisPuntosGeisha");
			startActivity(intent2);
			return true;
		case EXIT:
			finish();
			return true;
		case CAPAS:
			new AlertDialog.Builder(DibujarGeishaAgrup.this).setIcon(
					R.drawable.car_yellow).setTitle(R.string.menuCapas)
					.setItems(R.array.opcionvista,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int position) {
									switch (position) {
									case 0:
										mRuta.toggleSatellite();
										break;
									case 1:
										mRuta.toggleTraffic();
										break;
									}
								}
							}).show();
			return true;

		case ZOOM:
			new AlertDialog.Builder(DibujarGeishaAgrup.this).setIcon(
					R.drawable.lupa).setTitle(R.string.menuZoom).setItems(
					R.array.zoomarray, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int position) {
							switch (position) {
							case 0:
								mRuta.getController().zoomTo(
										mRuta.getZoomLevel() + 1);
								break;
							case 1:
								mRuta.getController().zoomTo(
										mRuta.getZoomLevel() - 1);
								break;
							}
						}
					}).show();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_I) {
			mRuta.getController().zoomTo(mRuta.getZoomLevel() + 1);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_O) {
			mRuta.getController().zoomTo(mRuta.getZoomLevel() - 1);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_S) {
			mRuta.toggleSatellite();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_T) {
			mRuta.toggleTraffic();
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_BACK){
			 finish();
			 return true;
		 }
		return false;
	}

}