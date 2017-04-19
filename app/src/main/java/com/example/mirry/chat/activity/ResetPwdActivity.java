package com.example.mirry.chat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.common.CheckSumBuilder;
import com.example.mirry.chat.utils.PreferencesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ResetPwdActivity extends BaseActivity {

    private String mAccid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        mAccid = PreferencesUtil.getString(ResetPwdActivity.this,"config","account","");
        if(isNetConnected){
            try {
                resetPwd(mAccid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "网络异常,请检查网络连接", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetPwd(String accid) throws Exception{
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPostExecute(String resultStr) {
                super.onPostExecute(resultStr);
                try {
                    if (resultStr != null) {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        String code = jsonObject.getString("code");
                        if (code.equals("200")) {
                            Toast.makeText(ResetPwdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(ResetPwdActivity.this,LoginActivity.class));
                                    ResetPwdActivity.this.finish();
                                }
                            }, 1500);
                        } else {
                            Toast.makeText(ResetPwdActivity.this, "服务器异常，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPwdActivity.this, "密码修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String url = "https://api.netease.im/nimserver/user/refreshToken.action";
                HttpPost httpPost = new HttpPost(url);

                String appKey = "f12752f14a1ed52eb6e6a0f024015a70";
                String appSecret = "caa1842fdfcb";
                String nonce = String.valueOf(Math.random() * 1000);
                String curTime = String.valueOf((new Date()).getTime() / 1000L);
                String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);//参考 计算CheckSum的java代码

                // 设置请求的header
                httpPost.addHeader("AppKey", appKey);
                httpPost.addHeader("Nonce", nonce);
                httpPost.addHeader("CurTime", curTime);
                httpPost.addHeader("CheckSum", checkSum);
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                String resultStr = null;

                // 设置请求的参数
                List<NameValuePair> nvps = new ArrayList<>();
                nvps.add(new BasicNameValuePair("accid", params[0]));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
                    // 执行请求
                    HttpResponse response = httpClient.execute(httpPost);
                    resultStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    // 打印执行结果
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return resultStr;
            }
        };
        asyncTask.execute(accid);
    }
}
