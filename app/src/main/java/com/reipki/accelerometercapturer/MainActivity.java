package com.reipki.accelerometercapturer;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xCoor, yCoor, zCoor, saveStatus;
    private Button btn_Record, btn_Stop, number_btn;

    SensorManager SM;

    private CountDownTimer countDownTimer;
    private TextView timerText;

    static FileOutputStream fOut;

    static Boolean simpanData = false;

    String accData;

    private int minute, second, sumMinSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Record = (Button) findViewById(R.id.btn_Record);
        btn_Stop = (Button) findViewById(R.id.btn_Stop);
        timerText = (TextView)findViewById(R.id.timerText);

        xCoor = (TextView) findViewById(R.id.xCoor);
        yCoor = (TextView) findViewById(R.id.yCoor);
        zCoor = (TextView) findViewById(R.id.zCoor);
        saveStatus = (TextView) findViewById(R.id.saveStatus);

        number_btn = (Button)findViewById(R.id.button);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        SM.registerListener(this, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SM.SENSOR_DELAY_NORMAL); //Menambahkan Listener Sensor Accelerometer

       // onButtonClick();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText(String.valueOf(x));
            yCoor.setText(String.valueOf(y));
            zCoor.setText(String.valueOf(z));

            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String currTime = timeFormat.format(Calendar.getInstance().getTime());

            accData = xCoor.getText()+","+yCoor.getText()+","+zCoor.getText()+","+currTime+"\n";

            if (simpanData == true){
                try {
                    fOut.write(accData.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if  (simpanData == false){
                    try {
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*public void startCapture (View view){
        btn_Record.setVisibility(View.INVISIBLE);
        btn_Stop.setVisibility(View.VISIBLE);
    }*/

    public void stopCapture (View view){
        btn_Stop.setVisibility(View.INVISIBLE);
        btn_Record.setVisibility(View.VISIBLE);

        //Intent intent = new Intent(this, AccelerometerCapturer.class);
        //stopService(intent);
        simpanData = false;
        Toast.makeText(getBaseContext(), "Data Saved in: /data/data/com.reipki.accelerometercapturer/files", Toast.LENGTH_LONG).show();
        saveStatus.setText("Done!");

        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void startCapture(View view){
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(60);

        final NumberPicker numberPicker2 = new NumberPicker(this);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(60);

        NumberPicker.OnValueChangeListener myValueChange = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //number.setText(""+newVal);
                minute = newVal;
            }
        };

        NumberPicker.OnValueChangeListener myValueChange2 = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //number2.setText(""+newVal);
                second = newVal;
            }
        };

        numberPicker.setValue(minute);
        numberPicker2.setValue(second);

        numberPicker.setOnValueChangedListener(myValueChange);
        numberPicker2.setOnValueChangedListener(myValueChange2);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.weight = (float) 4.3;
        LinearLayout LL = new LinearLayout(this);
        LL.setOrientation(LinearLayout.HORIZONTAL);
        LL.addView(numberPicker,lp);
        LL.addView(numberPicker2,lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(LL);

        builder.setTitle("Lama Pengambilan Data");
        builder.setMessage("\t\t\t\t\t  Menit \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  Detik");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //number.setText(Integer.toString(minute));
                //number2.setText(Integer.toString(second));
                sumMinSec = minute*60 + second;
                //Toast.makeText(getBaseContext(), Integer.toString(sumMinSec), Toast.LENGTH_SHORT).show();



                btn_Record.setVisibility(View.INVISIBLE);
                btn_Stop.setVisibility(View.VISIBLE);

                DateFormat df = new SimpleDateFormat("dMMyyyy - HHmmss");
                String filename = df.format(Calendar.getInstance().getTime());

                try {
                    fOut = openFileOutput(filename+".txt", MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Intent intent = new Intent(this, AccelerometerCapturer.class);
                //startService(intent);
                simpanData = true;
                Toast.makeText(getBaseContext(), "Saving Data", Toast.LENGTH_SHORT).show();

                countDownTimer = new CountDownTimer(sumMinSec*1000, 1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //timerText.setText("" +millisUntilFinished / 1000);
                        saveStatus.setText("Saving Data in Progress....");
                    }

                    @Override
                    public void onFinish() {
//                        timerText.setText("Done!");
                        saveStatus.setText("Done!");
                        simpanData = false;
                        Toast.makeText(getBaseContext(), "Data Saved in: /data/data/com.reipki.accelerometercapturer/files", Toast.LENGTH_LONG).show();
                        btn_Stop.setVisibility(View.INVISIBLE);
                        btn_Record.setVisibility(View.VISIBLE);
                    }
                }.start();




            }
        } ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        } );
        builder.show();
    }

    /*public void onButtonClick() {
        btn_Record = (Button) findViewById(R.id.btn_Record);
        btn_Stop = (Button) findViewById(R.id.btn_Stop);

        btn_Record.setEnabled(true);

        btn_Record.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_Record.setVisibility(View.INVISIBLE);
                        btn_Stop.setVisibility(View.VISIBLE);

                        DateFormat df = new SimpleDateFormat("dMMyyyy - HHmmss");
                        String filename = df.format(Calendar.getInstance().getTime());

                        try {
                            fOut = openFileOutput(filename+".txt", MODE_PRIVATE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        simpanData = true;
                        Toast.makeText(getBaseContext(), "Saving Data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btn_Stop.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_Stop.setVisibility(View.INVISIBLE);
                        btn_Record.setVisibility(View.VISIBLE);

                        simpanData = false;
                        Toast.makeText(getBaseContext(), "Data Saved in: /data/data/com.reipki.accelerometercapturer/files", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }*/

}
