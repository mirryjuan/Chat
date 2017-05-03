package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.FrameLayout;

import com.example.mirry.chat.apps.JokeUtil;
import com.example.mirry.chat.apps.NewsUtil;
import com.example.mirry.chat.apps.WeatherUtil;
import com.example.mirry.chat.service.WebappModeListener;

import io.dcloud.EntryProxy;
import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.feature.internal.sdk.SDK;

public class AppsActivity extends Activity {
    private String mAppId = "";
    boolean doHardAcc = true;
    EntryProxy mEntryProxy = null;

    private String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        String item = intent.getStringExtra("item");

        switch (item){
            case "scan":
                mAppId = "001";
                break;
            case "robot":
                mAppId = "002";
                break;
            case "record":
                mAppId = "003";
                break;
            case "news":
                mAppId = "004";
                break;
            case "weather":
                mAppId = "005";
                break;
            default:
                mAppId = "";
                break;
        }

        if (mEntryProxy == null) {
            FrameLayout f = new FrameLayout(this);
            // 创建5+内核运行事件监听
            WebappModeListener wm = new WebappModeListener(this, f, mAppId);
            // 初始化5+内核
            mEntryProxy = EntryProxy.init(this, wm);
            // 启动5+内核
            mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBAPP, null);
            setContentView(f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mEntryProxy.onActivityExecute(this, ISysEventListener.SysEventType.onCreateOptionMenu, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEntryProxy.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEntryProxy.onResume(this);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getFlags() != 0x10600000) {
            // 非点击icon调用activity时才调用newintent事件
            mEntryProxy.onNewIntent(this, intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEntryProxy.onStop(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        try {
            int temp = this.getResources().getConfiguration().orientation;
            if (mEntryProxy != null) {
                mEntryProxy.onConfigurationChanged(this, temp);
            }
            super.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mEntryProxy.onActivityExecute(this, ISysEventListener.SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
    }

    @Override
    public void onBackPressed() {
        backToActivity();
    }

    public void backToActivity(){
        SDK.stopWebApp(SDK.obtainCurrentApp());
    }

    public String getNews(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = NewsUtil.getNewsData();
            }
        }).start();
        return result;
    }

    public String getWeather(final String cityName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = WeatherUtil.getWeatherData(cityName);
            }
        }).start();
        return result;
    }

    public String getJoke(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                result = JokeUtil.getJokeData();
            }
        }).start();
        return result;
    }
}
