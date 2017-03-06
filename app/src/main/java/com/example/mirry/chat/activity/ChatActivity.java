package com.example.mirry.chat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.IconFontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {

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

        msg.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.userinfo:
                Toast.makeText(this, "联系人信息界面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add:
                break;
            case R.id.send:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(StringUtils.isNotEmpty(msg.getText().toString())){
            send.setEnabled(true);
            send.setBackgroundResource(R.drawable.bg_sendmsg_enabled);
        }else{
            send.setEnabled(false);
            send.setBackgroundResource(R.drawable.bg_sendmsg_normal);
        }
    }
}
