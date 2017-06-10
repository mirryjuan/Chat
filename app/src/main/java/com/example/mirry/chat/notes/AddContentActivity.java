package com.example.mirry.chat.notes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.IconFontTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddContentActivity extends Activity implements View.OnClickListener{
    private String val = null;
    private Button savebtn,cancelbtn,btnRecord,btnStop,btnPlay,btnDelete;
    private EditText ettext;
    private ImageView c_img;
    private VideoView v_video;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;
    private File phoneFile,videoFile;
    private File recordAudioFile = null;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private IconFontTextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
//        获取传来的flag值
        val = getIntent().getStringExtra("flag");
        back = (IconFontTextView) findViewById(R.id.back);
        savebtn = (Button) findViewById(R.id.save);
        cancelbtn = (Button) findViewById(R.id.cancel);
        ettext = (EditText) findViewById(R.id.ettext);
        c_img = (ImageView) findViewById(R.id.c_img);
        v_video = (VideoView) findViewById(R.id.v_video);
        btnRecord = (Button) findViewById(R.id.record);
        btnStop = (Button) findViewById(R.id.stop);
        btnPlay = (Button) findViewById(R.id.play);
        btnDelete = (Button) findViewById(R.id.delete);
        back.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        notesDB = new NotesDB(this);
        dbWriter = notesDB.getWritableDatabase();
//        初始化视图
        initView();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.save:
                    addDB();
                    if (mediaPlayer!=null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    finish();
                    break;
                case R.id.cancel:
                    if (mediaPlayer!=null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    finish();
                    break;
                case R.id.record:  //录制音频
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        recordAudioFile = new File(Environment.getExternalStorageDirectory()+"/"+
                                getTime() + ".amr");
                    }
                    mediaRecorder = new MediaRecorder();
//                    设置音源
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    设置音频格式
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//                    采用默认音频编码
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                    设置输出路径
                    mediaRecorder.setOutputFile(recordAudioFile.getAbsolutePath());
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    Toast.makeText(AddContentActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.stop:
                    if (mediaRecorder != null && recordAudioFile.exists()) {
//                        停止录制
                        mediaRecorder.stop();
//                        释放资源
                        mediaRecorder.release();
                        mediaRecorder = null;
                        Toast.makeText(AddContentActivity.this, "停止录制", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.play:
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(recordAudioFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    break;
                case R.id.delete:
                    recordAudioFile.delete();
                    break;
                default:
                    break;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void addDB(){
        ContentValues cv = new ContentValues();
        cv.put(NotesDB.CONTENT,ettext.getText().toString());
        cv.put(NotesDB.TIME,getTime());
        cv.put(NotesDB.PATH,phoneFile+"");
        cv.put(NotesDB.VIDEO,videoFile+"");
        cv.put(NotesDB.AUDIO, recordAudioFile + "");
        dbWriter.insert(NotesDB.TABLE_NAME,null,cv);
    }

    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Date date = new Date();
        return format.format(date);
    }

    public void initView(){
        if (val.equals("1")){//文字
            c_img.setVisibility(View.GONE);
            v_video.setVisibility(View.GONE);
            btnRecord.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
        if (val.equals("2")){//图文
            c_img.setVisibility(View.VISIBLE);
            v_video.setVisibility(View.GONE);
            btnRecord.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
//            照相意图
            Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            phoneFile = new File(Environment.getExternalStorageDirectory()+"/"+
                    getTime()+".jpg");
            iimg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(phoneFile));
            startActivityForResult(iimg,1);
        }

        if (val.equals("3")){//音频
            c_img.setVisibility(View.GONE);
            v_video.setVisibility(View.GONE);
            btnRecord.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        if (val.equals("4")){//视频
            c_img.setVisibility(View.GONE);
            v_video.setVisibility(View.VISIBLE);
            btnRecord.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
//            录制视频意图
            Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            videoFile = new File(Environment.getExternalStorageDirectory()+"/"+
                    getTime()+".mp4");
            video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
            video.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
            startActivityForResult(video, 2);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Bitmap bitmap = BitmapFactory.decodeFile(phoneFile.getAbsolutePath());
            c_img.setImageBitmap(bitmap);
        }
        if (requestCode == 2){
            v_video.setVideoURI(Uri.fromFile(videoFile));
            v_video.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
