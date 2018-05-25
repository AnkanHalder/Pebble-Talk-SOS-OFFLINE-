package com.example.samscots.sosoffine;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Sam Scots on 12/3/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static String table_name;
    private static final String COL2 = "Frm";
    private static final String COL1 = "message";

    public SQLiteHelper(Context context, String name) {
        super(context, name, null, 1);
        table_name=name;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table="CREATE TABLE IF NOT EXISTS "+table_name+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 +" TEXT,"+COL2+" INTEGER)";
        Log.d("SQLITE",create_table);
        try {
            sqLiteDatabase.execSQL(create_table);
            Log.d("SQLITE","Table Created");
        }catch (Exception e){
            Log.d("SQLITE",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS "+table_name);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(String msg,int frm) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COL1,msg);
        cv.put(COL2,frm);
//        Log.d("SQLiteHeleper","Inserting  data "+msg+" from"+frm+" into"+table_name);
        long result=db.insert(table_name,null,cv);
        if(result==-1)
            return false;
        else
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db=this.getReadableDatabase();

        String create_table="CREATE TABLE IF NOT EXISTS "+table_name+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 +" TEXT,"+COL2+" INTEGER)";
        Log.d("SQLITE",create_table);
        try {
            db.execSQL(create_table);
            Log.d("SQLITE","Table Created");
        }catch (Exception e){
            Log.d("SQLITE",e.getMessage());
        }


        Cursor data=db.rawQuery("SELECT * FROM "+table_name,null);
        return data;
    }

    public void del_table(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+table_name);
        Log.d("SQLITE","Table Dropped");
    }
}
