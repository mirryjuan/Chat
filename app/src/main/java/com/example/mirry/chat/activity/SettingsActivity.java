package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.account)
    TextView account;
    @InjectView(R.id.editPwd)
    TextView editPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initData();
        head.setOnClickListener(this);
        nickname.setOnClickListener(this);
        editPwd.setOnClickListener(this);
    }

    private void initData() {
        account.setText("15732136540");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                Toast.makeText(this, "更换头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nickName:
                Toast.makeText(this, "修改昵称", Toast.LENGTH_SHORT).show();
                break;
            case R.id.editPwd:
                startActivity(new Intent(SettingsActivity.this,ResetPwdActivity.class));
                break;
        }
    }
}
