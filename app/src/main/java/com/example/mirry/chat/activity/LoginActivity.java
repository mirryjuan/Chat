package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mirry.chat.Common;
import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.AllUsersAdapter;
import com.example.mirry.chat.utils.CommonUtil;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @InjectView(R.id.userinfo)
    LinearLayout userLayout;
    @InjectView(R.id.activity_login)
    RelativeLayout activityLogin;

    private ListView userList;
    private AllUsersAdapter adapter;

    private List<String> users = new ArrayList<>();
    private Map<String, String> allUserInfo;

    private String currentUser = "";
    private PopupWindow popupWindow = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.USER_DELETE:
                    username.setText("");
                    password.setText("");
                    break;
            }
        }
    };

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
        }, 1500);


        login.setOnClickListener(this);
        forget.setOnClickListener(this);
        register.setOnClickListener(this);

        deleteName.setOnClickListener(this);
        deletePwd.setOnClickListener(this);
        down.setOnClickListener(this);

        activityLogin.setOnClickListener(this);

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
        String name = PreferencesUtil.getString(LoginActivity.this, "config", "userName", "");
        String pwd = PreferencesUtil.getString(LoginActivity.this, "config", "password", "");
        username.setText(name);
        password.setText(pwd);

        allUserInfo = PreferencesUtil.getAll(LoginActivity.this, "users");
        Set<String> set = allUserInfo.keySet();
        for (String username : set) {
            users.add(username);
        }
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
                deleteName.setVisibility(View.GONE);
                deletePwd.setVisibility(View.GONE);
                showPopupWindow();
                break;
            case R.id.activity_login:
                CommonUtil.hideKeyBoard(LoginActivity.this,password);
                break;
            default:
                break;
        }
    }

    private void showPopupWindow() {
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.popup_user, null, false);
        userList = (ListView) view.findViewById(R.id.list_user);
        adapter = new AllUsersAdapter(LoginActivity.this, users, handler);
        userList.setAdapter(adapter);

        popupWindow = new PopupWindow(view,
                userLayout.getMeasuredWidth(), LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setAnimationStyle(R.anim.anim_popup);  //设置加载动画

        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popupWindow.showAsDropDown(userLayout, 0, 0);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentUser = users.get(position);
                username.setText(currentUser);
                password.setText(allUserInfo.get(currentUser));
                popupWindow.dismiss();
            }
        });

    }

    private void doLogin() {
        CommonUtil.hideKeyBoard(LoginActivity.this, password);
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
                            PreferencesUtil.setString(LoginActivity.this, "config", "userName", name);
                            PreferencesUtil.setString(LoginActivity.this, "config", "password", pwd);
                            PreferencesUtil.setString(LoginActivity.this, "config", "account", param.getAccount());
                            PreferencesUtil.setString(LoginActivity.this, "config", "token", param.getToken());
                            saveUsersInfo(name, pwd);   //缓存登陆过的用户信息
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

    private void saveUsersInfo(String name, String pwd) {
        PreferencesUtil.setString(LoginActivity.this, "users", name, pwd);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.username:
                if(hasFocus){
                    if (deletePwd.getVisibility() != View.GONE) {
                        deletePwd.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(username.getText().toString())) {
                        deleteName.setVisibility(View.VISIBLE);
                    }else{
                        deleteName.setVisibility(View.GONE);
                    }
                }else{
                    deleteName.setVisibility(View.GONE);
                }
                break;
            case R.id.password:
                if(hasFocus){
                    if (deleteName.getVisibility() != View.GONE) {
                        deleteName.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(password.getText().toString())) {
                        deletePwd.setVisibility(View.VISIBLE);
                    }else{
                        deletePwd.setVisibility(View.GONE);
                    }
                }else{
                    deletePwd.setVisibility(View.GONE);
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
