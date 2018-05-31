package com.example.apple.pluseapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

   public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "pulseAppDB.db", null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE userInfo (\n" +
                "  userName nvarchar(10) PRIMARY KEY NOT NULL,\n" +
                "  age integer(3) NOT NULL,\n" +
                "  gender integer(1) NOT NULL,\n" +
                "  password varchar(15) NOT NULL,\n" +
                "  rank integer(1),\n" +
                "  detail nvarchar(128)\n" +
                ")");
        sqLiteDatabase.execSQL("INSERT INTO userInfo values( 'ml',19,1,123,2,'非常健康,超过80%同龄人')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("ALTER TABLE userInfo ADD phone VARCHAR(12) NULL");
    }
}
