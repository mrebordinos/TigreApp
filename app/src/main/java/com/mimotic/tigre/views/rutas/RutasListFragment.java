package com.mimotic.tigre.views.rutas;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.mimotic.tigre.R;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.tools.TigreFragment;
import com.mimotic.tigre.views.rutas.adapter.RutaAdapter;

import java.util.ArrayList;
import java.util.List;

public class RutasListFragment extends TigreFragment{

    protected static final String TAG = "RutasListFragment";

    private List<Ruta> rutas = new ArrayList<>();

    private RutaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.rutas_list_view, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis rutas");

        SwipeMenuListView rutasList = (SwipeMenuListView)rootView.findViewById(R.id.listview);
        rutasList.setOnItemClickListener(listadoListener);

        rutasList.setMenuCreator(creator);
        rutasList.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        getRutas();

        adapter = new RutaAdapter(getActivity(), R.layout.row_ruta, rutas);
        rutasList.setAdapter(adapter);


        rutasList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        Toast.makeText(getActivity(), "Ruta borrada", Toast.LENGTH_SHORT).show();
                        RutaDataSource rutaDataSource = new RutaDataSource(getActivity());
                        rutaDataSource.open();
                        rutaDataSource.deleteRutaById(rutas.get(position).getId());
                        rutaDataSource.close();
                        rutas.remove(position);
                        adapter.notifyDataSetChanged();
                        break;
//                    case 1:
//                        // delete
//                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(rutas.size()==0){
            getRutas();

            adapter = new RutaAdapter(getActivity(), R.layout.row_ruta, rutas);
            adapter.notifyDataSetChanged();
        }
    }

    private void getRutas(){
        RutaDataSource rutaDataSource = new RutaDataSource(getActivity());
        rutaDataSource.open();
        rutas = rutaDataSource.getAllRutas();
        rutaDataSource.close();
    }


    private AdapterView.OnItemClickListener listadoListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Ruta mruta = rutas.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("title", mruta.getTitle());
            bundle.putInt("idruta", mruta.getId());
            Fragment fragment = new RutaTabsFragment();//RutaViewFragment();
            fragment.setArguments(bundle);
            callbackNavigation.loadFragment(fragment);
        }
    };






    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
//            SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
//            // set item background
//            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
//            // set item width
//            openItem.setWidth(90);
//            // set item title
//            openItem.setTitle("Open");
//            // set item title fontsize
//            openItem.setTitleSize(18);
//            // set item title font color
//            openItem.setTitleColor(Color.WHITE);
//            // add to menu
//            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(120);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

}
