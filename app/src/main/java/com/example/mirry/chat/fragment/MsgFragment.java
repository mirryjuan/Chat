package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.ChatActivity;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.adapter.MsgAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.bean.Msg;
import com.example.mirry.chat.common.Common;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MsgFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.msgList)
    ListView msgList;
    @InjectView(R.id.emptyView)
    LinearLayout emptyView;
    private MainActivity mActivity;
    private List<Msg> msgData = null;

    private String account;
    private String content;
    private String nickname;

    private MsgAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.MSG_COMING:
                    Bundle data = msg.getData();
                    account = data.getString("fromAccount");
                    nickname = data.getString("fromNick");
                    content = data.getString("content");
                    Msg message = new Msg();
                    message.setAccount(account);
                    if(!nickname.equals("")){
                        message.setUsername(nickname);
                    }else{
                        message.setUsername(account);
                    }
                    message.setMsg(content);
                    message.setCount(1);
                    msgData.add(message);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ButterKnife.inject(this, view);

        initData();

        adapter = new MsgAdapter(mActivity,msgData);
        msgList.setAdapter(adapter);
        msgList.setEmptyView(emptyView);
        //消息列表单击事件
        msgList.setOnItemClickListener(this);

        return view;
    }

    private void initData() {
        msgData = new ArrayList<>();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
