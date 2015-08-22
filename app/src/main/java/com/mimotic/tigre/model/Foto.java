package com.mimotic.tigre.model;

public class Foto {

    private int id;
    private String url;
    private long timestamp;
    private int idRuta;
    private int idCoords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public int getIdCoords() {
        return idCoords;
    }

    public void setIdCoords(int idCoords) {
        this.idCoords = idCoords;
    }
}
