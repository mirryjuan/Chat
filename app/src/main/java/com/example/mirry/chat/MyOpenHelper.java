package com.example.mirry.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mirry on 2017/4/2.
 */

public class MyOpenHelper extends SQLiteOpenHelper {

    public MyOpenHelper(Context context) {
        super(context, "Chat.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "head VARCHAR(1024) NOT NULL," +
                "sex VARCHAR(2) NOT NULL," +
                "nickname VARCHAR(64) NOT NULL," +
                "account VARCHAR(32) NOT NULL," +
                "password VARCHAR(16) NOT NULL," +
                "phone VARCHAR(15)," +
                "birthday VARCHAR(30))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
