package com.liquable.photoplus;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.liquable.photoplus.CameraManager.CameraCallback;

public class PhotoPlusActivity extends Activity
{
    private static final String TAG = PhotoPlusActivity.class.getSimpleName();

    private final CameraManager cameraManager = new CameraManager();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoplus);

        final SurfaceHolder holder = ((SurfaceView) findViewById(R.id.preview)).getHolder();
        holder.addCallback(new SurfaceHolder.Callback()
        {
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photoplus, menu);
        return true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
        {
            openOptionsMenu();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle item selection.
        switch (item.getItemId())
        {
        case R.id.camera:
            takePicture();
            return true;
        case R.id.stop:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void takePicture()
    {
        cameraManager.takePicture(new CameraCallback()
        {
            @Override
            public void onPictureTaken(final Bitmap bmp)
            {
                Log.i(TAG, "onPictureTaken:" + bmp.getWidth() + "," + bmp.getHeight());
            }
        });
    }
}
