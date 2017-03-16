package com.example.mirry.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.activity.MainActivity;
import com.example.mirry.chat.activity.NewFriendActivity;
import com.example.mirry.chat.adapter.ContactAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.view.QuickIndexBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView contactList;
    private MainActivity mActivity;
    private QuickIndexBar bar;
    private LinearLayout addFriend;
    private List<Friend> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        contactList = (ListView) view.findViewById(R.id.contactList);
        bar = (QuickIndexBar) view.findViewById(R.id.bar);
        addFriend = (LinearLayout) view.findViewById(R.id.addFriend);

        addFriend.setOnClickListener(this);

        bar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                //根据字母定位List
                for(int i = 0; i < list.size();i++){
                    Friend friend = list.get(i);
                    String pinyin = friend.getPinyin().substring(0, 1);
                    if(TextUtils.equals(letter,pinyin)){
                        contactList.setSelection(i);
                        break;
                    }
                }
            }
        });

        fillAndSortData(list);
        contactList.setAdapter(new ContactAdapter(mActivity,list));
        contactList.setOnItemClickListener(this);
        return view;
    }

    private void fillAndSortData(List<Friend> list) {
        // TODO: 2017/3/8   设置联系人list
        list.add(new Friend("张三"));
        list.add(new Friend("陈芳"));
        list.add(new Friend("程一峰"));
        list.add(new Friend("杜立明"));
        list.add(new Friend("吕静"));
        list.add(new Friend("尚红艳"));
        list.add(new Friend("韩世飞"));
        list.add(new Friend("王琳琳"));
        list.add(new Friend("王明月"));
        list.add(new Friend("王熙"));
        list.add(new Friend("李志杰"));
        list.add(new Friend("李宁"));
        list.add(new Friend("李伟"));
        list.add(new Friend("付东"));
        list.add(new Friend("武少广"));
        list.add(new Friend("张筱雨"));
        list.add(new Friend("惠鹏"));
        list.add(new Friend("Mirry"));
        list.add(new Friend("David"));
        list.add(new Friend("高凯"));
        list.add(new Friend("蒋雯丽"));

        //进行排序
        Collections.sort(list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addFriend:
                startActivity(new Intent(mActivity, NewFriendActivity.class));
                break;
            default:
                break;
        }
    }
}
