package com.mimotic.tigre.menu;

public class NavMenuHeader implements NavDrawerItem {

    public static final int HEADER_TYPE = 2;

    private int id;
    private String name;
    private String urlPhoto;

    private NavMenuHeader() {
    }

    public static NavMenuHeader create( int id, String name, String urlPhoto ) {
        NavMenuHeader header = new NavMenuHeader();
        header.setName(name);
        header.setUrlPhoto(urlPhoto);
        return header;
    }

    @Override
    public int getType() {
        return HEADER_TYPE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean updateActionBarTitle() {
        return false;
    }
}