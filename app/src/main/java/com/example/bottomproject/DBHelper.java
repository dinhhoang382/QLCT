package com.example.bottomproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "QLCT.db";
    private static final String TABLE_NAME_LOAICHI = "LoaiChi";
    private static final String COLUMN_ID_LOAICHI = "idLoaiChi";
    private static final String COLUMN_NAME_LOAICHI = "tenLoaiChi";
    private static final String CREATE_TABLE_LOAICHI =
            "CREATE TABLE " + TABLE_NAME_LOAICHI
                    + "(" + COLUMN_ID_LOAICHI + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME_LOAICHI + " TEXT NOT NULL"
                    + ")";
    private static final String TABLE_NAME_LOAITHU = "LoaiThu";
    private static final String COLUMN_ID_LOAITHU = "idLoaiThu";
    private static final String COLUMN_NAME_LOAITHU = "tenLoaiThu";
    private static final String CREATE_TABLE_LOAITHU =
            "CREATE TABLE " + TABLE_NAME_LOAITHU
            + "(" + COLUMN_ID_LOAITHU + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_LOAITHU + " TEXT NOT NULL"
            + ")";
   public DBHelper(Context context){
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOAICHI);
        addLoaiChi(db);
        db.execSQL(CREATE_TABLE_LOAITHU);
        addLoaiThu(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOAICHI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOAITHU);
        onCreate(db);

    }
    private void addLoaiChi(SQLiteDatabase db){
        db.execSQL("INSERT INTO " + TABLE_NAME_LOAICHI + " (" + COLUMN_NAME_LOAICHI + ") VALUES ('Ăn uống');");
        db.execSQL("INSERT INTO " + TABLE_NAME_LOAICHI + " (" + COLUMN_NAME_LOAICHI + ") VALUES ('Quần áo');");
        db.execSQL("INSERT INTO " + TABLE_NAME_LOAICHI + " (" + COLUMN_NAME_LOAICHI + ") VALUES ('Học phí');");
    }
    private void addLoaiThu(SQLiteDatabase db){
        db.execSQL("INSERT INTO " + TABLE_NAME_LOAITHU + " (" + COLUMN_NAME_LOAITHU + ") VALUES ('Tiền lương');");
        db.execSQL("INSERT INTO " + TABLE_NAME_LOAITHU + " (" + COLUMN_NAME_LOAITHU + ") VALUES ('Tiền thưởng');");
    }
    public List<String> getDBLoaiChi(){
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_NAME_LOAICHI, new String[]{COLUMN_NAME_LOAICHI}, null, null,null,null, null);
        int columnIndex = cursor.getColumnIndex(COLUMN_NAME_LOAICHI);
        if(columnIndex != -1) {
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(columnIndex));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return list;
    }
    public List<String> getDBLoaiThu(){
       List<String> list = new ArrayList<>();
       SQLiteDatabase db = this.getReadableDatabase();

       Cursor cursor = db.query(TABLE_NAME_LOAITHU, new String[]{COLUMN_NAME_LOAITHU}, null, null, null, null, null);
       int columnIndex = cursor.getColumnIndex(COLUMN_NAME_LOAITHU);
       if(columnIndex != -1){
           if(cursor.moveToFirst()){
               do {
                   list.add(cursor.getString(columnIndex));
               } while (cursor.moveToNext());
           }
       }
       cursor.close();
       db.close();
       return list;
    }
}

