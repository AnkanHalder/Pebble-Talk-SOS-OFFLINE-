package com.example.samscots.sosoffine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sam Scots on 12/20/2017.
 */

public class SQLDB extends SQLiteOpenHelper {

    private static final String tableName="ChatRecord";
    private static final String COLUMN1 = "ADDRESS";
    private static final String COLUMN2 = "DEVICENAME";
    private static final String COLUMN3 = "LASTMESSAGE";
    private static final String COLUMN4 = "PROFILEURI";


    public SQLDB(Context context) {
        super(context, tableName, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table="CREATE TABLE IF NOT EXISTS "+tableName+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN1 +" TEXT,"+COLUMN2+" TEXT,"+COLUMN3+" TEXT,"+COLUMN4+" TEXT"+")";
        sqLiteDatabase.execSQL(create_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+tableName);
        onCreate(sqLiteDatabase);
    }
    public void addsqlData(String add,String devicename,String lastmsg,String profileuri){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN1,add);
        cv.put(COLUMN2,devicename);
        cv.put(COLUMN3,lastmsg);
        cv.put(COLUMN4,profileuri);
        db.insert(tableName,null,cv);
    }
    public Cursor getAllData(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor data=db.rawQuery("SELECT * FROM "+tableName,null);
        return data;
    }
    public void update(String add,String devicename,String lastmsg,String profileuri,int id){
        int i;
        ContentValues cv=new ContentValues();
        cv.put(COLUMN1,add);
        cv.put(COLUMN2,devicename);
        cv.put(COLUMN3,lastmsg);
        cv.put(COLUMN4,profileuri);
        SQLiteDatabase db=this.getReadableDatabase();
        i=db.update(tableName,cv,"_id="+id,null);
        if(i>0)
            Log.d("SQLDB","SUCCESSFULLLLLLLLY DONE");
        else
            Log.d("SQLDB","FAILLLLLLLLLLLLLLLLLLLLLLLLED");
    }
    public void delete(){
        SQLiteDatabase db=this.getReadableDatabase();
        db.delete(tableName, null, null);
    }

}
