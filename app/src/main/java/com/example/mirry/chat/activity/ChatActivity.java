package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.ChatAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.bean.Me;
import com.example.mirry.chat.utils.CommonUtil;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends Activity implements View.OnClickListener, TextWatcher {

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

    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;

    private List<Object> list = new ArrayList<>();

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    for (IMMessage message : messages) {
                        Friend obj = new Friend(message.getFromNick());
                        obj.setMsg(message.getContent());
                        obj.setType(TYPE_FRIEND);
                        list.add(obj);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);

        back.setOnClickListener(this);
        userinfo.setOnClickListener(this);
        add.setOnClickListener(this);
        send.setOnClickListener(this);

        msg.addTextChangedListener(this);

        adapter = new ChatAdapter(this, list);
        chatList.setAdapter(adapter);

        //true表示注册消息接收观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
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
                sendMsg("15732136540");
                break;
        }
    }

    private void sendMsg(String id) {
        final String content = msg.getText().toString();
        // 创建文本消息
        IMMessage message = MessageBuilder.createTextMessage(
                id,             //接收者ID
                SessionTypeEnum.P2P, // 单聊
                content // 文本内容
        );
        Me obj = new Me();
        obj.setMsg(content);
        obj.setType(TYPE_ME);
        list.add(obj);
        adapter.notifyDataSetChanged();

        msg.setText("");

        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                //隐藏软键盘
                CommonUtil.hideKeyBoard(ChatActivity.this,msg);
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
}
