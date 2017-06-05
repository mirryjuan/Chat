package com.example.mirry.chat.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.mirry.chat.R;

import java.io.IOException;

public class SelectAct extends Activity  implements View.OnClickListener,MediaPlayer.OnCompletionListener {
    private Button s_delete,s_back,s_playRecord,s_pause,s_stop;
    private ImageView s_img;
    private VideoView s_video;
    private TextView s_tv;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private MediaPlayer mediaPlayer=null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        s_delete = (Button) findViewById(R.id.s_delete);
        s_back = (Button) findViewById(R.id.back);
        s_img = (ImageView) findViewById(R.id.s_img);
        s_video = (VideoView) findViewById(R.id.s_video);
        s_tv = (TextView) findViewById(R.id.s_tv);
        s_playRecord = (Button) findViewById(R.id.s_playRecord);
        s_pause = (Button) findViewById(R.id.s_pause);
        s_stop = (Button) findViewById(R.id.s_stop);
        notesDB = new NotesDB(this);
//        获得可写数据库
        dbWriter = notesDB.getWritableDatabase();
//        为音频控制的三个按钮添加单击事件响应
        s_playRecord.setOnClickListener(this);
        s_pause.setOnClickListener(this);
        s_stop.setOnClickListener(this);

//        为返回和删除按钮添加单击事件响应
        s_back.setOnClickListener(this);
        s_delete.setOnClickListener(this);
        if (getIntent().getStringExtra(NotesDB.PATH).equals("null")){
            s_img.setVisibility(View.GONE);
        }else{
            s_img.setVisibility(View.VISIBLE);
        }
        if (getIntent().getStringExtra(NotesDB.VIDEO).equals("null")){
            s_video.setVisibility(View.GONE);
        }else{
            s_video.setVisibility(View.VISIBLE);
        }
//        判断是否有添加音频
        if (getIntent().getStringExtra(NotesDB.AUDIO).equals("null")){
            s_playRecord.setVisibility(View.GONE);
            s_pause.setVisibility(View.GONE);
            s_stop.setVisibility(View.GONE);
        }else {
            s_playRecord.setVisibility(View.VISIBLE);
            s_pause.setVisibility(View.VISIBLE);
            s_stop.setVisibility(View.VISIBLE);
        }
        s_tv.setText(getIntent().getStringExtra(NotesDB.CONTENT));
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra(NotesDB.PATH));
        s_img.setImageBitmap(bitmap);
//        设置视频URI
        s_video.setVideoURI(Uri.parse(getIntent().getStringExtra(NotesDB.VIDEO)));
        s_video.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s_delete:
                new AlertDialog.Builder(this).setMessage("确定删除么?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         delete();
                        finish();
                    }
                })
                        .setNegativeButton("取消", null).show();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    s_pause.setText("暂停播放");
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.s_playRecord://播放SD卡上的音频文件
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(this);
                    try {
                        mediaPlayer.setDataSource(getIntent().getStringExtra(NotesDB.AUDIO));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                setTitle("正在播放");
                    break;
            case R.id.s_stop:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer = null;
                    s_pause.setText("暂停播放");
                        setTitle("停止播放");
                    }else{
                        mediaPlayer = null;
                        s_pause.setText("暂停播放");
                        setTitle("停止播放");
                    }
                }
                break;
            case R.id.s_pause:
                if (mediaPlayer != null) {
                    System.out.println("满足外层条件");
                    if (s_pause.getText().toString().equals("继续播放")) {
                        System.out.println("满足条件1");
                        mediaPlayer.start();
                        s_pause.setText("暂停播放");
                        setTitle("正在播放");
                    } else if (s_pause.getText().toString().equals("暂停播放")) {
                        System.out.println("满足条件2");
                        mediaPlayer.pause();
                        s_pause.setText("继续播放");
                        setTitle("暂停播放");
                    }
                    break;
                }
            default:
                break;
        }

    }



//用于删除记录
    public void delete(){
        dbWriter.delete(NotesDB.TABLE_NAME, "_id=" + getIntent().getIntExtra(NotesDB.ID, 0), null);
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            s_pause.setText("暂停播放");
        }
        super.onBackPressed();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer = null;
        setTitle("播放完毕");
    }
}
