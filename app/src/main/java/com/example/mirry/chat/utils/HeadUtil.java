package com.example.mirry.chat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mirry on 2017/6/10.
 */

public class HeadUtil {
    public static void setHead(final CircleImageView head, final String imgUrl) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap>() {

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                head.setImageBitmap(bitmap);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    URL url = new URL(imgUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(500);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
        };
        task.execute();
    }

    public static void downloadHeadImg(Boolean download, String account ,String url) {
        if(download){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + account +".jpg";
            NosThumbParam param = new NosThumbParam();
            param.height = 200;
            param.width = 200;
            param.thumb = NosThumbParam.ThumbType.Crop;
            NIMClient.getService(NosService.class).download(url, param, path).setCallback(new RequestCallback() {
                @Override
                public void onSuccess(Object param) {

                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
    }

}
