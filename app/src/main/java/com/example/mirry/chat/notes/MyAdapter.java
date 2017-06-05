package com.example.mirry.chat.notes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mirry.chat.R;

/**
 * Created by Administrator on 2016/1/22.
 */
public class MyAdapter extends BaseAdapter{
    private Context context;
    private Cursor cursor;
    private LinearLayout linearLayout;

    public MyAdapter(Context context,Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
    }


        @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition() ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        linearLayout = (LinearLayout) inflater.inflate(R.layout.cell,null);
        TextView contenttv = (TextView) linearLayout.findViewById(R.id.list_content);
        TextView timetv = (TextView) linearLayout.findViewById(R.id.list_time);
        ImageView imgiv = (ImageView) linearLayout.findViewById(R.id.list_img);
        ImageView videovv = (ImageView) linearLayout.findViewById(R.id.list_video);
        cursor.moveToPosition(position);
        String content = cursor.getString(cursor.getColumnIndex("content"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        String url = cursor.getString(cursor.getColumnIndex("path"));
        String videoUrl = cursor.getString(cursor.getColumnIndex("video"));
        contenttv.setText(content);
        timetv.setText(time);
        imgiv.setImageBitmap(getImageThumbnail(url,200,200));
        videovv.setImageBitmap(getVideoThumbnail(videoUrl,200,200, MediaStore.Images.Thumbnails.MICRO_KIND));
        return linearLayout;
    }

//    该方法用于获取图片缩略图
    public Bitmap getImageThumbnail(String uri,int width,int height){
        Bitmap bitmap  = null;
//        加载图像尺寸,而不是图像本身
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(uri,options);
        options.inJustDecodeBounds = false;
        int beWidth = options.outWidth/width;
        int beHeight = options.outHeight/height;
        int be = 1;
        if (beWidth<beHeight){
//            若宽度比例更大则根据宽度进行缩放
            be = beWidth;
        }else{
//            若高度比例更大则根据高度进行缩放
            be = beHeight;
        }
        if (be<=0){
            be = 1;
        }
//        设定缩放比例
        options.inSampleSize = be;
//        重新获取缩略图之后的图片
        bitmap = BitmapFactory.decodeFile(uri,options);
//        ThumbnailUtils.extractThumbnail()方法用于提取缩略图
//        ThumbnailUtils.OPTIONS_RECYCLE_INPUT常量表示应该回收extractThumbnail(Bitmap, int, int, int)
//          输入源图片(第一个参数)，除非输出图片就是输入图片。
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    private Bitmap getVideoThumbnail(String uri,int width,int height,int kind){
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(uri,kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
