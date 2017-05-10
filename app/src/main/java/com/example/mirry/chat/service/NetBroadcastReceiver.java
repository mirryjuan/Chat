package com.example.mirry.chat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.mirry.chat.activity.BaseActivity;
import com.example.mirry.chat.utils.NetUtil;

/**
 * Created by Mirry on 2017/4/12.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    public NetEvent netEvent = BaseActivity.event;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            if(netEvent != null){
                // 接口回调传过去状态的类型
                netEvent.onNetChange(netWorkState);
            }
        }
    }

    public interface NetEvent {
        void onNetChange(int netType);
    }
}
