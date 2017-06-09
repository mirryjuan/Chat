package com.example.mirry.chat.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.mirry.chat.activity.ChatActivity;
import com.example.mirry.chat.activity.ContactInfoActivity;
import com.example.mirry.chat.activity.LoginActivity;
import com.example.mirry.chat.adapter.AllUsersAdapter;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.NewFriendActivity;
import com.example.mirry.chat.adapter.ContactAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.service.AddBroadcastReceiver;
import com.example.mirry.chat.view.QuickIndexBar;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener, AddBroadcastReceiver.OnFriendAddListener {

    private ListView contactList;
    private MainActivity mActivity;
    private QuickIndexBar bar;
    private LinearLayout addFriend;
    private List<Friend> friendList = new ArrayList<>();
    private List<Map<String,String>> newFriendList = new ArrayList<>();
    private ContactAdapter adapter;
    private LinearLayout emptyView;

    private List<String> accounts;

    private List<NimUserInfo> users;

    private AddBroadcastReceiver receiver;
    private PopupWindow popupWindow;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.FRIEND_ADD:
                    String curAccount = (String) msg.obj;
                    NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(curAccount);
                    friendList.add(new Friend(userInfo.getName()));
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public Handler getHandler(){
        return handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

        receiver = new AddBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.ADD);
        //注册广播
        mActivity.registerReceiver(receiver, filter);
        receiver.setOnFriendAddListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        newFriendList = (List<Map<String, String>>) getArguments().getSerializable("newFriend");
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        contactList = (ListView) view.findViewById(R.id.contactList);
        bar = (QuickIndexBar) view.findViewById(R.id.bar);
        addFriend = (LinearLayout) view.findViewById(R.id.addFriend);
        emptyView = (LinearLayout) view.findViewById(R.id.emptyView);

        addFriend.setOnClickListener(this);

        bar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                //根据字母定位List
                for(int i = 0; i < friendList.size();i++){
                    Friend friend = friendList.get(i);
                    String pinyin = friend.getPinyin().substring(0, 1);
                    if(TextUtils.equals(letter,pinyin)){
                        contactList.setSelection(i);
                        break;
                    }
                }
            }
        });

//        fillAndSortData(friendList);
        adapter = new ContactAdapter(mActivity,friendList);
        contactList.setAdapter(adapter);
        contactList.setEmptyView(emptyView);
        contactList.setOnItemClickListener(this);
        contactList.setOnItemLongClickListener(this);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        fillAndSortData(friendList);
        adapter.notifyDataSetChanged();
    }

    private void fillAndSortData(List<Friend> friendList) {
        friendList.clear();
        // 获取所有好友帐号
        accounts = NIMClient.getService(FriendService.class).getFriendAccounts();
        if(accounts.size() != 0){
            // 获取所有好友用户资料
            users = NIMClient.getService(UserService.class).getUserInfoList(accounts);
            for (NimUserInfo user : users) {
                Friend friend = null;
                if(!TextUtils.equals(user.getName(),"")){
                    friend = new Friend(user.getName());
                    friend.setAccount(user.getAccount());
                    friendList.add(friend);
                }else{
                    friend = new Friend(user.getAccount());
                    friend.setAccount(user.getAccount());
                    friendList.add(friend);
                }
            }
            //进行排序
            Collections.sort(friendList);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String curAccount = friendList.get(position).getAccount();
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("curAccount",curAccount);
        intent.putExtra("curUsername",friendList.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFriend:
//                List<SystemMessageType> types = new ArrayList<>();
//                types.add(SystemMessageType.AddFriend);
//                // 将“添加好友”类型的系统通知设为已读
//                NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();

                Intent intent = new Intent(mActivity, NewFriendActivity.class);
                intent.putExtra("newFriend", (Serializable) newFriendList);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        showPopupWindow(position);
        return true;
    }

    private void showPopupWindow(final int position) {
        View popupView = View.inflate(mActivity, R.layout.popup_contact, null);
        Button detail = (Button) popupView.findViewById(R.id.detail);
        Button delete = (Button) popupView.findViewById(R.id.delete);

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                String curAccount = friendList.get(position).getAccount();
                Intent intent = new Intent(mActivity, ContactInfoActivity.class);
                intent.putExtra("account", curAccount);
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                deleteFriends(position);
            }
        });

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(popupView);
        popupWindow.setAnimationStyle(R.anim.anim_popup);  //设置加载动画

        //点击非PopupWindow区域，PopupWindow会消失的
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;       // 如果返回true，touch事件将被拦截
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        popupWindow.showAtLocation(contactList, Gravity.BOTTOM,0,0);
    }



    public void deleteFriends(final int position){
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("是否删除好友？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String curAccount = friendList.get(position).getAccount();
                        NIMClient.getService(FriendService.class).deleteFriend(curAccount)
                                .setCallback(new RequestCallback<Void>() {

                                    @Override
                                    public void onSuccess(Void param) {
                                        friendList.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        Toast.makeText(mActivity, "code:"+code, Toast.LENGTH_SHORT).show();
                                        Toast.makeText(mActivity, "删除好友失败", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onException(Throwable exception) {
                                        Toast.makeText(mActivity, "系统异常，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    @Override
    public void onDestroy() {
        mActivity.unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onFriendAdd(String account) {
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        friendList.add(new Friend(userInfo.getName()));
        adapter.notifyDataSetChanged();
    }
}
