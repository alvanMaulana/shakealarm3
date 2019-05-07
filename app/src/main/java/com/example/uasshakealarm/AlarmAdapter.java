package com.example.uasshakealarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.uasshakealarm.Activty.LihatAlarm;
import com.example.uasshakealarm.Activty.MainActivity;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private final Context mContext;
    private ArrayList<ModelAlarm> dataList;
    public DatabaseHelper databaseHelper ;
    public ModelAlarm modelAlarm;
    public PendingIntent pendingIntent;
    private int NOTIFICATION_ID = 1;
    private int ALARM_REQUEST_CODE = 134;

    public AlarmAdapter(Context context,ArrayList<ModelAlarm> arrayList) {
        this.dataList = arrayList;
        this.mContext = context;

        databaseHelper = new DatabaseHelper(context);
        modelAlarm = new ModelAlarm();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_itemnya, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, final int position) {
        holder.jam.setText(dataList.get(position).getJam());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHelper = new DatabaseHelper(mContext);
                        databaseHelper.deleteData(dataList.get(position).getId());
                        Toast.makeText(mContext, "Deleted Successfully!", Toast.LENGTH_SHORT).show();

                        Intent alarmIntent = new Intent(mContext, AppReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(mContext, dataList.get(position).getId(), alarmIntent, 0);

                        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(mContext.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);

                        NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(NOTIFICATION_ID);

                        Intent intent = new Intent(mContext, LihatAlarm.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        private TextView jam;
        private Switch ganti;
        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            jam = (TextView) itemView.findViewById(R.id.Jam);
            ganti = (Switch) itemView.findViewById(R.id.switch1);
        }
    }
}
