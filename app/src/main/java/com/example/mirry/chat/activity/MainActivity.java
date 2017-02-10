package com.example.mirry.chat.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.mirry.chat.R;
import com.example.mirry.chat.fragment.AppsFragment;
import com.example.mirry.chat.fragment.ContactFragment;
import com.example.mirry.chat.fragment.MessageFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.content)
    FrameLayout content;
    @InjectView(R.id.message)
    RadioButton message;
    @InjectView(R.id.contact)
    RadioButton contact;
    @InjectView(R.id.miniApps)
    RadioButton miniApps;
    @InjectView(R.id.tabsGroup)
    RadioGroup tabsGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initData();

        tabsGroup.setOnCheckedChangeListener(this);
    }

    private void initData() {
        tabsGroup.check(R.id.message);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content,new MessageFragment());
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fragmentManager = getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (checkedId){
            case R.id.message:
                transaction.replace(R.id.content,new MessageFragment());
                break;
            case R.id.contact:
                transaction.replace(R.id.content,new ContactFragment());
                break;
            case R.id.miniApps:
                transaction.replace(R.id.content,new AppsFragment());
                break;
        }
        transaction.commit();
    }
}
