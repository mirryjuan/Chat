package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppsFragment extends Fragment {
    @InjectView(R.id.scan)
    TextView scan;
    @InjectView(R.id.robot)
    TextView robot;
    @InjectView(R.id.record)
    TextView record;
    @InjectView(R.id.news)
    TextView news;
    @InjectView(R.id.weather)
    TextView weather;
    @InjectView(R.id.share)
    TextView share;
    private MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
