package com.example.mirry.chat.apps;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Mirry on 2017/5/3.
 */

public class WeatherUtil {
    private static String appKey = "8b205580e5f7409a8f881b1c1d1d6482";
    private static String url = "http://api.avatardata.cn/Weather/Query";
    private static String result = null;

    public static String getWeatherData(String cityName){
        String mUrl = url + "?key=" + appKey + "&cityname=" + cityName;
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
