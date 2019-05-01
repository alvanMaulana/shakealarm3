package com.example.uasshakealarm.Activty;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uasshakealarm.AppReceiver;
import com.example.uasshakealarm.R;


public class ShakeActivity extends AppCompatActivity implements SensorEventListener {
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 134;

    private int NOTIFICATION_ID = 1;
    private  int penanda = 0;



    private SensorManager sensorManager;
    private long lastUpdate;

    private TextView Angka,txt1;
    private Button stop;

    SharedPreferences prf;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);




        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        progressBar = (ProgressBar)findViewById(R.id.PGB);



        Intent alarmIntent = new Intent(ShakeActivity.this, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ShakeActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);


        prf = getSharedPreferences("shake",MODE_PRIVATE);



        Angka = (TextView)findViewById(R.id.penanda);
        stop = (Button)findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarmManager();
            }
        });



    }



    private void stopAlarmManager() {

        Intent intent = new Intent(ShakeActivity.this,LihatAlarm.class);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //close existing/current notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        hapusSharedPreferences();


        //jika app ini mempunyai banyak notifikasi bisa di cancelAll()
        //notificationManager.cancelAll();
        Toast.makeText(this, "AlarmManager Stopped by User.", Toast.LENGTH_SHORT).show();
        startActivity(intent);


    }

    private void hapusSharedPreferences() {

        SharedPreferences.Editor editor = prf.edit();
        editor.clear();
        editor.commit();


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            getAccelerometer(sensorEvent);
        }

    }

    private void getAccelerometer(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        //movenment

        float x = values[0];
        float y = values[1];
        float z = values[2];






        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = sensorEvent.timestamp;

        if (accelationSquareRoot >= 10) {

            if (actualTime - lastUpdate < 400) {
                return;
            }


            lastUpdate = actualTime;

            Toast.makeText(this, "Goyangan Terdeteksi", Toast.LENGTH_SHORT).show();

            this.penanda = this.penanda + 1;

            progressBar.setProgress(penanda*10);

            if (penanda >= 10) {

                stopAlarmManager();

            }


            Angka.setText(String.valueOf(this.penanda ));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onResume() {
        super.onResume();


        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



}
