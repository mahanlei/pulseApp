package com.example.apple.pluseapp.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apple.pluseapp.util.DBHelper;
import com.example.apple.pluseapp.util.Message;

public class UserDao {
    public Message isExist(Context context,String userName) {
        SQLiteDatabase db= new DBHelper(context,"mu.db",null,1).getWritableDatabase();

        Cursor cursor=db.rawQuery("SELECT * FROM userInfo WHERE userName = ?",
                new String[]{userName});
        if(cursor.moveToFirst())
        {
            return Message.EXIST;
        }
        cursor.close();
        return Message.NOT_EXIST;
    }
}
