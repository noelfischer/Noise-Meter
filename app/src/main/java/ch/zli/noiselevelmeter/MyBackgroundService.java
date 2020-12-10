package ch.zli.noiselevelmeter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static java.lang.Math.log10;

public class MyBackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("info", "onStartCommand");

        recordAudio();

        return Service.START_STICKY;
    }

    public void recordAudio() {
        String AudioSavePathInDevice = "dev/null";
        MediaRecorder myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(AudioSavePathInDevice);

        try {
            myAudioRecorder.prepare();
        } catch (java.io.IOException ioe) {
            android.util.Log.e("RECORDER", "IOException: " + android.util.Log.getStackTraceString(ioe));

        } catch (java.lang.SecurityException e) {
            android.util.Log.e("RECORDER", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }

        try {
            myAudioRecorder.start();
            Log.e("RECORDER", "recording...");

            for (int i = 100; i < 6000; i += 500) {
                schedule(myAudioRecorder, i);
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    myAudioRecorder.stop();
                    Log.e("RECORDER", "stopped recording");
                }
            }, 6200);

        } catch (java.lang.SecurityException e) {
            android.util.Log.e("RECORDER", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }
    }

    private void schedule(MediaRecorder audioRecorder, int delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int maxAmpl = audioRecorder.getMaxAmplitude();
                Log.e("sound", new String(new char[(int) Math.round(20 * log10(maxAmpl))]).replace("\0", "#"));
                Log.e("sound", "" + Math.round(20 * log10(maxAmpl)) + " decibel");
            }
        }, delay);
    }

    private String visualizeNumber(int n) {
        return new String(new char[n]).replace("\0", "#");
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        Log.e("info", "onBind");
        return null;
    }
}