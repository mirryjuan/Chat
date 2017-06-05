package com.example.mirry.chat.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.activity.ContactInfoActivity;
import com.example.mirry.chat.activity.ShareActivity;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.LoginActivity;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.SettingsActivity;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MeFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    TextView nickname;
    @InjectView(R.id.settings)
    Button settings;
    @InjectView(R.id.gallery)
    Button gallery;
    @InjectView(R.id.share)
    Button share;
    @InjectView(R.id.exit)
    Button exit;
    @InjectView(R.id.info_user)
    LinearLayout userInfo;
    private MainActivity mActivity;
    private String mNickname = "";
    private int mSex = Common.MALE;
    private String mAccount = "";
    private String mPhone = "";
    private String mBirthday = "";

    private Map<String,Object> mInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        ButterKnife.inject(this, view);

        initUserData();

        head.setOnClickListener(this);
        nickname.setOnClickListener(this);
        settings.setOnClickListener(this);
        userInfo.setOnClickListener(this);

        gallery.setOnClickListener(this);
        share.setOnClickListener(this);

        exit.setOnClickListener(this);
        return view;
    }

    private void initUserData() {
        String account = PreferencesUtil.getString(mActivity, "config", "account", "");
        queryUserData(account);
        // TODO: 2017/4/2 设置头像
        nickname.setText(mNickname);
    }

    private void queryUserData(String account) {
        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        if(mInfo != null){
            mAccount = mInfo.getAccount();
            mNickname = mInfo.getName();
            mSex = mInfo.getGenderEnum().getValue();
            mPhone = mInfo.getMobile();
            mBirthday = mInfo.getBirthday();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
            case R.id.nickname:
            case R.id.info_user:
                Intent intent = new Intent(mActivity, ContactInfoActivity.class);
                intent.putExtra("account", mAccount);
                startActivity(intent);
                break;
            case R.id.gallery:
                openGallery();
                break;
            case R.id.settings:
                startActivity(new Intent(mActivity,SettingsActivity.class));
                break;
            case R.id.share:
                startActivity(new Intent(mActivity, ShareActivity.class));
                break;
            case R.id.exit:
                NIMClient.getService(AuthService.class).logout();
                startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    private void openGallery() {
        Toast.makeText(mActivity, "打开相册，暂未实现", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
