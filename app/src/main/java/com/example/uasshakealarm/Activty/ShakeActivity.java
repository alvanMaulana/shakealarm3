package com.example.uasshakealarm.Activty;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uasshakealarm.AppReceiver;
import com.example.uasshakealarm.DatabaseHelper;
import com.example.uasshakealarm.ModelAlarm;
import com.example.uasshakealarm.R;

import java.util.Calendar;


public class ShakeActivity extends AppCompatActivity implements SensorEventListener {
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 134;

    private int NOTIFICATION_ID = 1;
    private  int penanda = 0;



    private SensorManager sensorManager;
    private long lastUpdate;

    private TextView Nama,txt1;
    private Button stop;

    SharedPreferences prf;

    ProgressBar progressBar;
    DatabaseHelper databaseHelper;
    Calendar cal;
    Intent intent;
    int id  = 1;
    int batasKesulitan = 10;
    AnimationDrawable shakeAnimation;
    ModelAlarm modelAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        prf = getSharedPreferences("shake",MODE_PRIVATE);
        this.id = (int) prf.getLong("Id_On",1);
        databaseHelper = new DatabaseHelper(this);
        modelAlarm = databaseHelper.getDataById(this.id);
        checkKesulitan();








        ImageView imageView = (ImageView) findViewById(R.id.shake);
        ImageView outside = (ImageView)findViewById(R.id.outside_imageview);
        imageView.setBackgroundResource(R.drawable.animation);


        shakeAnimation = (AnimationDrawable) imageView.getBackground();

        AlphaAnimation fadeIn = new AlphaAnimation(0.0F, 1.0F); // change values as you want
        fadeIn.setDuration(3500);
        fadeIn.setRepeatCount(Animation.INFINITE);
        outside.startAnimation(fadeIn);

        cal = Calendar.getInstance();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        progressBar = (ProgressBar)findViewById(R.id.PGB);



        Intent alarmIntent = new Intent(ShakeActivity.this, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ShakeActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);











    }

    private void checkKesulitan() {

        String kesulitan = modelAlarm.getKesulitan();
        if (kesulitan.equals("E")){
            batasKesulitan = 10;
        }
        if (kesulitan.equals("M")){
            batasKesulitan = 20;
        }
        if (kesulitan.equals("H")){
            batasKesulitan = 30;
        }

    }


    public void stopAlarmManager() {

        Intent intent = new Intent(ShakeActivity.this,LihatAlarm.class);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //close existing/current notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        hapusSharedPreferences();


        //jika app ini mempunyai banyak notifikasi bisa di cancelAll()
        //notificationManager.cancelAll();
        AppReceiver.stopMusic();
        repeatAlarm();
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


            this.penanda = this.penanda + 1;

            progressBar.setProgress(penanda*(100/batasKesulitan));

            if (penanda >= batasKesulitan) {

                stopAlarmManager();

            }

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
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    private void repeatAlarm( ) {


        Intent alarmIntent = new Intent(ShakeActivity.this, AppReceiver.class);
        alarmIntent.putExtra("id", this.id);
        if(modelAlarm != null && modelAlarm.getChecked() == 1) {
            pendingIntent = PendingIntent.getBroadcast(ShakeActivity.this, id, alarmIntent, 0);
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, modelAlarm.getJam());
            cal.set(Calendar.MINUTE, modelAlarm.getMenit());
            cal.set(Calendar.SECOND, 0);
            AlarmManager Repeat = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            cal.add(Calendar.DATE, 1);

            Repeat.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

            Toast.makeText(this, "AlarmManager repeated." + id, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Alarm Sudah di hapus / dinonAktifkan." + id, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        shakeAnimation.start();
    }



}
