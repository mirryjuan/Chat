package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.common.CheckSumBuilder;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.common.MyOpenHelper;
import com.example.mirry.chat.service.NetBroadcastReceiver;
import com.example.mirry.chat.utils.CommonUtil;
import com.example.mirry.chat.utils.NetUtil;
import com.example.mirry.chat.view.IconFontTextView;

import org.apache.commons.lang3.StringUtils;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

@SuppressWarnings("all")
public class RegisterActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    @InjectView(R.id.account)
    EditText account;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.confirmPwd)
    EditText confirmPwd;
    @InjectView(R.id.register)
    Button register;
    @InjectView(R.id.delete_account)
    IconFontTextView deleteAccount;
    @InjectView(R.id.delete_pwd)
    IconFontTextView deletePwd;
    @InjectView(R.id.delete_confirm)
    IconFontTextView deleteConfirm;
    @InjectView(R.id.activity_register)
    LinearLayout activityRegister;
    private String mAccid;
    private String mPwd;
    private String mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        register.setOnClickListener(this);

        deleteAccount.setOnClickListener(this);
        deletePwd.setOnClickListener(this);
        deleteConfirm.setOnClickListener(this);
        activityRegister.setOnClickListener(this);

        account.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        confirmPwd.setOnFocusChangeListener(this);
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(account.getText().toString())) {
                    deleteAccount.setVisibility(View.VISIBLE);
                } else {
                    deleteAccount.setVisibility(View.GONE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(password.getText().toString())) {
                    if (deletePwd.getVisibility() != View.VISIBLE) {
                        deletePwd.setVisibility(View.VISIBLE);
                    }
                } else {
                    deletePwd.setVisibility(View.GONE);
                }
            }
        });
        confirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(confirmPwd.getText().toString())) {
                    if (deleteConfirm.getVisibility() != View.VISIBLE) {
                        deleteConfirm.setVisibility(View.VISIBLE);
                    }
                } else {
                    deleteConfirm.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_account:
                account.setText("");
                password.setText("");
                confirmPwd.setText("");
                break;
            case R.id.delete_pwd:
                password.setText("");
                confirmPwd.setText("");
                break;
            case R.id.delete_confirm:
                confirmPwd.setText("");
                break;
            case R.id.register:
                mAccid = account.getText().toString();
                mPwd = password.getText().toString();
                mConfirm = confirmPwd.getText().toString();
                if (mAccid.equals("")) {
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                } else if (mPwd.equals("")) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (mConfirm.equals(mPwd)) {
                        try {
                            if(isNetConnected){
                                doRegister(mAccid, mPwd);
                            }else{
                                Toast.makeText(this, "网络异常,请检查网络连接", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.activity_register:
                CommonUtil.hideKeyBoard(RegisterActivity.this,account);
                break;
        }
    }

    /*
   * 注册云信账号
   * */
    public void doRegister(String accid, String token) throws Exception {
        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPostExecute(String resultStr) {
                super.onPostExecute(resultStr);
                try {
                    if (resultStr != null) {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        String code = jsonObject.getString("code");
                        if (code.equals("414")) {
                            Toast.makeText(RegisterActivity.this, "该账户已经被注册", Toast.LENGTH_SHORT).show();
                            account.setText("");
                            confirmPwd.setText("");
                            password.setText("");
                        } else if (code.equals("200")) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            insertIntoDatabase();    //插入本地数据库
                            Intent intent = new Intent(RegisterActivity.this, InfoSetActivity.class);
                            intent.putExtra("accid", mAccid);
                            startActivity(intent);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    RegisterActivity.this.finish();
                                }
                            }, 1500);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String url = "https://api.netease.im/nimserver/user/create.action";
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
                nvps.add(new BasicNameValuePair("token", params[1]));
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
        asyncTask.execute(accid, token);
    }

    private void insertIntoDatabase() {
        MyOpenHelper helper = new MyOpenHelper(RegisterActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // TODO: 2017/4/12 头像
        values.put("account", mAccid);
        values.put("password", mPwd);
        values.put("nickname", mAccid);
        values.put("sex", Common.MALE);
        values.put("birthday", "");
        values.put("phone", "");
        db.insert("users", null, values);
        db.close();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.account:
                if (hasFocus) {
                    if (deletePwd.getVisibility() != View.GONE) {
                        deletePwd.setVisibility(View.GONE);
                    }
                    if (deleteConfirm.getVisibility() != View.GONE) {
                        deleteConfirm.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(account.getText().toString())) {
                        deleteAccount.setVisibility(View.VISIBLE);
                    } else {
                        deleteAccount.setVisibility(View.GONE);
                    }
                } else {
                    deleteAccount.setVisibility(View.GONE);
                }
                break;
            case R.id.password:
                if (hasFocus) {
                    if (deleteAccount.getVisibility() != View.GONE) {
                        deleteAccount.setVisibility(View.GONE);
                    }
                    if (deleteConfirm.getVisibility() != View.GONE) {
                        deleteConfirm.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(password.getText().toString())) {
                        deletePwd.setVisibility(View.VISIBLE);
                    } else {
                        deletePwd.setVisibility(View.GONE);
                    }
                } else {
                    deletePwd.setVisibility(View.GONE);
                }
                break;
            case R.id.confirmPwd:
                if (hasFocus) {
                    if (deleteAccount.getVisibility() != View.GONE) {
                        deleteAccount.setVisibility(View.GONE);
                    }
                    if (deletePwd.getVisibility() != View.GONE) {
                        deletePwd.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(confirmPwd.getText().toString())) {
                        deleteConfirm.setVisibility(View.VISIBLE);
                    } else {
                        deleteConfirm.setVisibility(View.GONE);
                    }
                } else {
                    deleteConfirm.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetChange(int netType) {
        isNetConnected = isNetConnect(netType);
    }
}
