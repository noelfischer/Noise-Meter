package ch.zli.noiselevelmeter;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.log10;

public class BackgroundService extends Service {

    public static boolean recorderStatus = false;
    MediaRecorder myAudioRecorder = new MediaRecorder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("info", "onStartCommand");

        recordAudio();

        return Service.START_STICKY;
    }
    private void recordAudio() {
        String AudioSavePathInDevice = "dev/null";
        try {
            if (!recorderStatus) {

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


                myAudioRecorder.start();
                recorderStatus = true;
                Log.e("RECORDER", "recording...");
            }


            for (int i = 0; i <= 59000; i += 500) {
                schedule(myAudioRecorder, i);
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.e("RECORDER", "stopped recording");
                }
            }, 59500);

        } catch (java.lang.SecurityException e) {
            android.util.Log.e("RECORDER", "SecurityException: " + android.util.Log.getStackTraceString(e));
        }
    }

    private void schedule(MediaRecorder audioRecorder, int delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int maxAmpl = audioRecorder.getMaxAmplitude();
                if (maxAmpl > 0) {
                    int decibel = (int) Math.round(20 * log10(maxAmpl));
                    MyViewModel.setLiveData(decibel);
                    Log.e("TIME", "seconds: " + delay / 1000);
                    Log.e("sound", new String(new char[decibel]).replace("\0", "#"));
                    Log.e("sound", "" + decibel + " decibel");
                }
            }
        }, delay);
    }


    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        Log.e("info", "onBind");
        return null;
    }


    @Override
    public void onDestroy() {
        if (recorderStatus) {
            try {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MyViewModel.setLiveData(0);
                    }
                }, 600);
                myAudioRecorder.stop();
            } catch (IllegalStateException e) {
                Log.e("error", "could not stop recorder");
            }

            recorderStatus = false;
        }
        Log.e("info", "onDestroyed");
    }


}