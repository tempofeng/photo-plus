package com.liquable.photoplus;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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

    private SpeechRecognizer speechRecognizer;

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

    private void startSpeechRecognizer()
    {
        if (speechRecognizer != null)
        {
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener()
        {

            @Override
            public void onBeginningOfSpeech()
            {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onBufferReceived(final byte[] buffer)
            {
                // Log.d(TAG, "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech()
            {
                Log.d(TAG, "onEndofSpeech");
            }

            @Override
            public void onError(final int error)
            {
                Log.d(TAG, "speechRecognizerError:" + error);
                stopSpeechRecognizer();
            }

            @Override
            public void onEvent(final int eventType, final Bundle params)
            {
                Log.d(TAG, "onEvent " + eventType);
            }

            @Override
            public void onPartialResults(final Bundle partialResults)
            {
                Log.d(TAG, "onPartialResults");
            }

            @Override
            public void onReadyForSpeech(final Bundle params)
            {
                Log.d(TAG, "onReadyForSpeech");
            }

            @Override
            public void onResults(final Bundle results)
            {
                Log.d(TAG, "onResults " + results);

                final List<String> keywords = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (final String keyword : keywords)
                {
                    Log.i(TAG, "result:" + keyword);
                }

                stopSpeechRecognizer();
            }

            @Override
            public void onRmsChanged(final float rmsdB)
            {
                // Log.d(TAG, "onRmsChanged");
            }
        });

        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(intent);
    }

    private void stopSpeechRecognizer()
    {
        if (speechRecognizer != null)
        {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
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
        stopSpeechRecognizer();
        cameraManager.close();

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
            startVoiceCommand();
            return true;
        case R.id.stop:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void startVoiceCommand()
    {
        startSpeechRecognizer();
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
