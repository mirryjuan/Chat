package com.example.mirry.chat.apps;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Mirry on 2017/5/2.
 */

public class JokeUtil {
    private static String appKey = "ebc4607ee9e348ccb1bec4fa39c4699d";
    private static int rows = 5;
    private static String url = "http://api.avatardata.cn/Joke/NewstJoke";
    private static String result = null;

    public static String getJokeData(){
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