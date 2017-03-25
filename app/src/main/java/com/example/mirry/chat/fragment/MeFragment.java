package com.example.mirry.chat.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.MyInfoActivity;
import com.example.mirry.chat.activity.SettingsActivity;
import com.example.mirry.chat.view.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MeFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    TextView nickname;
    @InjectView(R.id.settings)
    Button settings;
    @InjectView(R.id.account)
    TextView account;
    @InjectView(R.id.gallery)
    Button gallery;
    @InjectView(R.id.diary)
    Button diary;
    @InjectView(R.id.share)
    Button share;
    @InjectView(R.id.exist)
    Button exist;
    @InjectView(R.id.mInfo)
    LinearLayout mInfo;
    private MainActivity mActivity;

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

        head.setOnClickListener(this);
        account.setOnClickListener(this);
        nickname.setOnClickListener(this);
        settings.setOnClickListener(this);
        mInfo.setOnClickListener(this);

        gallery.setOnClickListener(this);
        diary.setOnClickListener(this);
        share.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
            case R.id.nickname:
            case R.id.account:
            case R.id.mInfo:
                startActivity(new Intent(mActivity, MyInfoActivity.class));
                break;
            case R.id.gallery:
                openGallery();
                break;
            case R.id.diary:
                openDiary();
                break;
            case R.id.settings:
                startActivity(new Intent(mActivity,SettingsActivity.class));
                break;
            case R.id.share:
                shareMyApp();
                break;
            default:
                break;
        }
    }

    private void shareMyApp() {
        Toast.makeText(mActivity, "分享APP", Toast.LENGTH_SHORT).show();
    }

    private void openDiary() {
        Toast.makeText(mActivity, "打开日记本", Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Toast.makeText(mActivity, "打开相册", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
