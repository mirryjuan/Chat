package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.mirry.chat.activity.ChatActivity;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.NewFriendActivity;
import com.example.mirry.chat.adapter.ContactAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.view.QuickIndexBar;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView contactList;
    private MainActivity mActivity;
    private QuickIndexBar bar;
    private LinearLayout addFriend;
    private List<Friend> friendList = new ArrayList<>();
    private List<Map<String,String>> newFriendList = new ArrayList<>();
    private ContactAdapter adapter;
    private LinearLayout emptyView;

    private List<String> accounts;

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
    private List<NimUserInfo> users;

    public Handler getHandler(){
        return handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
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

        fillAndSortData(friendList);
        adapter = new ContactAdapter(mActivity,friendList);
        contactList.setAdapter(adapter);
        contactList.setEmptyView(emptyView);
        contactList.setOnItemClickListener(this);
        return view;
    }

    private void fillAndSortData(List<Friend> friendList) {
        // 获取所有好友帐号
        accounts = NIMClient.getService(FriendService.class).getFriendAccounts();
        if(accounts.size() != 0){
            // 获取所有好友用户资料
            users = NIMClient.getService(UserService.class).getUserInfoList(accounts);
            for (NimUserInfo user : users) {
                if(!TextUtils.equals(user.getName(),"")){
                    friendList.add(new Friend(user.getName()));
                }else{
                    friendList.add(new Friend(user.getAccount()));
                }
            }
            //进行排序
            Collections.sort(friendList);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String curAccount = accounts.get(position);
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("curAccount",curAccount);
        intent.putExtra("curUsername",friendList.get(position).getName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFriend:
                Intent intent = new Intent(mActivity, NewFriendActivity.class);
                intent.putExtra("newFriend", (Serializable) newFriendList);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
