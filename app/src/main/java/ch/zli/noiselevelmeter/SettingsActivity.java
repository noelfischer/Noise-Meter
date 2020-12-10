package ch.zli.noiselevelmeter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import static androidx.core.app.ActivityCompat.requestPermissions;


import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Context context = getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment(context, alarmManager))
                    .commit();
        }

        if (!checkPermission(context)) {
            requestPermission();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        Context context;
        AlarmManager alarmManager;

        public SettingsFragment(Context context, AlarmManager alarmManager) {
            this.context = context;
            this.alarmManager = alarmManager;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SwitchPreferenceCompat switchPreferenceCompat = findPreference("sync");

            switchPreferenceCompat.setOnPreferenceChangeListener((arg0, active) -> {
                Intent i = new Intent(context, MyBackgroundService.class);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
                if (!checkPermission(context)) {
                    switchPreferenceCompat.setEnabled(false);
                }
                if ((boolean) active) {

                    alarmManager.setInexactRepeating(AlarmManager.RTC,
                            SystemClock.elapsedRealtime() + 300,
                            300, pendingIntent);
                    Log.e("INFO", "started alarmManager");
                } else {

                    alarmManager.cancel(pendingIntent);
                    Log.e("INFO", "stopped alarmManager");
                }
                return true;
            });
        }

    }

    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Log.e("info", "Permission Granted");
                    } else {
                        Log.e("info", "Permission Denied");
                    }
                }
                break;
        }
    }

}
