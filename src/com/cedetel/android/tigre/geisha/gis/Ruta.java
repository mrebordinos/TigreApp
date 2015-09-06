package com.cedetel.android.tigre.geisha.gis;

import java.util.Vector;

public class Ruta {

    private RutaBaseInfo rbi = null;
    private Vector vertices = new Vector();
    private Vector pois = new Vector();
	
    public RutaBaseInfo getRbi() {
		return rbi;
	}
	public void setRbi(RutaBaseInfo rbi) {
		this.rbi = rbi;
	}
	public Vector getVertices() {
		return vertices;
	}
	public void setVertices(Vector vertices) {
		this.vertices = vertices;
	}
	public Vector getPois() {
		return pois;
	}
	public void setPois(Vector pois) {
		this.pois = pois;
	}

    
   
    
}
