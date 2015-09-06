package com.cedetel.android.tigre.geisha.gis;

import java.util.Vector;

public class Poligono {
 
	private PoligonoBaseInfo pbi = null;
    private Vector vertices_poligono = new Vector();
    
	public PoligonoBaseInfo getPbi() {
		return pbi;
	}
	public void setPbi(PoligonoBaseInfo pbi) {
		this.pbi = pbi;
	}
	public Vector getVertices_poligono() {
		return vertices_poligono;
	}
	public void setVertices_poligono(Vector vertices_poligono) {
		this.vertices_poligono = vertices_poligono;
	}

}
