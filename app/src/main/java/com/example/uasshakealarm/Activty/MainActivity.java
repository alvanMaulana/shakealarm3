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
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



import com.example.uasshakealarm.AppReceiver;
import com.example.uasshakealarm.DatabaseHelper;
import com.example.uasshakealarm.ModelAlarm;
import com.example.uasshakealarm.R;
import com.example.uasshakealarm.TimePickerFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, RadioGroup.OnCheckedChangeListener{
    private PendingIntent pendingIntent;
    Calendar c;
    DatabaseHelper databaseHelper;
    TextView txtJam;
    EditText nama;
    RadioButton nilaiAwal;
    RadioGroup kesulitan;
    Spinner nadadering;
     String RadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtJam = (TextView)findViewById(R.id.jam);
        nama = (EditText)findViewById(R.id.nama);
        kesulitan = (RadioGroup)findViewById(R.id.kesulitan);
        nilaiAwal = (RadioButton)findViewById(R.id.M);
        nilaiAwal.setChecked(true);
        RadioGroup = "M";
        //menentukan nilai awal , 2 karena ada space di xml
        kesulitan.setOnCheckedChangeListener(this);
        nadadering = (Spinner)findViewById(R.id.nadaDering);







        c = Calendar.getInstance();
        txtJam.setText(""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));



        final Intent toLihat = new Intent(MainActivity.this,LihatAlarm.class);


        txtJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
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

    private int getID() {
        databaseHelper = new DatabaseHelper(this);
        return databaseHelper.getID();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        this.c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.c.set(Calendar.MINUTE, minute);
        this.c.set(Calendar.SECOND, 0);
        txtJam.setText(""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
    }

    public void startAlarmManager(Calendar c ) {
        int a = getID();
        Intent alarmIntent = new Intent(MainActivity.this, AppReceiver.class);
        alarmIntent.putExtra("id", getID());
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, a, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);


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
        String nama = this.nama.getText().toString().trim();
        String Kesulitan = RadioGroup;
        String nadadering = this.nadadering.getSelectedItem().toString();
        int jam = (c.get(Calendar.HOUR_OF_DAY));
        int menit = (c.get(Calendar.MINUTE));
        int checked = 1;

        ModelAlarm alarm = new ModelAlarm();

        alarm.setJam(jam);
        alarm.setMenit(menit);
        alarm.setChecked(checked);
        alarm.setNama(nama);
        alarm.setKesulitan(Kesulitan);
        alarm.setNadadering(nadadering);

        databaseHelper.insertData(alarm);

        Toast.makeText(this, " Alarm telah disimpan."+ nama + Kesulitan + nadadering, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioButtonId);
        RadioGroup = radioButton.getText().toString();

    }
}

