package com.example.mirry.chat.notes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.utils.ImageUtil;
import com.example.mirry.chat.view.IconFontTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddContentActivity extends Activity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    private String val = null;
//    private Button btnRecord,btnStop,btnPlay,btnDelete;
    private EditText ettext;
    private ImageView c_img;
    private VideoView v_video;
    private NotesDBHelper notesDB;
    private SQLiteDatabase dbWriter;
    private File phoneFile,videoFile;
    private File recordAudioFile = null;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private IconFontTextView back,save;
    private LinearLayout player;
    private Button s_playRecord,s_pause,s_stop;
    private String audioPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
//        获取传来的flag值
        val = getIntent().getStringExtra("flag");
        player = (LinearLayout) findViewById(R.id.audio_player);
        s_playRecord = (Button) findViewById(R.id.s_playRecord);
        s_pause = (Button) findViewById(R.id.s_pause);
        s_stop = (Button) findViewById(R.id.s_stop);
        back = (IconFontTextView) findViewById(R.id.back);
        save = (IconFontTextView) findViewById(R.id.save);
        ettext = (EditText) findViewById(R.id.ettext);
        c_img = (ImageView) findViewById(R.id.c_img);
        v_video = (VideoView) findViewById(R.id.v_video);

//        为音频控制的三个按钮添加单击事件响应
        s_playRecord.setOnClickListener(this);
        s_pause.setOnClickListener(this);
        s_stop.setOnClickListener(this);

        back.setOnClickListener(this);
        save.setOnClickListener(this);
        notesDB = new NotesDBHelper(this);
        dbWriter = notesDB.getWritableDatabase();
//        初始化视图
        initView();
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save:
                    addDB();
                    if (mediaPlayer!=null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    finish();
                    break;
                case R.id.back:
                    if (mediaPlayer!=null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    finish();
                    break;
                case R.id.s_playRecord://播放SD卡上的音频文件
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnCompletionListener(this);
                        try {
                            mediaPlayer.setDataSource(audioPath);
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
//                case R.id.record:  //录制音频
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        recordAudioFile = new File(Environment.getExternalStorageDirectory()+"/"+
//                                getTime() + ".amr");
//                    }
//                    mediaRecorder = new MediaRecorder();
////                    设置音源
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
////                    设置音频格式
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
////                    采用默认音频编码
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
////                    设置输出路径
//                    mediaRecorder.setOutputFile(recordAudioFile.getAbsolutePath());
//                    mediaRecorder.prepare();
//                    mediaRecorder.start();
//                    Toast.makeText(AddContentActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.stop:
//                    if (mediaRecorder != null && recordAudioFile.exists()) {
////                        停止录制
//                        mediaRecorder.stop();
////                        释放资源
//                        mediaRecorder.release();
//                        mediaRecorder = null;
//                        Toast.makeText(AddContentActivity.this, "停止录制", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case R.id.play:
//                    mediaPlayer = new MediaPlayer();
//                    mediaPlayer.setDataSource(recordAudioFile.getAbsolutePath());
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                    break;
//                case R.id.delete:
//                    recordAudioFile.delete();
//                    break;
                default:
                    break;
            }
    }

    public void addDB(){
        ContentValues cv = new ContentValues();
        cv.put(NotesDBHelper.CONTENT,ettext.getText().toString());
        cv.put(NotesDBHelper.TIME,getTime());
        cv.put(NotesDBHelper.PATH,phoneFile+"");
        cv.put(NotesDBHelper.VIDEO,videoFile+"");
        cv.put(NotesDBHelper.AUDIO, recordAudioFile + "");
        dbWriter.insert(NotesDBHelper.TABLE_NAME,null,cv);
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
//            btnRecord.setVisibility(View.GONE);
//            btnStop.setVisibility(View.GONE);
//            btnPlay.setVisibility(View.GONE);
//            btnDelete.setVisibility(View.GONE);
        }
        if (val.equals("2")){//图文
            c_img.setVisibility(View.VISIBLE);
            v_video.setVisibility(View.GONE);
//            btnRecord.setVisibility(View.GONE);
//            btnStop.setVisibility(View.GONE);
//            btnPlay.setVisibility(View.GONE);
//            btnDelete.setVisibility(View.GONE);
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
//            btnRecord.setVisibility(View.VISIBLE);
//            btnStop.setVisibility(View.VISIBLE);
//            btnPlay.setVisibility(View.VISIBLE);
//            btnDelete.setVisibility(View.VISIBLE);

            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, 3);
        }

        if (val.equals("4")){//视频
            c_img.setVisibility(View.GONE);
            v_video.setVisibility(View.VISIBLE);
//            btnRecord.setVisibility(View.GONE);
//            btnStop.setVisibility(View.GONE);
//            btnPlay.setVisibility(View.GONE);
//            btnDelete.setVisibility(View.GONE);
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
            //Bitmap bitmap = BitmapFactory.decodeFile(phoneFile.getAbsolutePath());
            Bitmap imageThumbnail = ImageUtil.getImageThumbnail(phoneFile.getAbsolutePath(), 200, 200);
            c_img.setImageBitmap(imageThumbnail);
        }
        if (requestCode == 2){
            v_video.setVideoURI(Uri.fromFile(videoFile));
            v_video.start();
        }

        if (requestCode == 3){
            try {
                Uri uri = data.getData();
                audioPath = getAudioFilePathFromUri(uri);

                if(audioPath != null){
                    recordAudioFile = new File(audioPath);
                    player.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer = null;
        setTitle("播放完毕");
    }
}
