package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ContactInfoActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.note)
    EditText note;
    @InjectView(R.id.info_phone)
    TextView phone;
    @InjectView(R.id.info_birthday)
    TextView birthday;
    @InjectView(R.id.wechat)
    Button chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contact_info);
        ButterKnife.inject(this);

        chat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wechat:
                Intent intent = new Intent(ContactInfoActivity.this,ChatActivity.class);
                startActivity(intent);
                break;
        }
    }
}
