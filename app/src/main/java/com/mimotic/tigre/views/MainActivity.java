package com.mimotic.tigre.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.mimotic.tigre.R;
import com.mimotic.tigre.menu.AbstractNavDrawerActivity;
import com.mimotic.tigre.menu.NavDrawerActivityConfiguration;
import com.mimotic.tigre.menu.NavDrawerAdapter;
import com.mimotic.tigre.menu.NavDrawerItem;
import com.mimotic.tigre.menu.NavMenuHeader;
import com.mimotic.tigre.menu.NavMenuItem;
import com.mimotic.tigre.tools.TigreCallback;

public class MainActivity extends AbstractNavDrawerActivity implements TigreCallback {

    int selectedMenu = 0;
    private int requestCodeToReturn = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        selectItem(0);
    }



    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

        NavDrawerItem[] menu = new NavDrawerItem[] {
                NavMenuHeader.create(0, "Tigre", "http://image.naldzgraphics.net/2012/11/35-simple-lines-tiger-logo.jpg"),
//                NavMenuSection.create(100, "seccion"),
                NavMenuItem.create(101, "home", "icon", false, false, this),
                NavMenuItem.create(102, "nueva ruta", "", false, false, this),
                NavMenuItem.create(103, "mis rutas", "", false, false, this),
                NavMenuItem.create(104, "cámara", "", false, false, this),
                NavMenuItem.create(105, "configuración", "", false, false, this),
//                NavMenuItem.create(110, "logout", "", false, false, this),
        };

        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
        navDrawerActivityConfiguration.setMainLayout(R.layout.activity_menu_lateral);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.navigation_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.navigation_drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.navigation_drawer_close);
        navDrawerActivityConfiguration.setBaseAdapter(new NavDrawerAdapter(this, R.layout.navdrawer_item, menu));

        return navDrawerActivityConfiguration;
    }


    @Override
    public void onNavItemSelected(int id) {
        selectedMenu = id;

        switch (id) {
            case 101: //HOME
                loadFragment(new HomeFragment());
                break;
            case 102: // NUEVA RUTA
                loadFragment(new CreateRutaFragment());
                break;
            case 103: // MIS RUTAS
                loadFragment(new RutasListFragment());
                break;
            case 104: // CAMARA
                loadFragment(new DefaultFragment());
                break;
            case 105: // CONFIGURACION
                loadFragment(new DefaultFragment());
                break;
            case 110:
                break;
            default:
                break;
        }
    }

    @Override
    public void loadFragment(Fragment mfragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (getSupportFragmentManager().findFragmentById(R.id.container) != null) {
            transaction.replace(R.id.container, mfragment);
        } else {
            transaction.add(R.id.container, mfragment);
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }


}
