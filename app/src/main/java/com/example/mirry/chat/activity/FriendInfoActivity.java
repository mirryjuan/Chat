package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;
import com.example.mirry.chat.view.IconFontTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;

import java.io.Serializable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendInfoActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.back)
    IconFontTextView back;
    @InjectView(R.id.add)
    ImageView add;
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.sendMsg)
    ImageView sendMsg;
    @InjectView(R.id.nickName)
    TextView nickName;
    @InjectView(R.id.accid)
    TextView accid;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.birthday)
    TextView birthday;
    private Map<String,Object> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_info);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        info = (Map<String, Object>) intent.getSerializableExtra("info");
        back.setOnClickListener(this);
        add.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
    }


    private void addFriend(String account) {
        final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
        String msg = "好友请求附言";
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                .setCallback(new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        // TODO: 2017/3/16 添加至联系人列表
                        Toast.makeText(FriendInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int code) {
                        // TODO: 2017/3/16 返回失败消息
                        Toast.makeText(FriendInfoActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.add:
                Toast.makeText(this, "添加", Toast.LENGTH_SHORT).show();
                addFriend(info.get("account").toString());
                break;
            case R.id.sendMsg:
                Intent intent = new Intent(FriendInfoActivity.this,ChatActivity.class);
                intent.putExtra("info", (Serializable) info);
                startActivity(intent);
                break;
        }
    }
}
