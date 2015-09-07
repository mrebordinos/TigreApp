package com.mimotic.tigre.views;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.mimotic.tigre.R;
import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.common.settings.SettingsConstants;
import com.mimotic.tigre.common.utils.Utils;
import com.mimotic.tigre.menu.AbstractNavDrawerActivity;
import com.mimotic.tigre.menu.NavDrawerActivityConfiguration;
import com.mimotic.tigre.menu.NavDrawerAdapter;
import com.mimotic.tigre.menu.NavDrawerItem;
import com.mimotic.tigre.menu.NavMenuHeader;
import com.mimotic.tigre.menu.NavMenuItem;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.db.datasource.PhotosDataSource;
import com.mimotic.tigre.model.db.datasource.PuntosDataSource;
import com.mimotic.tigre.tools.TigreCallback;
import com.mimotic.tigre.views.fotos.CamaraFragment;
import com.mimotic.tigre.views.fotos.FotosTabsFragment;
import com.mimotic.tigre.views.rutas.RutasListFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                NavMenuItem.create(101, "home", "ic_home", false, false, this),
                NavMenuItem.create(103, "mis rutas", "ic_mis_rutas", false, false, this),
                NavMenuItem.create(104, "mis fotos", "ic_faro", false, false, this),
                NavMenuItem.create(105, "configuración", "ic_atom", false, false, this),
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
                loadFragment(new FotosTabsFragment());
                break;
            case 105: // CONFIGURACION
                loadFragment(new ConfigFragment());
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


    static final int REQUEST_IMAGE_CAPTURE = 123;

    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                LogTigre.e("Photo", "Error guardando imagen", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File tigreFolder = new File(storageDir + "/tigrapp");
        if(!tigreFolder.exists()){
            tigreFolder.mkdir();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                tigreFolder      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//            Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
//            dispatchTakePictureIntent();

            int idRuta = SettingsConstants.getIdRuta(this);

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(Utils.getProviderName(this));

            GeoPunto mpunto = new GeoPunto();
            mpunto.setIdRuta(idRuta);
            mpunto.setLatitude(location.getLatitude());
            mpunto.setLongitude(location.getLongitude());
            mpunto.setAltitude(location.getAltitude());

            PuntosDataSource puntosDataSource = new PuntosDataSource(this);
            puntosDataSource.open();
            int idCoords = puntosDataSource.insertPoint(mpunto);
            puntosDataSource.close();

            Foto mfoto = new Foto();
            mfoto.setUrl(mCurrentPhotoPath);
            mfoto.setTimestamp(System.currentTimeMillis());
            mfoto.setIdCoords(idCoords);
            mfoto.setIdRuta(idRuta);

            PhotosDataSource photosDataSource = new PhotosDataSource(this);
            photosDataSource.open();
            photosDataSource.createFoto(mfoto);
            photosDataSource.close();
        }else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
        } else {
            // Image capture failed, advise user
        }
    }

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                LogTigre.e("Photo", "Error guardando imagen", ex);
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(photoFile));
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }


//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }


}
