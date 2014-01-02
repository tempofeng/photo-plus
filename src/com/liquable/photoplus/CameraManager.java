package com.liquable.photoplus;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraManager
{
    public interface CameraCallback
    {
        void onPictureTaken(Bitmap bmp);
    }

    private static final String TAG = CameraManager.class.getSimpleName();

    public static Bitmap decodeSampledBitmapFromData(final byte[] data,
        final int reqWidth,
        final int reqHeight)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = 2; // saved image will be one half the width and height of the
                                  // original (image captured is double the resolution of the screen
                                  // size)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    private Camera camera;

    public void close()
    {
        if (camera != null)
        {
            camera.stopPreview();
            // release the camera
            camera.release();
            // unbind the camera from this object
            camera = null;
        }
    }

    public void open(final SurfaceHolder holder) throws IOException
    {
        if (camera == null)
        {
            camera = Camera.open();

            final Parameters parameters = camera.getParameters();
            Log.v(TAG, "got parms");
            // set camera parameters
            parameters.setPreviewSize(640, 360);
            parameters.setPictureSize(1280, 720);
            // Camera.Parameters params = mCamera.getParameters();
            parameters.setPreviewFpsRange(30000, 30000);
            Log.v(TAG, "parms were set");
            camera.setParameters(parameters);

            camera.setPreviewDisplay(holder);
        }
    }

    public void startPreview()
    {
        camera.startPreview();
    }

    public void takePicture(final CameraCallback cameraCallback)
    {
        camera.takePicture(null, null, new PictureCallback()
        {
            @Override
            public void onPictureTaken(final byte[] data, final Camera camera)
            {
                final Bitmap bmp = decodeSampledBitmapFromData(data, 640, 360);
                camera.startPreview();
                cameraCallback.onPictureTaken(bmp);
            }
        });
    }
}
