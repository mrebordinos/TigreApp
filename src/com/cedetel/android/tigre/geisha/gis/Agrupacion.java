package com.cedetel.android.tigre.geisha.gis;

import java.util.Vector;

public class Agrupacion {
	
	private AgrupacionBaseInfo abi = null;
    private Vector rutas = new Vector();
    private Vector poligonos = new Vector();
    private Vector pois = new Vector();
    
    
	public AgrupacionBaseInfo getAbi() {
		return abi;
	}
	public void setAbi(AgrupacionBaseInfo abi) {
		this.abi = abi;
	}
	public Vector getRutas() {
		return rutas;
	}
	public void setRutas(Vector rutas) {
		this.rutas = rutas;
	}
	public Vector getPoligonos() {
		return poligonos;
	}
	public void setPoligonos(Vector poligonos) {
		this.poligonos = poligonos;
	}
	public Vector getPois() {
		return pois;
	}
	public void setPois(Vector pois) {
		this.pois = pois;
	}

}
