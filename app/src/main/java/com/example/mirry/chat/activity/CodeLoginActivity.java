package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mirry.chat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CodeLoginActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.phoneNum)
    EditText phoneNum;
    @InjectView(R.id.code)
    EditText code;
    @InjectView(R.id.getCode)
    Button sendSms;
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.back)
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_code_login);
        ButterKnife.inject(this);

        sendSms.setOnClickListener(this);
        login.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.getCode:
                break;
            case R.id.login:
                break;
        }
    }
}
