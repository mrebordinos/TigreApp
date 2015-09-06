package com.cedetel.android.tigre.webservice;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.cedetel.android.tigre.geisha.functiona.ServicioEnRegiones;
import com.cedetel.android.tigre.geisha.gis.Agrupacion;
import com.cedetel.android.tigre.geisha.gis.AgrupacionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.POI;
import com.cedetel.android.tigre.geisha.gis.Poligono;
import com.cedetel.android.tigre.geisha.gis.RegionBaseInfo;
import com.cedetel.android.tigre.geisha.gis.Ruta;
import com.cedetel.android.tigre.geisha.gis.RutaBaseInfo;
import com.cedetel.android.tigre.geisha.gis.VerticeRuta;

/**
 * 
 * @author bsoutullo
 */
public class GeishaWS {

	static final private String NAMESPACE = "http://webservices.geisha.cedetel.com/";


	/**
	 * Comprueba si el usuario y el password son correctos
	 * 
	 * @param login
	 * @param passwd
	 * @return true/false
	 */
	static public boolean isUserCorrect(String login, String passwd) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/isUserCorrect";
		String METHOD_NAME = "isUserCorrect";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/GeishaAdminWSService";

		boolean isCorrect = false;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		request.addProperty("login", login);
		request.addProperty("password", passwd);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			// Devuelve: isUserCorrectResponse{return=false; }
			SoapPrimitive valores = null;
			valores = (SoapPrimitive) ad.getProperty("return");
			isCorrect = Boolean.parseBoolean(valores.toString());
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return isCorrect;
	}


	/**
	 * Devuelve un Vector en cuyos elementos se encuentran las zonas existentes
	 * ordenadas por cantidad de información(agrupaciones, rutas, poligonos...)
	 * 
	 * @param numZones
	 *            : número de zonas que se quieren obtener como resultado
	 * @return Vector<RegionBaseInfo>
	 */
	static public Vector<RegionBaseInfo> getZones(int numZones) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/getZones";
		String METHOD_NAME = "getZones";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/GeishaAdminWSService";

		Vector<RegionBaseInfo> datos = new Vector(0);

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		request.addProperty("numZones", numZones);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			// miramos numero de objetos devueltos (creo que pueden darse 2
			// casos, uno si el objeto devuelto es una lista, y 2 si el objeto
			// devuelto es un unico objeto)
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;

			for (int i = 0; i < devueltos; i++) {
				RegionBaseInfo rbi = new RegionBaseInfo();
				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
				// propiedades de
				// las zonas
				SoapPrimitive valores = null;

				valores = (SoapPrimitive) objeto.getProperty("id_region");
				rbi.setId_region(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("nombre_region");
				rbi.setNombre_region(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("zoom_inicial");
				rbi.setZoom_inicial(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto
						.getProperty("id_agrupacionregion");
				rbi.setId_agrupacionregion(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto
						.getProperty("num_agrupaciones");
				rbi.setNum_agrupaciones(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("num_rutas");
				rbi.setNum_rutas(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("num_poligonos");
				rbi.setNum_poligonos(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("num_pois");
				rbi.setNum_pois(Integer.valueOf(valores.toString()));

				datos.add(rbi);
			}

		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return datos;
	}

	/**
	 * Devuelve un Vector en cuyos elementos se encuentran los servicios
	 * asociados y disponibles en la zona pedida
	 * 
	 * @param id_zone
	 * @param nombre
	 * @param password
	 * @return String[]
	 */
	static public Vector<ServicioEnRegiones> getAvailableServicesByZone(int id_zone,
			String nombre, String password) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/getAvailableServicesByZone";
		String METHOD_NAME = "getAvailableServicesByZone";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/GeishaAdminWSService";

		Vector<ServicioEnRegiones> datos = new Vector(0);

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		// SoapObject inParameters = new SoapObject(NAMESPACE, "inParameters");
		// inParameters.addProperty("zoneID", id);
		// request.addProperty("inParameters", inParameters);
		request.addProperty("zoneID", id_zone);
		request.addProperty("usuario", nombre);
		request.addProperty("password", password);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			// miramos numero de objetos devueltos (creo que pueden darse 2
			// casos, uno si el objeto devuelto es una lista, y 2 si el objeto
			// devuelto es un unico objeto)
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;

			for (int i = 0; i < devueltos; i++) {
				ServicioEnRegiones ser = new ServicioEnRegiones();
				objeto = (SoapObject) ad.getProperty(i); 
				
				SoapPrimitive valores = null;
				
				valores = (SoapPrimitive) objeto.getProperty("idServicio");
				ser.setIdServicio(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_categoria");
				ser.setId_categoria(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("nombre_servicio");
				ser.setNombre_servicio(valores.toString());				
				valores = (SoapPrimitive) objeto.getProperty("num_agrupaciones");
				ser.setNum_agrupaciones(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("num_pois");
				ser.setNum_pois(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("num_poligonos");
				ser.setNum_poligonos(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("num_rutas");
				ser.setNum_rutas(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("solo_puntos");
				ser.setSolo_puntos(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("idZona");
				ser.setIdZona(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("permiso_escritura");
				ser.setPermiso_escritura(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("permiso_lectura");
				ser.setPermiso_lectura(Integer.valueOf(valores.toString()));

			
				datos.add(ser);
				
//				// Devuelve: anyType{idServicio=2; idZona=0; id_categoria=0;
//				// nombre_servicio=Callejero; permiso_escritura=0;
//				// permiso_lectura=1; permite_rutas=1; }
//				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
//				// propiedades
//				SoapPrimitive valores = null;
//				valores = (SoapPrimitive) objeto.getProperty("nombre_servicio");
//				datos[i] = valores.toString();
			}

		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return datos;

	}
	

	/*
	 * Devuelve un objeto Ruta con informacion de la ruta, 
	 * los pois y los vertices de la ruta
	 */
	static public Ruta getRoute(int id_ruta, String usuario, String password) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/getRoute";
		String METHOD_NAME = "getRoute";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		// Vector<Ruta> r = new Vector();
		Ruta route = new Ruta();
		Vector<POI> p = new Vector();
		Vector<VerticeRuta> v = new Vector();

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("idRuta", id_ruta);
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {

			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			// miramos numero de objetos devueltos (creo que pueden darse 2
			// casos, uno si el objeto devuelto es una lista, y 2 si el objeto
			// devuelto es un unico objeto)
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;

			objeto = (SoapObject) ad.getProperty("return"); // Aqui tenemos ya
			// las propiedades

			SoapObject tmp;
			// En objeto tenemos ahora mismo la ruta completa con todas sus
			// cosas.
			for (int j = 0; j < objeto.getPropertyCount(); j++) {
				// miramos a ver el tipo que es
				tmp = (SoapObject) objeto.getProperty(j);
				String nombrePropiedad = tmp.getName();
				SoapPrimitive valores = null;
				if (nombrePropiedad.equals("poi")) {
					POI poi = new POI();
					valores = (SoapPrimitive) tmp.getProperty("titulo");
					poi.setTitulo(valores.toString());
					valores = (SoapPrimitive) tmp.getProperty("latitud");
					poi.setLatitud(Double.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("longitud");
					poi.setLongitud(Double.valueOf(valores.toString()));
					p.add(poi);
				} else if (nombrePropiedad.equals("verticeRuta")) {
					VerticeRuta vert = new VerticeRuta();
					valores = (SoapPrimitive) tmp.getProperty("latitud");
					vert.setLatitud(Double.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("longitud");
					vert.setLongitud(Double.valueOf(valores.toString()));
					v.add(vert);
				} else // suponemos que es rbi
				{
					RutaBaseInfo rbi = new RutaBaseInfo();
					valores = (SoapPrimitive) tmp.getProperty("id_ruta");
					rbi.setId_ruta(Integer.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("nombre_ruta");
					rbi.setNombre_ruta(valores.toString());
					valores = (SoapPrimitive) tmp.getProperty("linea_color");
					rbi.setLinea_color(valores.toString());
					valores = (SoapPrimitive) tmp.getProperty("linea_ancho");
					rbi.setLinea_ancho(Integer.valueOf(valores.toString()));
					/*
					 * valores =
					 * (SoapPrimitive)tmp.getProperty("linea_opacidad");
					 * rbi.setLinea_ancho(Integer.valueOf(valores.toString()));
					 * valores = (SoapPrimitive)tmp.getProperty("tags");
					 * rbi.setTags(valores.toString()); valores =
					 * (SoapPrimitive)tmp.getProperty("id_servicio");
					 * rbi.setId_servicio(Integer.valueOf(valores.toString()));
					 * valores = (SoapPrimitive)tmp.getProperty("login");
					 * rbi.setLogin(valores.toString()); valores =
					 * (SoapPrimitive)tmp.getProperty("id_region");
					 * rbi.setId_region(Integer.valueOf(valores.toString()));
					 * valores =
					 * (SoapPrimitive)tmp.getProperty("fecha_creacion");
					 * rbi.setId_region(Integer.valueOf(valores.toString()));
					 * valores =
					 * (SoapPrimitive)tmp.getProperty("fecha_caducidad");
					 * rbi.setId_region(Integer.valueOf(valores.toString()));
					 */
					route.setRbi(rbi);
				}

			}

			route.setPois(p);
			route.setVertices(v);

		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return route;
	}

	
	/*
	 * Devuelve un objeto Agrupacion que incluye la informacion de la agrupacion
	 * y todas sus rutas, poligonos y pois
	 */
	static public Agrupacion getAgrupation(int id_agrupacion, String usuario,
			String password) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/getAgrupation";
		String METHOD_NAME = "getAgrupation";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		Agrupacion agrup = new Agrupacion();
		Vector<Ruta> vRuta = new Vector();
		Vector<Poligono> vPol = new Vector();
		Vector<POI> vPOI = new Vector();

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("idAgrupacion", id_agrupacion);
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			// miramos numero de objetos devueltos (creo que pueden darse 2
			// casos, uno si el objeto devuelto es una lista, y 2 si el objeto
			// devuelto es un unico objeto)
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;

			// for (int i = 0; i < devueltos; i++) {

			objeto = (SoapObject) ad.getProperty("return"); // Aqui tenemos ya
			// las
			// propiedades

			SoapObject tmp;
			// En objeto tenemos ahora mismo la ruta completa con todas sus
			// cosas.
			for (int j = 0; j < objeto.getPropertyCount(); j++) {
				tmp = (SoapObject) objeto.getProperty(j);
				String nombrePropiedad = tmp.getName();
				SoapPrimitive valores = null;

				if (nombrePropiedad.equals("poi")) {
					POI poi = new POI();
					valores = (SoapPrimitive) tmp.getProperty("titulo");
					poi.setTitulo(valores.toString());
					valores = (SoapPrimitive) tmp.getProperty("latitud");
					poi.setLatitud(Double.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("longitud");
					poi.setLongitud(Double.valueOf(valores.toString()));
					// p.add(poi);
					vPOI.add(poi);
				} else if (nombrePropiedad.equals("poligonos")) {

					// De momento no utilizamnos poligonos

					// Poligono poligono = new Poligono();
					// valores =
					// (SoapPrimitive)tmp.getProperty("id_poligono");
					// pol.add(poligono);

				} else if (nombrePropiedad.equals("ruta")) {

					SoapObject tmp2;

					Ruta ruta = new Ruta();
					Vector<VerticeRuta> vVerticeRuta = new Vector();
					Vector<POI> vPOIRuta = new Vector();
					// En objeto tenemos ahora mismo la ruta completa con
					// todas sus cosas.
					for (int k = 0; k < tmp.getPropertyCount(); k++) {

						tmp2 = (SoapObject) tmp.getProperty(k);
						String nombrePropiedad2 = tmp2.getName();
						SoapPrimitive valores2 = null;

						if (nombrePropiedad2.equals("pois")) {

							POI poi = new POI();

							valores2 = (SoapPrimitive) tmp2
									.getProperty("titulo");
							poi.setTitulo(valores2.toString());
							valores2 = (SoapPrimitive) tmp2
									.getProperty("latitud");
							poi.setLatitud(Double.valueOf(valores2.toString()));
							valores2 = (SoapPrimitive) tmp2
									.getProperty("longitud");
							poi
									.setLongitud(Double.valueOf(valores2
											.toString()));

							vPOIRuta.add(poi);

						} else if (nombrePropiedad2.equals("verticeRuta")) {

							VerticeRuta vert = new VerticeRuta();

							valores2 = (SoapPrimitive) tmp2
									.getProperty("id_ruta");
							vert.setId_ruta(Integer
									.valueOf(valores2.toString()));
							valores2 = (SoapPrimitive) tmp2
									.getProperty("latitud");
							vert
									.setLatitud(Double.valueOf(valores2
											.toString()));
							valores2 = (SoapPrimitive) tmp2
									.getProperty("longitud");
							vert.setLongitud(Double
									.valueOf(valores2.toString()));

							vVerticeRuta.add(vert);

						} else {
							RutaBaseInfo rbi = new RutaBaseInfo();
							valores2 = (SoapPrimitive) tmp2
									.getProperty("id_ruta");
							rbi
									.setId_ruta(Integer.valueOf(valores2
											.toString()));
							valores2 = (SoapPrimitive) tmp2
									.getProperty("nombre_ruta");
							rbi.setNombre_ruta(valores2.toString());
							valores2 = (SoapPrimitive) tmp2
									.getProperty("linea_color");
							rbi.setLinea_color(valores2.toString());
							valores2 = (SoapPrimitive) tmp2
									.getProperty("linea_ancho");
							rbi.setLinea_ancho(Integer.valueOf(valores2
									.toString()));
							valores2 = (SoapPrimitive) tmp2.getProperty("tags");
							rbi.setTags(valores2.toString());
							valores2 = (SoapPrimitive) tmp2
									.getProperty("id_servicio");
							rbi.setId_servicio(Integer.valueOf(valores2
									.toString()));
							valores2 = (SoapPrimitive) tmp2
									.getProperty("login");
							rbi.setLogin(valores2.toString());
							valores2 = (SoapPrimitive) tmp2
									.getProperty("id_region");
							rbi.setId_region(Integer.valueOf(valores2
									.toString()));
							valores2 = (SoapPrimitive) tmp2
									.getProperty("id_agrupacion");
							rbi.setId_agrupacion(Integer.valueOf(valores2
									.toString()));
							ruta.setRbi(rbi);
						}

						ruta.setPois(vPOIRuta);
						ruta.setVertices(vVerticeRuta);
					}

					vRuta.add(ruta);

				} else {
					AgrupacionBaseInfo abi = new AgrupacionBaseInfo();
					valores = (SoapPrimitive) tmp.getProperty("id_agrupacion");
					abi.setId_agrupacion(Integer.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("id_servicio");
					abi.setId_servicio(Integer.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("id_region");
					abi.setId_region(Integer.valueOf(valores.toString()));
					valores = (SoapPrimitive) tmp.getProperty("titulo");
					abi.setTitulo(valores.toString());
					valores = (SoapPrimitive) tmp.getProperty("tags");
					abi.setTags(valores.toString());
					valores = (SoapPrimitive) tmp.getProperty("login");
					abi.setLogin(valores.toString());
					agrup.setAbi(abi);
				}
			}

			/*
			 * agrup.setPois(p); agrup.setPoligonos(pol); agrup.setRutas(r);
			 */
			// }
			agrup.setRutas(vRuta);
			//agrup.setPoligonos(vPol);
			agrup.setPois(vPOI);

		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return agrup;

	}

	
	/**
	 * Devuelve la información del punto de un servicio más cercano a uno pasado
	 * 
	 * @param lat
	 * @param lng
	 * @param usuario
	 * @param password
	 * @param idServicio
	 * @return Vector<POI>
	 */
	static public Vector<POI> searchNearestPoi(double lat, double lng,
			String usuario, String password, int idServicio) {

		Vector<POI> r = new Vector();

		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/searchNearestPoi";
		String METHOD_NAME = "searchNearestPoi";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		String[] datos = null;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("lat", ((Double)lat).toString());
		request.addProperty("lng", ((Double)lng).toString());	
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);
		request.addProperty("idServicio", idServicio);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		// SoapObject inParameters = new SoapObject(NAMESPACE, "inParameters");
		// inParameters.addProperty("zoneID", id);
		// request.addProperty("inParameters", inParameters);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;
			for (int i = 0; i < devueltos; i++) {
				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
				// propiedades
				SoapPrimitive valores = null;
				POI poi = new POI();
				valores = (SoapPrimitive) objeto.getProperty("id_poi");
				int idPOI = Integer.parseInt(valores.toString());
				poi.setId_poi(idPOI);
				valores = (SoapPrimitive) objeto.getProperty("id_region");
				int idZon = Integer.parseInt(valores.toString());
				poi.setId_region(idZon);
				valores = (SoapPrimitive) objeto.getProperty("titulo");
				String nom = valores.toString();
				poi.setTitulo(nom);

				r.add(poi);
			}
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());

		}

		return r;

	}
	
	
	
	static public Vector<POI> searchNearPois(float lat, float lng, float dist,
			String usuario, String password, int idServicio) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/searchNearPois";
		String METHOD_NAME = "searchNearPois";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		Vector<POI> p = new Vector();

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("lat", lat);
		request.addProperty("lng", lng);
		request.addProperty("dist", dist);
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);
		request.addProperty("idServicio", idServicio);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;

			for (int i = 0; i < devueltos; i++) {
				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
				// propiedades
				SoapPrimitive valores = null;

				POI poi = new POI();
				valores = (SoapPrimitive) objeto.getProperty("id_poi");
				poi.setId_poi(Integer.parseInt(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_region");
				poi.setId_region(Integer.parseInt(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("titulo");
				poi.setTitulo(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("id_agrupacion");
				poi.setId_agrupacion(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_servicio");
				poi.setId_servicio(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("login");
				poi.setLogin(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("latitud");
				poi.setLatitud(Double.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("longitud");
				poi.setLongitud(Double.valueOf(valores.toString()));

				p.add(poi);
			}
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return p;
	}

	
	
	static public Vector<POI> searchPois(int idServicio, int idZona,
			String usuario, String password, String titulo, String autor,
			String tag, String fecha) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/searchPois";
		String METHOD_NAME = "searchPois";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		Vector<POI> p = new Vector();

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("idServicio", idServicio);
		request.addProperty("idZona", idZona);
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);
		request.addProperty("titulo", titulo);
		request.addProperty("autor", autor);
		request.addProperty("tag", tag);
		request.addProperty("fecha", fecha);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			int devueltos = ad.getPropertyCount();
			SoapObject objeto;

			for (int i = 0; i < devueltos; i++) {
				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
				// propiedades
				SoapPrimitive valores = null;

				POI poi = new POI();
				valores = (SoapPrimitive) objeto.getProperty("id_poi");
				poi.setId_poi(Integer.parseInt(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_region");
				poi.setId_region(Integer.parseInt(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("titulo");
				poi.setTitulo(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("id_agrupacion");
				poi.setId_agrupacion(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_servicio");
				poi.setId_servicio(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("login");
				poi.setLogin(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("latitud");
				poi.setLatitud(Double.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("longitud");
				poi.setLongitud(Double.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("altitud");
				poi.setAltitud(Double.valueOf(valores.toString()));
				p.add(poi);
			}
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return p;
	}

	
	
	static public Vector<RutaBaseInfo> searchRoutes(int idServicio, int idZona,
			String usuario, String password, String tag, String nombre,
			String autor, String fecha) {

		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/searchRoutes";
		String METHOD_NAME = "searchRoutes";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		Vector<RutaBaseInfo> r = new Vector();
		String[] datos = null;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("idServicio", idServicio);
		request.addProperty("idZona", idZona);
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);
		request.addProperty("tag", tag);
		request.addProperty("nombre", nombre);
		request.addProperty("autor", autor);
		request.addProperty("fecha", fecha);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		// SoapObject inParameters = new SoapObject(NAMESPACE, "inParameters");
		// inParameters.addProperty("zoneID", id);
		// request.addProperty("inParameters", inParameters);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			// miramos numero de objetos devueltos (creo que pueden darse 2
			// casos, uno si el objeto devuelto es una lista, y 2 si el objeto
			// devuelto es un unico objeto)
			int devueltos = ad.getPropertyCount();
			datos = new String[devueltos];
			SoapObject objeto;

			for (int i = 0; i < devueltos; i++) {

				RutaBaseInfo rbi = new RutaBaseInfo();

				// Devuelve: anyType{fecha_creacion=2008-07-30T00:00:00+02:00;
				// id_region=2; id_ruta=2; id_servicio=3; linea_ancho=5;
				// linea_color=f00ff; linea_opacidad=0.0; login=luis;
				// nombre_ruta=Ruta Leyenda del Pisuerga; tags=campo, museos,
				// valladolid; }
				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
				// propiedades
				SoapPrimitive valores = null;
				valores = (SoapPrimitive) objeto.getProperty("id_ruta");
				rbi.setId_ruta(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("nombre_ruta");
				rbi.setNombre_ruta(valores.toString());

				r.add(rbi);

			}

		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return r;
	}

	

	

	static public Vector<AgrupacionBaseInfo> searchAgrupations(int idServicio,
			int idZona, String usuario, String password, String tag,
			String nombre, String autor, Date fecha) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/searchAgrupations";
		String METHOD_NAME = "searchAgrupations";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		Vector<AgrupacionBaseInfo> vAbi = new Vector();

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		request.addProperty("idServicio", idServicio);
		request.addProperty("idZona", idZona);
		request.addProperty("usuario", usuario);
		request.addProperty("password", password);
		request.addProperty("tag", tag);
		request.addProperty("nombre", nombre);
		request.addProperty("autor", autor);
		request.addProperty("fecha", fecha);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			int devueltos = ad.getPropertyCount();

			SoapObject objeto;

			for (int i = 0; i < devueltos; i++) {

				objeto = (SoapObject) ad.getProperty(i); // Aqui tenemos ya las
				// propiedades
				SoapPrimitive valores = null;

				AgrupacionBaseInfo abi = new AgrupacionBaseInfo();
				valores = (SoapPrimitive) objeto.getProperty("id_agrupacion");
				abi.setId_agrupacion(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_servicio");
				abi.setId_servicio(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("id_region");
				abi.setId_region(Integer.valueOf(valores.toString()));
				valores = (SoapPrimitive) objeto.getProperty("titulo");
				abi.setTitulo(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("tags");
				abi.setTags(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("login");
				abi.setLogin(valores.toString());
				valores = (SoapPrimitive) objeto.getProperty("fecha_creacion");
				abi.setLogin(valores.toString());

				vAbi.add(abi);

			}

		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return vAbi;

	}

	
	static public int writeAgrupation(Agrupacion infoAgrupacion,
			int idAgrupacion, String login, String passwd) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/writeAgrupation";
		String METHOD_NAME = "writeAgrupation";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		int id_agrupacion = -1;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		SoapObject agrup = new SoapObject(NAMESPACE, "Agrupacion");

		SoapObject abi = new SoapObject(NAMESPACE, "abi");

		abi.addProperty("titulo", infoAgrupacion.getAbi().getTitulo());
		abi.addProperty("id_servicio", infoAgrupacion.getAbi()
						.getId_servicio());
		abi.addProperty("id_region", infoAgrupacion.getAbi().getId_region());
		abi.addProperty("tags", infoAgrupacion.getAbi().getTags());
		agrup.addProperty("abi", abi);

		// -----------------------------------

		for (int i = 0; i < infoAgrupacion.getRutas().size(); i++) {
			
			Ruta route = new Ruta();
			
			route = (Ruta)infoAgrupacion.getRutas().get(i);
			
			SoapObject ruta = new SoapObject(NAMESPACE, "ruta");

			SoapObject rbi = new SoapObject(NAMESPACE, "rbi");

			rbi.addProperty("nombre_ruta", route.getRbi().getNombre_ruta());
			rbi.addProperty("login", route.getRbi().getLogin());
			rbi.addProperty("tags", route.getRbi().getTags());
			rbi.addProperty("linea_color", route.getRbi().getLinea_color());
			ruta.addProperty("rbi", rbi);

			for (int a = 0; a < route.getVertices().size(); a++) {

				VerticeRuta vertRuta = new VerticeRuta();

				SoapObject vertice = new SoapObject(NAMESPACE, "verticeRuta");

				vertRuta = (VerticeRuta) route.getVertices().get(a);

				vertice.addProperty("latitud", ((Double) vertRuta.getLatitud())
						.toString());
				vertice.addProperty("longitud", ((Double) vertRuta
						.getLongitud()).toString());
				vertice.addProperty("altitud", ((Double) vertRuta.getAltitud())
						.toString());
				vertice.addProperty("APie", vertRuta.getAPie());
				// vertice.addProperty("fecha_creacion",
				// vertRuta.getFecha_creacion());
				ruta.addProperty("vertices", vertice);

			}
			agrup.addProperty("rutas", ruta);
		}
		
		

		// -----------------------------------------

		for (int i = 0; i < infoAgrupacion.getPois().size(); i++) {
			
			POI infoPoi = new POI();
			infoPoi = (POI)infoAgrupacion.getPois().get(i);
			
			SoapObject poi = new SoapObject(NAMESPACE, "poi");

			poi.addProperty("titulo", infoPoi.getTitulo());
			poi.addProperty("login", infoPoi.getLogin());
			poi.addProperty("latitud", ((Double) infoPoi.getLatitud()).toString());
			poi.addProperty("longitud", ((Double) infoPoi.getLongitud()).toString());
			poi.addProperty("altitud", ((Double)infoPoi.getAltitud()).toString());
			poi.addProperty("html", infoPoi.getHtml());
			poi.addProperty("id_region", infoPoi.getId_region());
			poi.addProperty("id_servicio", infoPoi.getId_servicio());
			
			agrup.addProperty("pois", poi);
		
		}
		
		// -----------------------------------------------
		
		request.addProperty("idAgrupacion", idAgrupacion);
		request.addProperty("usuario", login);
		request.addProperty("password", passwd);

		request.addProperty("infoAgrupation", agrup);

		// request.addProperty("infoAgrupation", infoAgrupacion);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			SoapPrimitive valores = null;
			valores = (SoapPrimitive) ad.getProperty("return");
			id_agrupacion = Integer.valueOf(valores.toString());
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return id_agrupacion;
	}

	static public int writeRoute(Ruta route, int idAgrupacion, String login,
			String passwd) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/writeRoute";
		String METHOD_NAME = "writeRoute";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		int id_ruta = -1;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		SoapObject ruta = new SoapObject(NAMESPACE, "infoRoute");

		SoapObject rbi = new SoapObject(NAMESPACE, "rbi");

		rbi.addProperty("nombre_ruta", route.getRbi().getNombre_ruta());
		rbi.addProperty("login", route.getRbi().getLogin());
		rbi.addProperty("tags", route.getRbi().getTags());
		rbi.addProperty("linea_color", route.getRbi().getLinea_color());
		ruta.addProperty("rbi", rbi);

		for (int i = 0; i < route.getVertices().size(); i++) {

			VerticeRuta vertRuta = new VerticeRuta();

			SoapObject vertice = new SoapObject(NAMESPACE, "verticeRuta");

			vertRuta = (VerticeRuta) route.getVertices().get(i);

			vertice.addProperty("latitud", ((Double) vertRuta.getLatitud())
					.toString());
			vertice.addProperty("longitud", ((Double) vertRuta.getLongitud())
					.toString());
			vertice.addProperty("altitud", ((Double) vertRuta.getAltitud())
					.toString());
			vertice.addProperty("APie", vertRuta.getAPie());
			// vertice.addProperty("fecha_creacion",
			// vertRuta.getFecha_creacion());
			ruta.addProperty("vertices", vertice);

		}

		request.addProperty("idAgrupacionRuta", idAgrupacion);
		request.addProperty("usuario", login);
		request.addProperty("password", passwd);

		request.addProperty("infoRoute", ruta);

		// request.addProperty("infoAgrupation", infoAgrupacion);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			SoapPrimitive valores = null;
			valores = (SoapPrimitive) ad.getProperty("return");
			id_ruta = Integer.valueOf(valores.toString());
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return id_ruta;
	}

	static public int writePoi(POI infoPoi, String usuario, String passwd) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/writePoi";
		String METHOD_NAME = "writePoi";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		int id_poi = -1;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		SoapObject poi = new SoapObject(NAMESPACE, "poi");

		poi.addProperty("titulo", infoPoi.getTitulo());
		poi.addProperty("login", infoPoi.getLogin());
		poi.addProperty("latitud", ((Double) infoPoi.getLatitud()).toString());
		poi.addProperty("longitud", ((Double) infoPoi.getLongitud()).toString());
		poi.addProperty("altitud", ((Double)infoPoi.getAltitud()).toString());
		poi.addProperty("html", infoPoi.getHtml());
		poi.addProperty("id_agrupacion", infoPoi.getId_agrupacion());
		poi.addProperty("id_region", infoPoi.getId_region());
		poi.addProperty("id_servicio", infoPoi.getId_servicio());
		
		request.addProperty("usuario", usuario);
		request.addProperty("password", passwd);

		request.addProperty("infoPoi", poi);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			SoapPrimitive valores = null;
			valores = (SoapPrimitive) ad.getProperty("return");
			id_poi = Integer.valueOf(valores.toString());
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return id_poi;
	}

	static public int addVerticetoRoute(VerticeRuta infoVertice, String login,
			String passwd) {
		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/addVerticetoRoute";
		String METHOD_NAME = "addVerticetoRoute";
		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";

		int result = -1;

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		SoapObject vertice = new SoapObject(NAMESPACE, "VerticeRuta");

		// SoapObject vert = new SoapObject(NAMESPACE, "VerticeRuta");

		vertice.addProperty("latitud", ((Double) infoVertice.getLatitud())
				.toString());
		vertice.addProperty("longitud", ((Double) infoVertice.getLongitud())
				.toString());
		vertice.addProperty("altitud", ((Double) infoVertice.getAltitud())
				.toString());
		vertice.addProperty("id_ruta", infoVertice.getId_ruta());
		// vertice.addProperty("infoVertice", vert);

		// request.addProperty("infoVertice", vert);
		request.addProperty("login", login);
		request.addProperty("password", passwd);
		request.addProperty("infoVertice", vertice);

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject ad = (SoapObject) envelope.bodyIn;
			SoapPrimitive valores = null;
			valores = (SoapPrimitive) ad.getProperty("return");
			result = Integer.valueOf(valores.toString());
		} catch (Exception E) {
			System.out.println("ERROR:" + E.getClass().getName() + ": "
					+ E.getMessage());
		}

		return result;
	}
	
//	static public String rutaParaAndroid() {
//		String SOAP_ACTION = "http://webservices.geisha.cedetel.com/rutaParaAndroid";
//		String METHOD_NAME = "rutaParaAndroid";
//		String URL = "http://192.168.10.71:8080/GEISHACorev2/MapWSService";
//
//		String ruta = "";
//		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//				SoapEnvelope.VER11);
//
//		envelope.setOutputSoapObject(request);
//
//		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
//
//		try {
//			androidHttpTransport.call(SOAP_ACTION, envelope);
//			SoapObject ad = (SoapObject) envelope.bodyIn;
//			// Devuelve: isUserCorrectResponse{return=false; }
//
//			SoapPrimitive valores = null;
//			valores = (SoapPrimitive) ad.getProperty("return");
//			ruta = valores.toString();
//		} catch (Exception E) {
//			System.out.println("ERROR:" + E.getClass().getName() + ": "
//					+ E.getMessage());
//		}
//
//		return ruta;
//
//	}


} // Fin de clase