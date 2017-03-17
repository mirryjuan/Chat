package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.SharedPreferencesUtil;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {

    @InjectView(R.id.username)
    EditText username;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.register)
    Button register;
    @InjectView(R.id.forget)
    Button forget;
    @InjectView(R.id.delete_name)
    IconFontTextView deleteName;
    @InjectView(R.id.down)
    IconFontTextView down;
    @InjectView(R.id.delete_pwd)
    IconFontTextView deletePwd;
    @InjectView(R.id.splash)
    ImageView splash;
    private StatusCode status;

    Observer<StatusCode> observer = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (statusCode == StatusCode.LOGINED) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        initUserData();

        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(observer, true);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        splash.setVisibility(View.GONE);
                    }
                });
            }
        },2000);


        login.setOnClickListener(this);
        forget.setOnClickListener(this);
        register.setOnClickListener(this);

        deleteName.setOnClickListener(this);
        deletePwd.setOnClickListener(this);
        down.setOnClickListener(this);

        username.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(username.getText().toString())) {
                    deleteName.setVisibility(View.VISIBLE);
                } else {
                    deleteName.setVisibility(View.GONE);
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

    }

    private void initUserData() {
        String name = SharedPreferencesUtil.getString(LoginActivity.this, "userName", "");
        String pwd = SharedPreferencesUtil.getString(LoginActivity.this, "password", "");
        username.setText(name);
        password.setText(pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                doLogin();
                break;
            case R.id.forget:
                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.delete_name:
                username.setText("");
                password.setText("");
                deleteName.setVisibility(View.GONE);
                break;
            case R.id.delete_pwd:
                password.setText("");
                deletePwd.setVisibility(View.GONE);
                break;
            case R.id.down:
                break;
            default:
                break;
        }
    }

    private void doLogin() {
        final String name = username.getText().toString().toLowerCase();
        final String pwd = password.getText().toString();
        if (name.equals("") || pwd.equals("")) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            LoginInfo info = new LoginInfo(name, pwd);
            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo param) {
                            SharedPreferencesUtil.setString(LoginActivity.this, "userName", name);
                            SharedPreferencesUtil.setString(LoginActivity.this, "password", pwd);
                            SharedPreferencesUtil.setString(LoginActivity.this, "account", param.getAccount());
                            SharedPreferencesUtil.setString(LoginActivity.this, "token", param.getToken());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailed(int code) {
                            Log.e("code", "错误码：" + code);
                            switch (code) {
                                case 302:
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    break;
                                case 422:
                                    Toast.makeText(LoginActivity.this, "用户被禁用", Toast.LENGTH_SHORT).show();
                                    break;
                                case 415:
                                    Toast.makeText(LoginActivity.this, "网络异常，请检查网络连接", Toast.LENGTH_SHORT).show();
                                    break;
                                case 408:
                                    Toast.makeText(LoginActivity.this, "请求超时，请稍候重试", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "服务器异常，请稍候重试", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onException(Throwable exception) {

                        }
                    };
            NIMClient.getService(AuthService.class).login(info).setCallback(callback);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.username:
                if (deletePwd.getVisibility() != View.GONE) {
                    deletePwd.setVisibility(View.GONE);
                }
                if (StringUtils.isNotEmpty(username.getText().toString())) {
                    deleteName.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.password:
                if (deleteName.getVisibility() != View.GONE) {
                    deleteName.setVisibility(View.GONE);
                }
                if (StringUtils.isNotEmpty(password.getText().toString())) {
                    deletePwd.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(observer, false);
    }
}
