package com.example.uasshakealarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


import com.example.uasshakealarm.Activty.ShakeActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class AppReceiver extends BroadcastReceiver {

    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 134;
    //set interval notifikasi 10 detik
    private int interval_seconds = 2000;
    private NotificationManager alarmNotificationManager;
    String NOTIFICATION_CHANNEL_ID = "rasupe_channel_id";
    String NOTIFICATION_CHANNEL_NAME = "rasupe channel";
    private int NOTIFICATION_ID = 1;
    static MediaPlayer player;
    SharedPreferences pref;
    String harusgoyang ="apake";

    DatabaseHelper databaseHelper;
    Calendar cal;
    String Nama,Kesulitan,NadaDering;
    int id,duration;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        this.id = extras.getInt("id");
        pref = context.getSharedPreferences("shake",MODE_PRIVATE);


        if(!pref.contains("HarusGoyang")){

            databaseHelper = new DatabaseHelper(context);
            ModelAlarm modelAlarm = databaseHelper.getDataById(this.id);
            Nama = modelAlarm.getNama();
            NadaDering = modelAlarm.getNadadering();
            updateSharedPreferences();
            Intent keShake = new Intent(context,ShakeActivity.class);
            keShake.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(keShake);
        }





        //selama alarm tidak di stop, makan akan terus mengirim notifikasi


        Intent alarmIntent = new Intent(context, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, alarmIntent, 0);


        playmusic(context);

        //set waktu sekarang berdasarkan interval
        cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, interval_seconds);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //set alarm manager dengan memasukkan waktu yang telah dikonversi menjadi milliseconds

        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, pendingIntent);
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);


        } else if (android.os.Build.VERSION.SDK_INT >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);


        } else {
            manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        }
        //kirim notifikasi

        sendNotification(context, intent);
    }


    public void updateSharedPreferences() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("HarusGoyang",harusgoyang);
        editor.putLong("Id_On",this.id);
        editor.putString("Nama_On", Nama);
        editor.putString("NadaDering_On",NadaDering);
        editor.commit();
    }

    private void playmusic(Context context) {

        String NadaDering = pref.getString("NadaDering_On","null");

        if(NadaDering.equals("asiaaap")){
            player = MediaPlayer.create(context, R.raw.asiaaap);

        }
        else if(NadaDering.equals("galaxy_s3_original")){
            player = MediaPlayer.create(context, R.raw.galaxy_s3_original);

        }
        else{
            player = MediaPlayer.create(context, R.raw.morning_flower);
        }


                this.duration = player.getDuration();
                this.interval_seconds = this.duration + 100;
        player.start();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                mp.release();

            };
        });



    }
    public static void stopMusic(){
        if(player.isPlaying()){
        player.stop();
        }

    }

    //handle notification
    private void sendNotification(Context context, Intent intent) {
        String Nama = pref.getString("Nama_On","null");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        String datetimex = sdf.format(new Date());
        String notif_title = ""+Nama;
        String notif_content = "Notif time "+datetimex;

        alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent newIntent = new Intent(context,  ShakeActivity.class);
        newIntent.putExtra("notifkey", "notifvalue");


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //cek jika OS android Oreo atau lebih baru
        //kalau tidak di set maka notifikasi tidak akan muncul di OS tersebut

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            alarmNotificationManager.createNotificationChannel(mChannel);
        }

        //Buat notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        alamNotificationBuilder.setContentTitle(notif_title);
        alamNotificationBuilder.setSmallIcon(R.mipmap.logoicon);
        alamNotificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        alamNotificationBuilder.setContentText(notif_content);
        alamNotificationBuilder.setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);

        //Tampilkan notifikasi
        alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());

    }
}