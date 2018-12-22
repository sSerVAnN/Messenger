package com.example.lenovo.chat;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "USERS";
    public static String TABLE_NAME = "USERS";
    public static String TABLE_INFO = "INFORMATION";


    public static final String KEY_NAME = "name";
    public static final String KEY_MAIL = "mail";
    public static final String KEY_AVATAR = "photo_url";
    public static final String KEY_PHONE = "phone number";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_2nd = "id";





    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key," + KEY_NAME + " text" + ")");

        db.execSQL("create table " + TABLE_INFO + "(" + COLUMN_ID_2nd + " integer primary key," + KEY_MAIL + " text," + KEY_AVATAR + " text" + ")");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        db.execSQL("drop table if exists " + TABLE_INFO);
        onCreate(db);
    }
}
