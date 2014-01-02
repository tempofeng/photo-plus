package com.liquable.photoplus;

import java.io.IOException;

import android.util.Log;
import android.view.SurfaceHolder;

public class CameraDrawer implements SurfaceHolder.Callback
{
    private static final String TAG = CameraDrawer.class.getSimpleName();

    private final CameraManager cameraManager;

    public CameraDrawer(final CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder,
        final int format,
        final int width,
        final int height)
    {
        cameraManager.startPreview();
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder)
    {
        try
        {
            cameraManager.open(holder);
        }
        catch (final IOException e)
        {
            Log.e(TAG, "cantOpenCamera:" + e.getLocalizedMessage());
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder)
    {
        cameraManager.close();
    }
}
