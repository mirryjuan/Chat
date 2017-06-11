package com.example.mirry.chat.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.ImageUtil;
import com.example.mirry.chat.view.IconFontTextView;

import java.io.IOException;

public class SelectAct extends Activity  implements View.OnClickListener,MediaPlayer.OnCompletionListener {
    private Button s_playRecord,s_pause,s_stop;
    private IconFontTextView s_delete,s_back;
    private ImageView s_img;
    private VideoView s_video;
    private EditText s_et;
    private NotesDBHelper notesDB;
    private SQLiteDatabase dbWriter;
    private MediaPlayer mediaPlayer=null;
    private IconFontTextView save;
    private IconFontTextView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        save = (IconFontTextView) findViewById(R.id.save);
        edit = (IconFontTextView) findViewById(R.id.edit);
        s_delete = (IconFontTextView) findViewById(R.id.s_delete);
        s_back = (IconFontTextView) findViewById(R.id.back);
        s_img = (ImageView) findViewById(R.id.s_img);
        s_video = (VideoView) findViewById(R.id.s_video);
        s_et = (EditText) findViewById(R.id.s_et);
        s_playRecord = (Button) findViewById(R.id.s_playRecord);
        s_pause = (Button) findViewById(R.id.s_pause);
        s_stop = (Button) findViewById(R.id.s_stop);
        notesDB = new NotesDBHelper(this);
//        获得可写数据库
        dbWriter = notesDB.getWritableDatabase();
//        为音频控制的三个按钮添加单击事件响应
        s_playRecord.setOnClickListener(this);
        s_pause.setOnClickListener(this);
        s_stop.setOnClickListener(this);

//        为返回和删除按钮添加单击事件响应
        s_back.setOnClickListener(this);
        s_delete.setOnClickListener(this);

        edit.setOnClickListener(this);
        save.setOnClickListener(this);

        if (getIntent().getStringExtra(NotesDBHelper.PATH).equals("null")){
            s_img.setVisibility(View.GONE);
        }else{
            s_img.setVisibility(View.VISIBLE);
        }
        if (getIntent().getStringExtra(NotesDBHelper.VIDEO).equals("null")){
            s_video.setVisibility(View.GONE);
        }else{
            s_video.setVisibility(View.VISIBLE);
        }
//        判断是否有添加音频
        if (getIntent().getStringExtra(NotesDBHelper.AUDIO).equals("null")){
            s_playRecord.setVisibility(View.GONE);
            s_pause.setVisibility(View.GONE);
            s_stop.setVisibility(View.GONE);
        }else {
            s_playRecord.setVisibility(View.VISIBLE);
            s_pause.setVisibility(View.GONE);
            s_stop.setVisibility(View.VISIBLE);
        }
        s_et.setText(getIntent().getStringExtra(NotesDBHelper.CONTENT));
        Bitmap imageThumbnail = ImageUtil.getImageThumbnail(getIntent().getStringExtra(NotesDBHelper.PATH), 200, 200);
        //Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra(NotesDBHelper.PATH));
        s_img.setImageBitmap(imageThumbnail);
//        设置视频URI
        s_video.setVideoURI(Uri.parse(getIntent().getStringExtra(NotesDBHelper.VIDEO)));
        s_video.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit:
                s_et.setFocusable(true);
                s_et.setFocusableInTouchMode(true);
                save.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                break;
            case R.id.save:
                s_et.setFocusable(false);
                s_et.setFocusableInTouchMode(false);
                save.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                // TODO: 2017/6/10 更新数据库
                NotesDBHelper notesDBHelper = new NotesDBHelper(SelectAct.this);
                SQLiteDatabase db = notesDBHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(NotesDBHelper.CONTENT,s_et.getText().toString());
                db.update(NotesDBHelper.TABLE_NAME,contentValues,notesDBHelper.ID+"=?",
                        new String[]{""+getIntent().getIntExtra(NotesDBHelper.ID, 0)});
                Toast.makeText(SelectAct.this, "日志更新成功", Toast.LENGTH_SHORT).show();
                db.close();
                break;
            case R.id.s_delete:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }else{
                    mediaPlayer = null;
                }
                new AlertDialog.Builder(this).setMessage("确定删除么?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         delete();
                        finish();
                    }
                })
                        .setNegativeButton("取消", null).show();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.s_playRecord://播放SD卡上的音频文件
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(this);
                    try {
                        mediaPlayer.setDataSource(getIntent().getStringExtra(NotesDBHelper.AUDIO));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    mediaPlayer.start();
                }
                s_playRecord.setVisibility(View.GONE);
                s_pause.setVisibility(View.VISIBLE);
                break;
            case R.id.s_stop:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    }else{
                        mediaPlayer = null;
                    }
                    Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.s_pause:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }else{
                    mediaPlayer = null;
                }
                s_playRecord.setVisibility(View.VISIBLE);
                s_pause.setVisibility(View.GONE);
                break;

            default:
                break;
        }

    }



//用于删除记录
    public void delete(){
        dbWriter.delete(NotesDBHelper.TABLE_NAME, "_id=" + getIntent().getIntExtra(NotesDBHelper.ID, 0), null);
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
