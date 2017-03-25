package com.example.mirry.chat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    TextView account;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.birthday)
    TextView birthday;
    @InjectView(R.id.img_sex)
    ImageView sexImg;
    @InjectView(R.id.refuse)
    ImageView refuse;
    @InjectView(R.id.info_phone)
    LinearLayout phoneInfo;
    @InjectView(R.id.info_birthday)
    LinearLayout birthdayInfo;
    private Map<String, Object> info;

    private Boolean isRequest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_info);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        info = (Map<String, Object>) intent.getSerializableExtra("info");
        isRequest = intent.getBooleanExtra("isRequest", true);

        initData();
        if (!isRequest) {
            refuse.setVisibility(View.VISIBLE);
            sendMsg.setVisibility(View.GONE);
            refuse.setClickable(true);
            sendMsg.setClickable(false);
        } else {
            refuse.setVisibility(View.GONE);
            sendMsg.setVisibility(View.VISIBLE);
            refuse.setClickable(false);
            sendMsg.setClickable(true);
        }
        back.setOnClickListener(this);
        add.setOnClickListener(this);
        refuse.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
    }

    private void initData() {
//            if(info.get("head") != null){
//                head.setImageResource((Integer)info.get("head"));
//            }
        nickName.setText(info.get("nickname") == null ? "" : info.get("nickname").toString());
        account.setText(info.get("account") == null ? "" : info.get("account").toString());
        if (info.get("sex").equals(2)) {
            sexImg.setImageResource(R.drawable.sex_female);
        } else {
            sexImg.setImageResource(R.drawable.sex_male);
        }
        if (info.get("phone") == null) {
            phoneInfo.setVisibility(View.GONE);
        }else{
            phone.setText(info.get("phone").toString());
        }

        if (info.get("birthday") == null) {
            birthdayInfo.setVisibility(View.GONE);
        }else{
            birthday.setText(info.get("birthday").toString());
        }
    }


    private void addFriend(final String account) {
        if (isRequest) {
            requestAddFriend(account);
        } else {
            NIMClient.getService(FriendService.class).ackAddFriendRequest(account, true); // 通过对方的好友请求
            refuse.setVisibility(View.GONE);
            sendMsg.setVisibility(View.VISIBLE);
            refuse.setClickable(false);
            sendMsg.setClickable(true);
            // TODO: 2017/3/19 加入通讯录
        }
    }

    private void requestAddFriend(final String account) {
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("验证信息")
                .setView(editText)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
                        String msg = editText.getText().toString();
                        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                                .setCallback(new RequestCallback<Void>() {

                                    @Override
                                    public void onSuccess(Void param) {
                                        // TODO: 2017/3/16  返回请求发送成功信息
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
                });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.add:
                addFriend(info.get("account").toString());
                break;
            case R.id.sendMsg:
                Intent intent = new Intent(FriendInfoActivity.this, ChatActivity.class);
                intent.putExtra("info", (Serializable) info);
                startActivity(intent);
                break;
            case R.id.refuse:
                // 拒绝对方的好友请求
                NIMClient.getService(FriendService.class).ackAddFriendRequest(info.get("account").toString(), false);
                break;
            default:
                break;
        }
    }
}
