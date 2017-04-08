package com.reipki.accelerometercapturer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.reipki.accelerometercapturer.MainActivity.simpanData;
import static com.reipki.accelerometercapturer.MainActivity.fOut;

/**
 * Created by reipki on 9/28/2016.
 */

public class AccelerometerCapturer extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        simpanData = true;
        Toast.makeText(getBaseContext(), "Saving Data", Toast.LENGTH_SHORT).show();

        DateFormat df = new SimpleDateFormat("dMMyyyy - HHmmss");
        String filename = df.format(Calendar.getInstance().getTime());

        try {
            fOut = openFileOutput(filename+".txt", MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        synchronized (this){

            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        simpanData = false;
        Toast.makeText(getBaseContext(), "Data Saved in: /data/data/com.reipki.accelerometercapturer/files", Toast.LENGTH_LONG).show();
    }

}
