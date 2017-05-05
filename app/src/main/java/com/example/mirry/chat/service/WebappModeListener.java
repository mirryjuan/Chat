package com.example.mirry.chat.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;

import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;

/**
 * Created by Mirry on 2017/4/13.
 */

public class WebappModeListener implements ICore.ICoreStatusListener {
    Activity activity;
    View splashView = null;
    ViewGroup rootView;
    IApp app = null;
    ProgressDialog pd = null;
    String mAppId = "";

    public WebappModeListener(Activity activity, ViewGroup rootView, String mAppId) {
        this.activity = activity;
        this.rootView = rootView;
        this.mAppId = mAppId;
    }

    @Override
    public void onCoreReady(ICore iCore) {
        // 初始化SDK并将5+引擎的对象设置给SDK
        SDK.initSDK(iCore);
        //
        SDK.requestAllFeature();
    }

    @Override
    public void onCoreInitEnd(ICore iCore) {
        // 表示Webapp的路径在 file:///android_asset/apps/H52C035EF
        String appBasePath = "/apps/H52C035EF";

        // 设置启动参数,可在页面中通过plus.runtime.arguments;方法获取到传入的参数
        String args = mAppId;

        // 启动启动独立应用的5+ Webapp
        app = SDK.startWebApp(activity, appBasePath, args, new IWebviewStateListener() {
            // 设置Webview事件监听，可在监监听内获取WebIvew加载内容的进度
            @Override
            public Object onCallBack(int pType, Object pArgs) {
                switch (pType) {
                    case IWebviewStateListener.ON_WEBVIEW_READY:
                        // WebApp准备加载事件
                        // 准备完毕之后添加webview到显示父View中，
                        // 设置排版不显示状态，避免显示webview时html内容排版错乱问题
                        View view = ((IWebview) pArgs).obtainApp().obtainWebAppRootView().obtainMainView();
                        view.setVisibility(View.INVISIBLE);
                        rootView.addView(view, 0);
                        break;
                    case IWebviewStateListener.ON_PAGE_STARTED:
//                        pd = ProgressDialog.show(activity, "加载中", "0/100");
                        break;
                    case IWebviewStateListener.ON_PROGRESS_CHANGED:
                        // WebApp首页面加载进度变化事件
//                        if (pd != null) {
//                            pd.setMessage(pArgs + "/100");
//                        }
                        break;
                    case IWebviewStateListener.ON_PAGE_FINISHED:
                        // WebApp首页面加载完成事件
//                        if (pd != null) {
//                            pd.dismiss();
//                            pd = null;
//                        }
                        // 页面加载完毕，设置显示webview
                        app.obtainWebAppRootView().obtainMainView().setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        },null);

        app.setIAppStatusListener(new IApp.IAppStatusListener() {
            // 设置APP运行事件监听
            @Override
            public boolean onStop() {
                // 应用运行停止时调用
                rootView.removeView(app.obtainWebAppRootView().obtainMainView());
                return false;
            }

            @Override
            public String onStoped(boolean b, String s) {
                return null;
            }

            @Override
            public void onStart() {
                // 独立应用启动时触发事件
            }

            @Override
            public void onPause(IApp arg0, IApp arg1) {
                // WebApp暂停运行时触发事件
            }
        });
    }

    @Override
    public boolean onCoreStop() {
        // 当返回false时候关闭activity
        return false;
    }
}
