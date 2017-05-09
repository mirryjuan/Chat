package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.NewFriendAdapter;
import com.example.mirry.chat.utils.CommonUtil;
import com.example.mirry.chat.utils.NimUserInfoCache;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddFriendActivity extends Activity implements TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {

    @InjectView(R.id.et_search)
    EditText searchText;
    @InjectView(R.id.friend_list)
    ListView friendList;
    @InjectView(R.id.clear_search)
    IconFontTextView clearSearch;
    @InjectView(R.id.search)
    IconFontTextView search;
    @InjectView(R.id.back)
    IconFontTextView back;

    private List<Map<String, Object>> list = new ArrayList<>();
    private NewFriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);

        searchText.setOnEditorActionListener(this);
        searchText.addTextChangedListener(this);
        clearSearch.setOnClickListener(this);
        search.setOnClickListener(this);
        back.setOnClickListener(this);

        adapter = new NewFriendAdapter(this, list, false);

        friendList.setAdapter(adapter);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddFriendActivity.this, FriendInfoActivity.class);
                intent.putExtra("info", (Serializable) list.get(position));
                intent.putExtra("isRequest", true);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_SEARCH:
                list.clear();
                searchFriend();
                CommonUtil.hideKeyBoard(AddFriendActivity.this, searchText);
                break;
        }
        return false;
    }

    private void searchFriend() {
        String account = searchText.getText().toString();
        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallback<NimUserInfo>() {
            @Override
            public void onSuccess(NimUserInfo param) {
                if (param == null) {
                    Toast.makeText(AddFriendActivity.this, "没有找到用户", Toast.LENGTH_SHORT).show();
                } else {
//                    Map<String, Object> extensionMap = param.getExtensionMap();
//                    Log.e("info",extensionMap.toString());
                    Map<String, Object> info = new HashMap<>();
                    info.put("account", param.getAccount());
                    info.put("head", param.getAvatar());
                    info.put("nickname", param.getName());
                    info.put("sex", param.getGenderEnum().getValue());
                    info.put("birthday", param.getBirthday());
                    info.put("mobile", param.getMobile());
                    list.add(info);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AddFriendActivity.this, "成功添加到列表中", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int code) {
                Log.e("code", "错误码：" + code);
                switch (code) {
                    case 408:
                        Toast.makeText(AddFriendActivity.this, "请求超时，请稍候重试", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_search:
                searchText.setText("");
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.search:
                list.clear();
                searchFriend();
                CommonUtil.hideKeyBoard(AddFriendActivity.this, searchText);
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtils.isNotEmpty(searchText.getText().toString())) {
            clearSearch.setVisibility(View.VISIBLE);
        } else {
            clearSearch.setVisibility(View.GONE);
        }
    }


}
