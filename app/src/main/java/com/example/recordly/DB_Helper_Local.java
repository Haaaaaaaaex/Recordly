package com.example.recordly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Helper_Local extends SQLiteOpenHelper {

    public DB_Helper_Local(Context context) {

        super(context, "RECORDLY", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sched = "create table SCHEDULE(sched_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SUBJECT TEXT, SECTION TEXT, TIME TEXT, TIME_END TEXT)";
        sqLiteDatabase.execSQL(sched);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists SCHEDULE");
        onCreate(db);
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM SCHEDULE";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db !=null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }

    //SCHEDULE INSERT TABLE
    public boolean insertSchedule (String SUBJECT, String SECTION, String TIME, String TIME_END){

        SQLiteDatabase CapstoneDB = this.getWritableDatabase();
        CapstoneDB.beginTransaction();
        ContentValues SCHED = new ContentValues();
        try {
            SCHED.put("SUBJECT", SUBJECT);
            SCHED.put("SECTION", SECTION);
            SCHED.put("TIME", TIME);
            SCHED.put("TIME_END",TIME_END);
            CapstoneDB.insert("SCHEDULE", null, SCHED);
            CapstoneDB.setTransactionSuccessful();
        }catch (Exception e){ e.printStackTrace(); }
        finally {
            CapstoneDB.endTransaction();
            CapstoneDB.close();
        }
        return true;
    }

    //DELETE A ROW IN TABLE SCHEDULE
   public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("SCHEDULE", "sched_id=?", new String[]{row_id});

    }

    //DELETE ALL ROWS ON THE TABLE SCHEDULE
   public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SCHEDULE");
    }

    public Boolean updateData(String row_id, String SUBJECT, String SECTION, String TIME, String TIME_END){
        SQLiteDatabase CapstoneDB = this.getWritableDatabase();
        ContentValues sch = new ContentValues();
        sch.put("SUBJECT", SUBJECT);
        sch.put("SECTION", SECTION);
        sch.put("TIME", TIME);
        sch.put("TIME_END",TIME_END);
        long result = CapstoneDB.update("SCHEDULE", sch, "sched_id=?", new String[]{row_id});
        return result != -1;
    }
}
