package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.AppsActivity;
import com.example.mirry.chat.activity.MainActivity;

public class AppsFragment extends Fragment implements View.OnClickListener {
    private MainActivity mActivity;
    private TextView scan;
    private TextView robot;
    private TextView record;
    private TextView news;
    private TextView weather;
    private TextView share;
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(mActivity,R.layout.fragment_apps,null);
        scan = (TextView) view.findViewById(R.id.scan);
        robot = (TextView) view.findViewById(R.id.robot);
        record = (TextView) view.findViewById(R.id.record);
        news = (TextView) view.findViewById(R.id.news);
        weather = (TextView) view.findViewById(R.id.weather);
        share = (TextView) view.findViewById(R.id.share);

        scan.setOnClickListener(this);
        robot.setOnClickListener(this);
        record.setOnClickListener(this);
        news.setOnClickListener(this);
        weather.setOnClickListener(this);
        share.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        intent = new Intent(mActivity, AppsActivity.class);
        switch (v.getId()){
            case R.id.scan:
                intent.putExtra("item","scan");
                startActivity(intent);
                break;
            case R.id.robot:
                intent.putExtra("item","robot");
                startActivity(intent);
                break;
            case R.id.record:
                intent.putExtra("item","record");
                startActivity(intent);
                break;
            case R.id.news:
                intent.putExtra("item","news");
                startActivity(intent);
                break;
            case R.id.weather:
                intent.putExtra("item","weather");
                startActivity(intent);
                break;
            case R.id.share:
                intent.putExtra("item","share");
                startActivity(intent);
                break;
        }
    }

}
