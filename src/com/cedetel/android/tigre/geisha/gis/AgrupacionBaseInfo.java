package com.cedetel.android.tigre.geisha.gis;

import java.util.Date;

public class AgrupacionBaseInfo {
	
	private int id_agrupacion = 0;
    private int id_servicio = 0;
    private int id_region = 0;
    private String titulo = null;
    private String tags = null;
    private String login = null;
    private Date fecha_creacion = null;
    
    
	public int getId_agrupacion() {
		return id_agrupacion;
	}
	public void setId_agrupacion(int id_agrupacion) {
		this.id_agrupacion = id_agrupacion;
	}
	public int getId_servicio() {
		return id_servicio;
	}
	public void setId_servicio(int id_servicio) {
		this.id_servicio = id_servicio;
	}
	public int getId_region() {
		return id_region;
	}
	public void setId_region(int id_region) {
		this.id_region = id_region;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
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
