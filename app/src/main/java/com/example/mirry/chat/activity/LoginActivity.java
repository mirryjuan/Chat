package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.ToastUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.username)
    EditText username;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.register)
    Button register;
    @InjectView(R.id.codeLogin)
    Button codeLogin;
    @InjectView(R.id.forget)
    Button forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        login.setOnClickListener(this);
        codeLogin.setOnClickListener(this);
        forget.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                doLogin();
                break;
            case R.id.codeLogin:
                startActivity(new Intent(LoginActivity.this, CodeLoginActivity.class));
                break;
            case R.id.forget:
                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    private void doLogin() {
        final String name = username.getText().toString().toLowerCase();
        final String pwd = password.getText().toString();
        if(name.equals("")||pwd.equals("")){
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        }else{
            LoginInfo info = new LoginInfo(name,pwd);
            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo param) {
//                        MyCache.setAccount(name);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onFailed(int code) {
                            Log.e("code","错误码："+code);
                            switch (code){
                                case 302:
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    break;
                                case 422:
                                    Toast.makeText(LoginActivity.this, "用户被禁用", Toast.LENGTH_SHORT).show();
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
}
