package com.cedetel.android.tigre.geisha.functiona;

import com.cedetel.android.tigre.geisha.gis.Punto;

public class Servicio {

    private int idServicio = 0;
   
    private Punto[] puntos = null;
    private String nombre_servicio = null;
    private int solo_puntos = 0;
    private int id_categoria = 0;
    private String superUser = null;
    private int num_agrupaciones = 0;
    private int num_rutas = 0;
    private int num_poligonos = 0;
    private int num_pois = 0;
	public int getIdServicio() {
		return idServicio;
	}
	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
	}
	public Punto[] getPuntos() {
		return puntos;
	}
	public void setPuntos(Punto[] puntos) {
		this.puntos = puntos;
	}
	public String getNombre_servicio() {
		return nombre_servicio;
	}
	public void setNombre_servicio(String nombre_servicio) {
		this.nombre_servicio = nombre_servicio;
	}
	public int getSolo_puntos() {
		return solo_puntos;
	}
	public void setSolo_puntos(int solo_puntos) {
		this.solo_puntos = solo_puntos;
	}
	public int getId_categoria() {
		return id_categoria;
	}
	public void setId_categoria(int id_categoria) {
		this.id_categoria = id_categoria;
	}
	public String getSuperUser() {
		return superUser;
	}
	public void setSuperUser(String superUser) {
		this.superUser = superUser;
	}
	public int getNum_agrupaciones() {
		return num_agrupaciones;
	}
	public void setNum_agrupaciones(int num_agrupaciones) {
		this.num_agrupaciones = num_agrupaciones;
	}
	public int getNum_rutas() {
		return num_rutas;
	}
	public void setNum_rutas(int num_rutas) {
		this.num_rutas = num_rutas;
	}
	public int getNum_poligonos() {
		return num_poligonos;
	}
	public void setNum_poligonos(int num_poligonos) {
		this.num_poligonos = num_poligonos;
	}
	public int getNum_pois() {
		return num_pois;
	}
	public void setNum_pois(int num_pois) {
		this.num_pois = num_pois;
	}

    
    
    
}

