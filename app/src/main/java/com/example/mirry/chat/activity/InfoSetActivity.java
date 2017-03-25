package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InfoSetActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.male)
    RadioButton male;
    @InjectView(R.id.female)
    RadioButton female;
    @InjectView(R.id.sex)
    RadioGroup sex;
    @InjectView(R.id.accomplish)
    Button accomplish;
    @InjectView(R.id.next)
    TextView next;
    @InjectView(R.id.birthday)
    EditText birthday;
    @InjectView(R.id.phone)
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_set);
        ButterKnife.inject(this);

        next.setOnClickListener(this);
        accomplish.setOnClickListener(this);

        sex.check(R.id.male);   //默认选中 "男"
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                finish();
                break;
            case R.id.accomplish:
                //存储到本地user表
                //存储到服务器
                finish();
                break;
        }
    }
}
