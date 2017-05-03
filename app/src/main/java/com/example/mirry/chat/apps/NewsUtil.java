package com.example.mirry.chat.apps;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirry on 2017/5/2.
 */

public class NewsUtil {
    private static String appKey = "f55fd954bc1848289667d5e85a4977a2";
    private static int rows = 5;
    private static String url = "http://api.avatardata.cn/GuoNeiNews/Query";
    private static String result = null;

    public static String getNewsData(){
        String mUrl = url + "?key=" + appKey + "&rows=" + rows;
        HttpPost httppost=new HttpPost(mUrl);
        HttpResponse response = null;
        try {
            response = new DefaultHttpClient().execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //发送Post,并返回一个HttpResponse对象
        if(response.getStatusLine().getStatusCode()==200){
            try {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}