package com.cedetel.android.tigre.camara;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.CameraDevice;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class Camara extends Activity {
	
	private static final String TAG = "Camara";
	private static CameraDevice.CaptureParams param = new CameraDevice.CaptureParams();
	private static final String SAVING_MSG = "Guardando en disco...";
	private ProgressDialog mProgressDialog;
	
	private Previews mPreview;
	
	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mPreview = new Previews(this, param);
		setContentView(mPreview);

		// Coge el tamaño de la pantalla
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// Parametros
		param.type = 1; // preview
		param.srcWidth = dm.widthPixels;
		param.srcHeight = dm.heightPixels;
		param.leftPixel = 0;
		param.topPixel = 0;
		param.outputWidth = dm.widthPixels;
		param.outputHeight = dm.heightPixels;
		param.dataFormat = 2; // RGB_565
	}
	
	@Override
    protected void onResume()
    {
        super.onResume();
        mPreview.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mPreview.pause();
    }

   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_DPAD_CENTER:            

                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(SAVING_MSG);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                mPreview.post(mTakePicture);
                break;
                
            case KeyEvent.KEYCODE_BACK:
                mPreview.pause();
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
        return false;
    }

    final Runnable mTakePicture = new Runnable()
    {
        public void run()
        {
            mPreview.pause();

            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap", takePicture());

            mProgressDialog.dismiss();
         
            setResult(Activity.RESULT_OK, "test", bundle);
            finish();
        }
    };

    
    private Bitmap takePicture()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        CameraDevice camera = CameraDevice.open();

        if (camera != null)
        {
            camera.setCaptureParams(param);
            Bitmap myPic = Bitmap.createBitmap(param.outputWidth, param.outputHeight, false);
            Canvas canvas = new Canvas(myPic);
            camera.capture(canvas);
            camera.close();
            return myPic;
        } 
        else
        {
            Log.e(TAG, "Failed to open camera device");
            return null;
        }
    }
    
}


// ----------------------------------------------------------------------

class Previews extends SurfaceView implements SurfaceHolder.Callback
{

    private CameraDevice.CaptureParams parama = null;

    Previews(Context context, CameraDevice.CaptureParams param)
    {
        super(context);
       
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHasSurface = false;
        mHolder.setSizeFromLayout();
        this.parama = param;
    }

    public void resume()
    {
        if (mPreviewThread == null)
        {
            mPreviewThread = new PreviewThread();
            mPreviewThread.param = parama;

            if (mHasSurface == true)
            {
                mPreviewThread.start();
            }
        }
    }

    public void pause()
    {
        if (mPreviewThread != null)
        {
            mPreviewThread.requestExitAndWait();
            mPreviewThread = null;
        }
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        mHasSurface = true;
        if (mPreviewThread != null)
        {
            mPreviewThread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mHasSurface = false;
        pause();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
    }

    // -----------------------------------------------------------------------------

    class PreviewThread extends Thread
    {
        PreviewThread()
        {
            super();
            mDone = false;
        }

        @Override
        public void run()
        {
            CameraDevice camera = CameraDevice.open();
            if (camera != null)
            {
                camera.setCaptureParams(param);
            }
           
            SurfaceHolder holder = mHolder;
            while (!mDone)
            {
                Canvas canvas = holder.lockCanvas();

                if (camera != null)
                {
                    camera.capture(canvas);
                }
              
                holder.unlockCanvasAndPost(canvas);
            }

            if (camera != null)
                camera.close();
        }

        public void requestExitAndWait()
        {            
            mDone = true;
            try{
                join();
            } catch (InterruptedException ex){
            }
        }

        private boolean mDone;

        private CameraDevice.CaptureParams param = null;
    }

    SurfaceHolder mHolder;
    private PreviewThread mPreviewThread;
    private boolean mHasSurface;
}