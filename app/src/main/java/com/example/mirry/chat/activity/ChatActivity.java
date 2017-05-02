package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.ChatAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.bean.Me;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    @InjectView(R.id.back)
    IconFontTextView back;
    @InjectView(R.id.username)
    TextView username;
    @InjectView(R.id.userinfo)
    IconFontTextView userinfo;
    @InjectView(R.id.add)
    IconFontTextView add;
    @InjectView(R.id.chatList)
    ListView chatList;
    @InjectView(R.id.msg)
    EditText msg;
    @InjectView(R.id.send)
    Button send;
    private ChatAdapter adapter = null;
    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;
    private static final int LIMIT = 50;

    private String curAccount;
    private String curUsername;
    private String mAccount;

    private List<Object> list = new ArrayList<>();

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    for (IMMessage message : messages) {
                        Friend friend = null;
                        if(!message.getFromNick().equals("")){
                            friend = new Friend(message.getFromNick());
                        }else{
                            friend = new Friend(message.getFromAccount());
                        }
                        friend.setMsg(message.getContent());
                        friend.setType(TYPE_FRIEND);
                        list.add(friend);
                        adapter.notifyDataSetChanged();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);

        initData();

        back.setOnClickListener(this);
        userinfo.setOnClickListener(this);
        add.setOnClickListener(this);
        send.setOnClickListener(this);

        msg.addTextChangedListener(this);

        adapter = new ChatAdapter(this, list);
        chatList.setAdapter(adapter);
        chatList.smoothScrollToPosition(adapter.getCount() - 1);

        //true表示注册消息接收观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    private void initData() {
        mAccount = PreferencesUtil.getString(ChatActivity.this,"config","account","");
        Intent intent = getIntent();
        curAccount = intent.getStringExtra("curAccount");
        curUsername = intent.getStringExtra("curUsername");

        if(curUsername.equals("")){
            username.setText(curAccount);
        }else{
            username.setText(curUsername);
        }
        NIMClient.getService(MsgService.class).setChattingAccount(curAccount,SessionTypeEnum.P2P);

        if(isNetConnected){
            list.clear();
            long time = System.currentTimeMillis()+10000;
            NIMClient.getService(MsgService.class).queryMessageListEx(
                    MessageBuilder.createEmptyMessage(curAccount, SessionTypeEnum.P2P, time),
                    QueryDirectionEnum.QUERY_OLD, LIMIT, true)
                    .setCallback(new RequestCallback<List<IMMessage>>() {
                @Override
                public void onSuccess(List<IMMessage> allMessages) {
                    for (IMMessage message :allMessages) {
                        String account = message.getFromAccount();
                        String content = message.getContent();
                        if(account.equals(curAccount)){
                            Friend friend = null;
                            if(!message.getFromNick().equals("")){
                                friend = new Friend(message.getFromNick());
                            }else{
                                friend = new Friend(account);
                            }

                            friend.setMsg(content);
                            friend.setType(TYPE_FRIEND);
                            list.add(friend);
                        }else if(account.equals(mAccount)){
                            Me me = new Me();
                            me.setMsg(content);
                            me.setType(TYPE_ME);
                            list.add(me);
                        }
                    }
                    if(adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }else{
            // TODO: 2017/4/21 加载本地消息
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.userinfo:
                Toast.makeText(this, "联系人信息界面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add:
                break;
            case R.id.send:
                sendMsg(curAccount,Common.MSG_TEXT);
                break;
        }
    }

    private void sendMsg(String id,int type) {
        IMMessage message = null;
        final String content = msg.getText().toString();
        final File file = null;
        final double latitude = 0; // 纬度
        final double longitude = 0; // 经度

        switch (type){
            case Common.MSG_TEXT:
                // 创建文本消息
                message = MessageBuilder.createTextMessage(
                        id,             //接收者ID
                        SessionTypeEnum.P2P, // 单聊
                        content // 文本内容
                );
                break;
            case Common.MSG_IMG:
                //创建图片消息
                message = MessageBuilder.createImageMessage(
                        id,             //接收者ID
                        SessionTypeEnum.P2P, // 单聊
                        file, // 图片文件对象
                        null // 文件显示名字，如果第三方 APP 不关注，可以为 null
                );
                break;
            case Common.MSG_FILE:

                break;
            case Common.MSG_LOCATION:
                message = MessageBuilder.createLocationMessage(
                        id, // 用户帐号
                        SessionTypeEnum.P2P, // 单聊
                        latitude, // 纬度
                        longitude, // 经度
                        "" // 地址信息描述
                );
                break;
            case Common.MSG_AUDIO:
                message = MessageBuilder.createAudioMessage(
                        id, //用户帐号
                        SessionTypeEnum.P2P, // 单聊
                        file, // 音频文件
                        0 // 音频持续时间，单位是ms
                );
                break;
            case Common.MSG_VIDEO:
                message = MessageBuilder.createVideoMessage(
                        id, // 用户帐号
                        SessionTypeEnum.P2P, //单聊
                        file, // 视频文件
                        0, // 视频持续时间
                        200, // 视频宽度
                        100, // 视频高度
                        null // 视频显示名，可为空
                );
                break;
        }

        Me me = new Me();
        me.setMsg(content);
        me.setType(TYPE_ME);
        list.add(me);
        adapter.notifyDataSetChanged();

        msg.setText("");

        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {

            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtils.isNotEmpty(msg.getText().toString())) {
            send.setEnabled(true);
            send.setBackgroundResource(R.drawable.bg_sendmsg_enabled);
        } else {
            send.setEnabled(false);
            send.setBackgroundResource(R.drawable.bg_sendmsg_normal);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //false表示注销消息接收观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    @Override
    public void onNetChange(int netType) {
        isNetConnected = isNetConnect(netType);
        if(isNetConnected){
            // TODO: 2017/4/21 刷新消息列表
        }
    }
}
