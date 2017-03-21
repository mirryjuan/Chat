package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.NewFriendAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewFriendActivity extends Activity {

    @InjectView(R.id.friend_list)
    ListView friendList;
    private List<Map<String,Object>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_friend);
        ButterKnife.inject(this);

        NewFriendAdapter adapter = new NewFriendAdapter(NewFriendActivity.this,list,true);
        friendList.setAdapter(adapter);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewFriendActivity.this, FriendInfoActivity.class);
                intent.putExtra("info", (Serializable) list.get(position));
                startActivity(intent);
            }
        });
    }
}
