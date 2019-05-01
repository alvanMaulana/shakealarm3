package com.example.uasshakealarm.Activty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



import com.example.uasshakealarm.AppReceiver;
import com.example.uasshakealarm.DatabaseHelper;
import com.example.uasshakealarm.ModelAlarm;
import com.example.uasshakealarm.R;
import com.example.uasshakealarm.TimePickerFragment;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 134;
    //set interval notifikasi 10 detik
    private int interval_seconds = 10;
    private int NOTIFICATION_ID = 1;
    private  int penanda = 0;



    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;

    private TextView accelText;
    SharedPreferences pref;
    Calendar c;
    DatabaseHelper databaseHelper;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = Calendar.getInstance();




        final Intent toLihat = new Intent(MainActivity.this,LihatAlarm.class);
        Intent alarmIntent = new Intent(MainActivity.this, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);

        Button buttonTimePicker = findViewById(R.id.buttonStart);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                //kkkk



            }
        });


        Button btnSave = (Button)findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertToSqlLite();
                startAlarmManager(c);
                startActivity(toLihat);

            }
        });


        Button back = (Button)findViewById(R.id.buttonBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(toLihat);

            }
        });


    }




    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {


        this.c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.c.set(Calendar.MINUTE, minute);
        this.c.set(Calendar.SECOND, 0);



    }

    public void startAlarmManager(Calendar c ) {

        //set waktu sekarang berdasarkan interval

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //set alarm manager dengan memasukkan waktu yang telah dikonversi menjadi milliseconds

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        manager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);


        Toast.makeText(this, "AlarmManager Start.", Toast.LENGTH_SHORT).show();

    }

    private void insertToSqlLite() {
        databaseHelper = new DatabaseHelper(this);
        String jam = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String menit = String.valueOf(c.get(Calendar.MINUTE));
        String semua = jam +":"+ menit;
        ModelAlarm alarm = new ModelAlarm();

        alarm.setJam(semua);

        databaseHelper.insertData(alarm);

        Toast.makeText(this, " Alarm disimpan.", Toast.LENGTH_SHORT).show();

    }


}

