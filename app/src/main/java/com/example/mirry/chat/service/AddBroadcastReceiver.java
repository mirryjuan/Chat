package com.example.mirry.chat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mirry.chat.common.Common;

/**
 * Created by Mirry on 2017/5/9.
 */

public class AddBroadcastReceiver extends BroadcastReceiver {
    private OnFriendAddListener onFriendAddListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Common.FRIEND_ADD)){
            String account = intent.getStringExtra("account");
            if(onFriendAddListener != null){
                onFriendAddListener.onFriendAdd(account);
            }
        }
    }

    public interface OnFriendAddListener{
        void onFriendAdd(String account);
    }

    public void setOnFriendAddListener(OnFriendAddListener onFriendAddListener) {
        this.onFriendAddListener = onFriendAddListener;
    }
}
