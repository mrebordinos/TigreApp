package com.cedetel.android.tigre.geisha.gis;

import java.util.Date;

public class PoligonoBaseInfo {
	
    private int id_poligono = 0;
    private int id_agrupacion = 0;
    private String nombre_poligono = null;
    private String tags = null;
    private String linea_color = null;
    private int linea_ancho = 0;
    private double linea_opacidad = 0;
    private String login = null;
    private Date fecha_creacion = null;
    
	public int getId_poligono() {
		return id_poligono;
	}
	public void setId_poligono(int id_poligono) {
		this.id_poligono = id_poligono;
	}
	public int getId_agrupacion() {
		return id_agrupacion;
	}
	public void setId_agrupacion(int id_agrupacion) {
		this.id_agrupacion = id_agrupacion;
	}
	public String getNombre_poligono() {
		return nombre_poligono;
	}
	public void setNombre_poligono(String nombre_poligono) {
		this.nombre_poligono = nombre_poligono;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getLinea_color() {
		return linea_color;
	}
	public void setLinea_color(String linea_color) {
		this.linea_color = linea_color;
	}
	public int getLinea_ancho() {
		return linea_ancho;
	}
	public void setLinea_ancho(int linea_ancho) {
		this.linea_ancho = linea_ancho;
	}
	public double getLinea_opacidad() {
		return linea_opacidad;
	}
	public void setLinea_opacidad(double linea_opacidad) {
		this.linea_opacidad = linea_opacidad;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Date getFecha_creacion() {
		return fecha_creacion;
	}
	public void setFecha_creacion(Date fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

    
}
