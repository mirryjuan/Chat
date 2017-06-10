package com.example.mirry.chat.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/22.
 */
public class NotesDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "notes";
    public static final String CONTENT = "content";
    public static final String PATH = "path";
    public static final String VIDEO = "video";
    public static final String AUDIO = "audio";
    public static final String ID = "_id";
    public static final String TIME = "time";
    public NotesDBHelper(Context context) {
        super(context,"notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建数据库
        db.execSQL("CREATE TABLE "+TABLE_NAME+"("+
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                CONTENT+" TEXT NOT NULL,"+
                PATH+" TEXT DEFAULT NONE,"+
                VIDEO+" TEXT DEFAULT NONE,"+
                AUDIO+" TEXT DEFAULT NONE,"+
                TIME+" TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
