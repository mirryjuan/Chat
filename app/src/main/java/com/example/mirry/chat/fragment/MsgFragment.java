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
import com.example.mirry.chat.bean.Msg;
import com.example.mirry.chat.common.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MsgFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.msgList)
    ListView msgList;
    @InjectView(R.id.emptyView)
    LinearLayout emptyView;
    private MainActivity mActivity;
    private List<Msg> msgData = null;

    private Bundle newMsg;

    private String account;
    private String content;
    private String nickname;

    private MsgAdapter adapter;
    private List<Map<String,String>> messages = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Common.MSG_COMING:
                    if(newMsg == null){
                        newMsg = new Bundle();
                    }
                    newMsg = msg.getData();
                    account = newMsg.getString("fromAccount");
                    nickname = newMsg.getString("fromNick");
                    content = newMsg.getString("content");
                    updateMsgList(account,nickname,content);
                    Collections.reverse(msgData);
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

        msgData = new ArrayList<>();
        adapter = new MsgAdapter(mActivity,msgData);
        msgList.setAdapter(adapter);
        msgList.setEmptyView(emptyView);

        initData();

        //消息列表单击事件
        msgList.setOnItemClickListener(this);

        return view;
    }

    private void initData() {
        messages = (List<Map<String, String>>) getArguments().getSerializable("messages");
        if(messages.size() > 0 ){
            for (Map<String, String> message:messages){
                account = message.get("fromAccount");
                nickname = message.get("fromNick");
                content = message.get("content");
                updateMsgList(account,nickname,content);
            }
            Collections.reverse(msgData);
            adapter.notifyDataSetChanged();
        }
    }


    private void updateMsgList(String account, String nickname, String content) {
        for (Msg message:msgData) {
            if(message.getAccount().equals(account)){
                msgData.remove(message);
                break;
            }
        }
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Msg msg = msgData.get(position);
        String curAccount = msg.getAccount();
        String curUsername = msg.getUsername();
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("curAccount",curAccount);
        intent.putExtra("curUsername",curUsername);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}


