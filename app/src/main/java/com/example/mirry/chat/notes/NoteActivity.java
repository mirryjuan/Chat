package com.example.mirry.chat.notes;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.ChatActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class NoteActivity extends Activity {
//    private Button textbtn,imgbtn,videobtn,audiobtn;
    private long lastClickTime = 0;
    private ListView lv;
    private Intent i;
    private MyAdapter adapter;
    private NotesDB notesDB;
    private SQLiteDatabase dbReader,dbWriter;
    private Cursor cursor;


    private SubActionButton type_text;
    private SubActionButton type_img;
    private SubActionButton type_audio;
    private SubActionButton type_video;
    private FloatingActionMenu actionMenu;

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
        initTypeView();
        initView();
    }

    private void initTypeView() {
        final ImageView add = new ImageView(this);
        add.setImageResource(R.drawable.menu_add);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(add)
                .build();


        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        type_text = itemBuilder.setContentView(itemIcon).build();

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.picture));
        type_img = itemBuilder.setContentView(itemIcon2).build();

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable(getResources().getDrawable(R.drawable.voice));
        type_audio = itemBuilder.setContentView(itemIcon3).build();

        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        type_video = itemBuilder.setContentView(itemIcon4).build();

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(type_text)
                .addSubActionView(type_img)
                .addSubActionView(type_audio)
                .addSubActionView(type_video)
                .attachTo(actionButton)
                .build();

        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {

            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // 逆时针旋转90°
                add.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, -90);

                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(add, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // 顺时针旋转90°
                add.setRotation(-90);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(add, pvhR);
                animation.start();

            }
        });

        setButtonsClickListener();
    }

    private void setButtonsClickListener() {
        i = new Intent(this,AddContentActivity.class);
        type_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                Toast.makeText(NoteActivity.this, "文字", Toast.LENGTH_SHORT).show();
                i.putExtra("flag","1");
                startActivity(i);
            }
        });

        type_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                Toast.makeText(NoteActivity.this, "图片", Toast.LENGTH_SHORT).show();
                i.putExtra("flag","1");
                startActivity(i);
            }
        });

        type_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                Toast.makeText(NoteActivity.this, "音频", Toast.LENGTH_SHORT).show();
                i.putExtra("flag","1");
                startActivity(i);
            }
        });

        type_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                Toast.makeText(NoteActivity.this, "视频", Toast.LENGTH_SHORT).show();
                i.putExtra("flag","1");
                startActivity(i);
            }
        });
    }

    public void initView(){
        lv = (ListView) findViewById(R.id.list);
//        textbtn = (Button) findViewById(R.id.text);
//        imgbtn = (Button) findViewById(R.id.img);
//        videobtn = (Button) findViewById(R.id.video);
//        audiobtn = (Button) findViewById(R.id.audio);
//        textbtn.setOnClickListener(this);
//        imgbtn.setOnClickListener(this);
//        videobtn.setOnClickListener(this);
//        audiobtn.setOnClickListener(this);
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