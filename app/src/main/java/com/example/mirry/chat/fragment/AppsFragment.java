package com.example.mirry.chat.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;

import io.dcloud.EntryProxy;
import io.dcloud.RInformation;
import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.IOnCreateSplashView;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;

public class AppsFragment extends Fragment {
    private MainActivity mActivity;
    private MiniAppsListener appsListener;
    private EntryProxy entryProxy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout,container,false);
        FrameLayout webViewContainer = (FrameLayout) view.findViewById(R.id.container);
        appsListener = new MiniAppsListener(mActivity,webViewContainer);
        entryProxy = EntryProxy.init(mActivity,appsListener);
        entryProxy.onCreate(mActivity,savedInstanceState,SDK.IntegratedMode.WEBAPP,null);

        return view;
    }

    class MiniAppsListener implements ICoreStatusListener, IOnCreateSplashView {
        Activity activity;
        View splashView = null;
        ViewGroup rootView;
        IApp app = null;
        ProgressDialog pd = null;

        public MiniAppsListener(Activity activity, ViewGroup rootView) {
            this.activity = activity;
            this.rootView = rootView;
        }

        @Override
        public void onCoreReady(ICore iCore) {
            SDK.initSDK(iCore);       // 调用SDK的初始化接口，初始化5+ SDK
            SDK.requestAllFeature();  // 设置当前应用可使用的5+ API
        }

        /**
         * 5+内核初始化完成时触发
         * @param iCore
         */
        @Override
        public void onCoreInitEnd(ICore iCore) {
            // 表示Webapp的路径在 file:///android_asset/apps/H52C035EF/www
            String appBasePath = "/apps/H52C035EF";

            // 启动独立应用的5+ WebApp
            app = SDK.startWebApp(activity, appBasePath, null, new IWebviewStateListener() {
                // 设置WebView事件监听，可在监监听内获取WebView加载内容的进度
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
                            pd = ProgressDialog.show(activity, "加载中", "0/100");
                            break;
                        case IWebviewStateListener.ON_PROGRESS_CHANGED:
                            // WebApp首页面加载进度变化事件
                            if (pd != null) {
                                pd.setMessage(pArgs + "/100");
                            }
                            break;
                        case IWebviewStateListener.ON_PAGE_FINISHED:
                            // WebApp首页面加载完成事件
                            if (pd != null) {
                                pd.dismiss();
                                pd = null;
                            }
                            // 页面加载完毕，设置显示webview
                            app.obtainWebAppRootView().obtainMainView().setVisibility(View.VISIBLE);
                            break;
                    }
                    return null;
                }
            }, this);

            app.setIAppStatusListener(new IApp.IAppStatusListener() {
                // 设置APP运行事件监听
                @Override
                public boolean onStop() {
                    // 应用运行停止时调用
                    rootView.removeView(app.obtainWebAppRootView().obtainMainView());
                    // TODO Auto-generated method stub
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
            return false;
        }

        @Override
        public Object onCreateSplash(Context context) {
            splashView = new FrameLayout(activity);
            splashView.setBackgroundResource(RInformation.DRAWABLE_SPLASH);
            rootView.addView(splashView);
            return null;
        }

        @Override
        public void onCloseSplash() {
            rootView.removeView(splashView);
        }
    }
}
