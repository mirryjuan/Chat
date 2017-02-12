package com.example.mirry.chat.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
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
        nickname.setOnClickListener(this);
        settings.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head:
                Toast.makeText(mActivity, "修改头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nickname:
                Toast.makeText(mActivity, "修改昵称", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(mActivity, "设置", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
