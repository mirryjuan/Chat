package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.common.CheckSumBuilder;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.common.MyOpenHelper;
import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InfoSetActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.male)
    RadioButton male;
    @InjectView(R.id.female)
    RadioButton female;
    @InjectView(R.id.sex)
    RadioGroup sex;
    @InjectView(R.id.accomplish)
    Button accomplish;
    @InjectView(R.id.next)
    TextView next;
    @InjectView(R.id.birthday)
    EditText birthday;
    @InjectView(R.id.phone)
    EditText phone;
    private String accid;
    private int mSex = Common.MALE;
    private String mNickname;
    private String mBirthday;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_set);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        accid = intent.getStringExtra("accid");

        initData();

        next.setOnClickListener(this);
        accomplish.setOnClickListener(this);
        sex.setOnCheckedChangeListener(this);
    }

    private void initData() {
        // TODO: 2017/4/2 设置默认头像
        nickname.setText(accid);   //昵称默认为账号
        sex.check(R.id.male);      //性别默认选中 "男"
    }

    @Override
    public void onClick(View v) {
        mNickname = nickname.getText().toString();
        mBirthday = birthday.getText().toString();
        mPhone = phone.getText().toString();
        switch (v.getId()) {
            case R.id.next:
                updateRemoteData(mNickname, mBirthday, mPhone);
                finish();
                break;
            case R.id.accomplish:
//                //更新本地数据库信息
//                updateDatabaseInfo(mNickname, mBirthday, mPhone);
//                //更新服务器用户信息
//                updateRemoteData(mNickname, mBirthday, mPhone);
                doUpdate(accid,mNickname,mBirthday,mPhone);
                break;
        }
    }

    private void updateDatabaseInfo(String mNickname, String mBirthday,String mPhone) {
        MyOpenHelper helper = new MyOpenHelper(InfoSetActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // TODO: 2017/4/12 头像
        values.put("nickname",mNickname);
        values.put("sex",mSex);
        values.put("birthday",mBirthday);
        values.put("phone",mPhone);
        db.update("users",values,"account = ?",new String[]{ accid });
        db.close();
    }

    private void updateRemoteData(String mNickname, String mBirthday, String mPhone) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        // TODO: 2017/4/12 头像
        fields.put(UserInfoFieldEnum.Name, mNickname);
        fields.put(UserInfoFieldEnum.GENDER, mSex);
        fields.put(UserInfoFieldEnum.BIRTHDAY, mBirthday);
        fields.put(UserInfoFieldEnum.MOBILE, mPhone);
        NIMClient.getService(UserService.class).updateUserInfo(fields)
                .setCallback(new RequestCallbackWrapper<Void>() {

                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        Log.e("code",""+code);
                        Toast.makeText(InfoSetActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    public void doUpdate(String accid, String mNickname, String mBirthday, String mPhone){
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPostExecute(String resultStr) {
                super.onPostExecute(resultStr);
                try {
                    if (resultStr != null) {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        String code = jsonObject.getString("code");
                        if (code.equals("200")) {
                            Toast.makeText(InfoSetActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1500);
                        }else{
                            Toast.makeText(InfoSetActivity.this, "更新失败，错误码"+ code, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(InfoSetActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String url = "https://api.netease.im/nimserver/user/updateUinfo.action";
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
                nvps.add(new BasicNameValuePair("name", params[1]));
                nvps.add(new BasicNameValuePair("birth", params[2]));
                nvps.add(new BasicNameValuePair("mobile", params[3]));
//                nvps.add(new BasicNameValuePair("gender", params[4]));
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
        asyncTask.execute(accid, mNickname,mBirthday,mPhone);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.male:
                mSex = Common.MALE;
                break;
            case R.id.female:
                mSex = Common.FEMALE;
                break;
        }
    }
}
