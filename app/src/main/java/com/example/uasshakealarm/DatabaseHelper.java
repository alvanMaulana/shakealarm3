package com.example.uasshakealarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private Integer id;
    private static int DATABASE_VERSION = 1;
    private static String DB_FILE_NAME = "concretepage";
    public DatabaseHelper(Context context) {
        super(context, DB_FILE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Alarm ( " +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " nama varchar(50),"+
                " jam int,"+
                "menit int,"+
                "kesulitan varchar(5),"+
                "nadadering varchar(30),"+
                "checked int)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == oldVersion + 1) {
            //	db.execSQL("ALTER TABLE balita_info ADD COLUMN country VARCHAR(30)");
        }



    }

    public void insertData(ModelAlarm isi){
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("INSERT INTO Alarm (jam,menit,checked,nama,kesulitan,nadadering) " +
                "VALUES (?,?,?,?,?,?)");
        stmt.bindLong(1, isi.getJam());
        stmt.bindLong(2, isi.getMenit());
        stmt.bindLong(3, isi.getChecked());
        stmt.bindString(4, isi.getNama());
        stmt.bindString(5, isi.getKesulitan());
        stmt.bindString(6, isi.getNadadering());
        stmt.execute();
        stmt.close();
        db.close();


    }
    //Update data into table

    //Select all data from the table

    public List<ModelAlarm> getData() {
        List<ModelAlarm> isi = new ArrayList<ModelAlarm>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id,jam,menit,checked,nama,kesulitan,nadadering from Alarm ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ModelAlarm std = new ModelAlarm();
            std.setId(cursor.getInt(0));
            std.setJam(cursor.getInt(1));
            std.setMenit(cursor.getInt(2));
            std.setChecked(cursor.getInt(3));
            std.setNama(cursor.getString(4));
            std.setKesulitan(cursor.getString(5));
            std.setNadadering(cursor.getString(6));



            isi.add(std);
        }
        db.close();
        return isi;
    }

    public ModelAlarm getDataById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT jam, menit,checked,nama,kesulitan,nadadering FROM Alarm WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(id)});
        if(cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            ModelAlarm std = new ModelAlarm();
            std.setJam(cursor.getInt(0));
            std.setMenit(cursor.getInt(1));
            std.setChecked(cursor.getInt(2));
            std.setNama(cursor.getString(3));
            std.setKesulitan(cursor.getString(4));
            std.setNadadering(cursor.getString(5));
            db.close();

            return std;
        }
        return null;
    }

    public void deleteData(int stdId){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("DELETE FROM Alarm WHERE id = ?");
        stmt.bindLong(1, stdId);
        stmt.execute();
        stmt.close();
        db.close();

    }

    public int getID(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  *  FROM Alarm";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToLast()){
            id = cursor.getInt(0);

        }
        cursor.close();
        return id;

    }
    public void updateData(int id, int checked){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("UPDATE Alarm SET checked=?"+
                "WHERE id = ?");
        stmt.bindLong(1, checked);
        stmt.bindLong(2, id);

        stmt.execute();
        stmt.close();
        db.close();


    }




}
