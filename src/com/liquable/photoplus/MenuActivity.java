/*
 * Copyright (C) 2013 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.liquable.photoplus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.liquable.photoplus.CameraManager.CameraCallback;

/**
 * Activity showing the options menu.
 */
public class MenuActivity extends Activity
{
    private static final String TAG = MenuActivity.class.getSimpleName();

    private final ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service)
        {
            if (service instanceof PhotoPlusService.PhotoPlusBinder)
            {
                cameraManager = ((PhotoPlusService.PhotoPlusBinder) service).getCameraManager();
                openOptionsMenu();
            }

            // No need to keep the service bound.
            unbindService(this);
        }

        @Override
        public void onServiceDisconnected(final ComponentName name)
        {
            // Nothing to do here.
        }
    };

    private CameraManager cameraManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this, PhotoPlusService.class), mConnection, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photoplus, menu);
        return true;
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
            stopService(new Intent(this, PhotoPlusService.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(final Menu menu)
    {
        // Nothing else to do, closing the Activity.
        finish();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        openOptionsMenu();
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
