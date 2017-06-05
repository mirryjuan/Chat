package com.example.mirry.chat.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mirry.chat.R;

public class NoteActivity extends Activity implements View.OnClickListener{
    private Button textbtn,imgbtn,videobtn,audiobtn;
    private long lastClickTime = 0;
    private ListView lv;
    private Intent i;
    private MyAdapter adapter;
    private NotesDB notesDB;
    private SQLiteDatabase dbReader,dbWriter;
    private Cursor cursor;
    @Override
    protected void onResume() {
        super.onResume();
        dbReader = notesDB.getReadableDatabase();
        selectDB();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initView();
    }

    public void initView(){
        lv = (ListView) findViewById(R.id.list);
        textbtn = (Button) findViewById(R.id.text);
        imgbtn = (Button) findViewById(R.id.img);
        videobtn = (Button) findViewById(R.id.video);
        audiobtn = (Button) findViewById(R.id.audio);
        textbtn.setOnClickListener(this);
        imgbtn.setOnClickListener(this);
        videobtn.setOnClickListener(this);
        audiobtn.setOnClickListener(this);
        notesDB = new NotesDB(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent i = new Intent(NoteActivity.this,SelectAct.class);
                i.putExtra(NotesDB.ID,cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                i.putExtra(NotesDB.CONTENT,cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
                i.putExtra(NotesDB.TIME,cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
//                获取图片和视频路径
                i.putExtra(NotesDB.PATH,cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
                i.putExtra(NotesDB.VIDEO,cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
                i.putExtra(NotesDB.AUDIO,cursor.getString(cursor.getColumnIndex(NotesDB.AUDIO)));
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                final int itemId = cursor.getInt(cursor.getColumnIndex("_id"));
                dbWriter = notesDB.getWritableDatabase();
                new AlertDialog.Builder(NoteActivity.this).setMessage("确定删除该记录么?").setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbWriter.delete(NotesDB.TABLE_NAME, "_id=?", new String[]{"" + itemId});
                        Toast.makeText(NoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        selectDB();
                    }
                })
                        .setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        i = new Intent(this,AddContentActivity.class);
        switch(v.getId()){
            case R.id.text:
                i.putExtra("flag","1");
                startActivity(i);
                break;
            case R.id.img:
                i.putExtra("flag","2");
                startActivity(i);
                break;
            case  R.id.audio:
                i.putExtra("flag","3");
                startActivity(i);
                break;
            case R.id.video:
                i.putExtra("flag","4");
                startActivity(i);
                break;
            default:
                break;
        }
    }

    public void selectDB(){
        cursor = dbReader.query(NotesDB.TABLE_NAME,null,null,null,null,null,null);
        adapter = new MyAdapter(this,cursor);
        lv.setAdapter(adapter);
    }
//处理主页面的后退事件
    @Override
    public void onBackPressed() {
        if (lastClickTime<=0){
            Toast.makeText(NoteActivity.this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
            lastClickTime =System.currentTimeMillis();
        }else{
            long currentTime = System.currentTimeMillis();
            if (currentTime-lastClickTime<1000){
                finish();//退出应用
            }else{
                Toast.makeText(NoteActivity.this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
                lastClickTime = currentTime;
            }
        }
    }
}