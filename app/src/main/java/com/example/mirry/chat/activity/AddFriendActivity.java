package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddFriendActivity extends Activity implements TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {

    @InjectView(R.id.et_search)
    EditText search;
    @InjectView(R.id.friend_list)
    ListView friendList;
    @InjectView(R.id.clear_search)
    IconFontTextView clearSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);

        search.setOnEditorActionListener(this);
        search.addTextChangedListener(this);
        clearSearch.setOnClickListener(this);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId){
            case EditorInfo.IME_ACTION_SEARCH:
                Toast.makeText(this, search.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_search:
                search.setText("");
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
        if (StringUtils.isNotEmpty(search.getText().toString())) {
            clearSearch.setVisibility(View.VISIBLE);
        } else {
            clearSearch.setVisibility(View.GONE);
        }
    }

    private void addFriend(String account) {
        final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
        String msg = "好友请求附言";
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                .setCallback(new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        // TODO: 2017/3/16 添加至联系人列表
                    }

                    @Override
                    public void onFailed(int code) {
                        // TODO: 2017/3/16 返回失败消息

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }
}
