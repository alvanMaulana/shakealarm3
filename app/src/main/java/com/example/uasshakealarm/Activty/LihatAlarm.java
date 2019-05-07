package com.example.uasshakealarm.Activty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.uasshakealarm.AlarmAdapter;
import com.example.uasshakealarm.DatabaseHelper;
import com.example.uasshakealarm.ModelAlarm;
import com.example.uasshakealarm.R;

import java.util.ArrayList;

public class LihatAlarm extends AppCompatActivity {
    SharedPreferences pref;
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    public static ArrayList<ModelAlarm> alarmArrayList = new ArrayList<ModelAlarm>();
    public DatabaseHelper databaseHelper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_alarm);
        checkGoyang();

        databaseHelper =  new DatabaseHelper(this);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        alarmArrayList.clear();
        alarmArrayList.addAll(databaseHelper.getData());

        adapter = new AlarmAdapter(this,alarmArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMain = new Intent(LihatAlarm.this,MainActivity.class);
                startActivity(toMain);
            }
        });
    }

    private void checkGoyang() {
        pref = getSharedPreferences("shake",MODE_PRIVATE);
        Intent toShake = new Intent(LihatAlarm.this,ShakeActivity.class);
        if(pref.contains("HarusGoyang")) {
            startActivity(toShake);
        }
    }
}
