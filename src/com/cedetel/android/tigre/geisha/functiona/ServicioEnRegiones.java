package com.cedetel.android.tigre.geisha.functiona;

public class ServicioEnRegiones extends Servicio {

    private int idZona = 0;
    private String funcion_datos_externos = null;
    // permiso_lectura y permiso_escritura se inicializan a -1 ya que pueden tener valor 0 ó 1, según
    // tengan o no tengan permiso 
    private int permiso_lectura = -1;
    private int permiso_escritura = -1;
	public int getIdZona() {
		return idZona;
	}
	public void setIdZona(int idZona) {
		this.idZona = idZona;
	}
	public String getFuncion_datos_externos() {
		return funcion_datos_externos;
	}
	public void setFuncion_datos_externos(String funcion_datos_externos) {
		this.funcion_datos_externos = funcion_datos_externos;
	}
	public int getPermiso_lectura() {
		return permiso_lectura;
	}
	public void setPermiso_lectura(int permiso_lectura) {
		this.permiso_lectura = permiso_lectura;
	}
	public int getPermiso_escritura() {
		return permiso_escritura;
	}
	public void setPermiso_escritura(int permiso_escritura) {
		this.permiso_escritura = permiso_escritura;
	}
    
    
}
