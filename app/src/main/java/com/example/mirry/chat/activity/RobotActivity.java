package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.ChatMessageAdapter;
import com.example.mirry.chat.bean.ChatMessage;
import com.example.mirry.chat.utils.HttpUtils;
import com.example.mirry.chat.view.IconFontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RobotActivity extends Activity implements TextWatcher {

    @InjectView(R.id.back)
    IconFontTextView back;
    @InjectView(R.id.chatList)
    ListView mMsgs;
    @InjectView(R.id.msg)
    EditText msg;
    @InjectView(R.id.send)
    Button send;

    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mData;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 等待接收，子线程完成数据的返回
            ChatMessage fromMessge = (ChatMessage) msg.obj;
            mData.add(fromMessge);
            mAdapter.notifyDataSetChanged();
            mMsgs.setSelection(mData.size() - 1);
        }

        ;

    };
    private IconFontTextView add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_robot);
        ButterKnife.inject(this);

        initData();
        // 初始化事件
        initListener();
    }

    private void initListener() {
        msg.addTextChangedListener(this);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toMsg = msg.getText().toString();
                if (TextUtils.isEmpty(toMsg)) {
                    Toast.makeText(RobotActivity.this, "发送消息不能为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatMessage toMessage = new ChatMessage();
                toMessage.setDate(new Date());
                toMessage.setMsg(toMsg);
                toMessage.setType(ChatMessage.Type.OUTCOMING);
                mData.add(toMessage);
                mAdapter.notifyDataSetChanged();
                mMsgs.setSelection(mData.size() - 1);

                msg.setText("");

                new Thread() {
                    public void run() {
                        ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
                        Message m = Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    }

                    ;
                }.start();

            }
        });
    }

    private void initData() {
        mData = new ArrayList<ChatMessage>();
        mData.add(new ChatMessage("你好, 我是机器人小微", ChatMessage.Type.INCOMING, new Date()));
        mAdapter = new ChatMessageAdapter(this, mData);
        mMsgs.setAdapter(mAdapter);
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
}
