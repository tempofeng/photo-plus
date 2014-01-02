package com.liquable.photoplus;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

public class PhotoPlusService extends Service
{
    public class PhotoPlusBinder extends Binder
    {
        public CameraManager getCameraManager()
        {
            return cameraManager;
        }
    }

    private static final String LIVE_CARD_ID = "photoPlus";

    private LiveCard liveCard;

    private final CameraManager cameraManager = new CameraManager();

    private final PhotoPlusBinder binder = new PhotoPlusBinder();

    @Override
    public IBinder onBind(final Intent intent)
    {
        return binder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        if (liveCard != null && liveCard.isPublished())
        {
            liveCard.unpublish();
            liveCard = null;
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        if (liveCard == null)
        {
            final TimelineManager timelineManager = TimelineManager.from(this);
            liveCard = timelineManager.createLiveCard(LIVE_CARD_ID);

            liveCard.setDirectRenderingEnabled(true)
                .getSurfaceHolder()
                .addCallback(new CameraDrawer(cameraManager));

            final Intent menuIntent = new Intent(this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            liveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            liveCard.publish(PublishMode.REVEAL);
        }

        return START_STICKY;
    }
}
