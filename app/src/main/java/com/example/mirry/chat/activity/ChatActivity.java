package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.IconFontTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends Activity implements View.OnClickListener {

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.userinfo:
                break;
            case R.id.add:
                break;
            case R.id.send:
                break;
        }
    }
}
