package com.mimotic.tigre.views.fotos;

import android.content.Intent;
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
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.db.datasource.PhotosDataSource;
import com.mimotic.tigre.model.db.datasource.RutaDataSource;
import com.mimotic.tigre.tools.TigreFragment;
import com.mimotic.tigre.views.fotos.adapter.FotoAdapter;

import java.util.ArrayList;
import java.util.List;

public class FotosListFragment extends TigreFragment{

    protected static final String TAG = "RutasListFragment";

    private List<Foto> fotos = new ArrayList<>();

    private FotoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.rutas_list_view, container, false);

        SwipeMenuListView fotosList = (SwipeMenuListView)rootView.findViewById(R.id.listview);
        fotosList.setOnItemClickListener(listadoListener);

        fotosList.setMenuCreator(creator);
        fotosList.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        getFotos();

        adapter = new FotoAdapter(getActivity(), R.layout.row_ruta, fotos);
        fotosList.setAdapter(adapter);


        fotosList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        Toast.makeText(getActivity(), "Foto borrada", Toast.LENGTH_SHORT).show();
                        PhotosDataSource photosDataSource = new PhotosDataSource(getActivity());
                        photosDataSource.open();
                        photosDataSource.deletePhotoById(fotos.get(position).getId());
                        photosDataSource.close();
                        fotos.remove(position);
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

        if(fotos.size()==0){
            getFotos();

            adapter = new FotoAdapter(getActivity(), R.layout.row_ruta, fotos);
            adapter.notifyDataSetChanged();
        }
    }

    private void getFotos(){
        PhotosDataSource photosDataSource = new PhotosDataSource(getActivity());
        photosDataSource.open();
        fotos = photosDataSource.getAllPhotos();
        photosDataSource.close();
    }


    private AdapterView.OnItemClickListener listadoListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Foto mfoto = fotos.get(position);

            Intent fotoIntent = new Intent(getActivity(), TouchImageActivity.class);
            fotoIntent.putExtra("imageURL", mfoto.getUrl());
            startActivity(fotoIntent);

//            Bundle bundle = new Bundle();
//            bundle.putString("url", mfoto.getUrl());
//            bundle.putInt("idfoto", mfoto.getId());
//            Fragment fragment = new FotosTabsFragment();//RutaViewFragment();
//            fragment.setArguments(bundle);
//            callbackNavigation.loadFragment(fragment);
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
