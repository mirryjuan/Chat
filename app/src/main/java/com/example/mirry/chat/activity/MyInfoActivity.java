package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.mirry.chat.R;

import java.util.Map;

public class MyInfoActivity extends Activity {
    private String mNickname;
    private int mSex;
    private String mAccount;
    private String mPhone;
    private String mBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_info);

        Intent intent = getIntent();
        Map<String,Object> mInfo = (Map<String, Object>) intent.getSerializableExtra("info");
        mNickname = (String) mInfo.get("nickname");
        mAccount = (String) mInfo.get("account");
        mPhone = (String) mInfo.get("phone");
        mBirthday = (String) mInfo.get("birthday");
        mSex = (int) mInfo.get("sex");

        initData();
    }

    private void initData() {

    }
}
