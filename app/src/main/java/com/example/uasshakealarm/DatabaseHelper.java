package com.example.uasshakealarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DB_FILE_NAME = "concretepage";
    public DatabaseHelper(Context context) {
        super(context, DB_FILE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Alarm ( " +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " jam VARCHAR2(6))";
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

        SQLiteStatement stmt = db.compileStatement("INSERT INTO Alarm (jam) " +
                "VALUES (?)");
        stmt.bindString(1, isi.getJam());
        stmt.execute();
        stmt.close();
        db.close();


    }
    //Update data into table

    //Select all data from the table

    public List<ModelAlarm> getData() {
        List<ModelAlarm> isi = new ArrayList<ModelAlarm>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id,jam from Alarm ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ModelAlarm std = new ModelAlarm();
            std.setId(cursor.getInt(0));
            std.setJam(cursor.getString(1));


            isi.add(std);
        }
        db.close();
        return isi;
    }
    public void deleteData(int stdId){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("DELETE FROM Alarm WHERE id = ?");
        stmt.bindLong(1, stdId);
        stmt.execute();
        stmt.close();
        db.close();
    }
}
