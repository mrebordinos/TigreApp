package com.mimotic.tigre.views.fotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mimotic.tigre.R;
import com.mimotic.tigre.tools.TigreActivity;
import com.mimotic.tigre.tools.TouchImageView;
import com.squareup.picasso.Picasso;

public class TouchImageActivity extends TigreActivity {


    protected static final String TAG = "TouchImageActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String imageURL = getIntent().getStringExtra("imageURL");

        final TouchImageView view = new TouchImageView(this);
        view.setMaxZoom(4f);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        Picasso.with(this).load(imageURL).into(view);

        setContentView(view);

//        ImageView mImageView = new ImageView(this);
//
//        Picasso.with(this).load(imageURL).into(mImageView);
//
//        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);
//
////        mAttacher.update();
//        setContentView(mImageView);
    }


//    public static Bitmap loadBitmap(String imageURL) {
//        Bitmap bitmap = null;
//        try {
//            InputStream in = new java.net.URL(imageURL).openStream();
//            bitmap = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            LogTigre.e(TAG, "Exception - loadBitmap()", e);
//        }
//
//        return bitmap;
//    }


    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
	}


}
