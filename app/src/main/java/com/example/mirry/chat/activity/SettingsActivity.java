package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.HashMap;
import java.util.Map;

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
    @InjectView(R.id.voice)
    Switch voice;
    @InjectView(R.id.vibration)
    Switch vibration;
    @InjectView(R.id.exit)
    Button exit;
    @InjectView(R.id.back)
    IconFontTextView back;
    private String mNickname;
    private String mAccount;
    private Boolean mVoice;
    private Boolean mVibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initData();
        back.setOnClickListener(this);
        head.setOnClickListener(this);
        nickname.setOnClickListener(this);
        editPwd.setOnClickListener(this);
        exit.setOnClickListener(this);

        voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtil.setBoolean(SettingsActivity.this, "config", "voice", isChecked);
                voice.setChecked(isChecked);
            }
        });
        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesUtil.setBoolean(SettingsActivity.this, "config", "vibration", isChecked);
                vibration.setChecked(isChecked);
            }
        });
    }

    private void initData() {
        mAccount = PreferencesUtil.getString(SettingsActivity.this, "config", "account", "");
        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(mAccount);
        if (mInfo != null) {
            mNickname = mInfo.getName();
            if (mNickname == null || mNickname.equals("")) {
                nickname.setText(mAccount);
            } else {
                nickname.setText(mNickname);
            }
        }
        account.setText(mAccount);

        mVoice = PreferencesUtil.getBoolean(SettingsActivity.this, "config", "voice", true);
        mVibration = PreferencesUtil.getBoolean(SettingsActivity.this, "config", "vibration", true);
        voice.setChecked(mVoice);
        vibration.setChecked(mVibration);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                updateDataAndBack();
                break;
            case R.id.head:
                Toast.makeText(this, "更换头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.editPwd:
                startActivity(new Intent(SettingsActivity.this, ResetPwdActivity.class));
                break;
            case R.id.exit:
                NIMClient.getService(AuthService.class).logout();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        updateDataAndBack();
    }

    private void updateDataAndBack() {
        String curNick = nickname.getText().toString().trim();
        if (!curNick.equals(mNickname)) {
            Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
            fields.put(UserInfoFieldEnum.Name, curNick);
            NIMClient.getService(UserService.class).updateUserInfo(fields)
                    .setCallback(new RequestCallbackWrapper<Void>() {

                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            Toast.makeText(SettingsActivity.this, "用户信息设置完成", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        finish();
    }
}
