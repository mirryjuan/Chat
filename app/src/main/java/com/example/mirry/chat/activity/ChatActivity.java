package com.example.mirry.chat.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;
import com.example.mirry.chat.adapter.ChatAdapter;
import com.example.mirry.chat.bean.Friend;
import com.example.mirry.chat.bean.Me;
import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.service.IflyService;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.IconFontTextView;
import com.example.zxing.activity.CaptureActivity;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends BaseActivity implements View.OnClickListener, TextWatcher, IflyService.OnRecordFinishListener {

    @InjectView(R.id.back)
    IconFontTextView back;
    @InjectView(R.id.username)
    TextView username;
    @InjectView(R.id.userinfo)
    IconFontTextView userinfo;
    @InjectView(R.id.chatList)
    ListView chatList;
    @InjectView(R.id.msg)
    EditText msg;
    @InjectView(R.id.send)
    Button send;
    @InjectView(R.id.textmsg)
    LinearLayout textmsg;
    private ChatAdapter adapter = null;
    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;
    private static final int LIMIT = 50;

    private SubActionButton type_text;
    private SubActionButton type_voice_text;
    private FloatingActionMenu actionMenu;


    private String curAccount;
    private String curUsername;
    private String mAccount;

    private List<Object> list = new ArrayList<>();

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    for (IMMessage message : messages) {
                        Friend friend = null;
                        if (!message.getFromNick().equals("")) {
                            friend = new Friend(message.getFromNick());
                        } else {
                            friend = new Friend(message.getFromAccount());
                        }
                        friend.setAccount(message.getFromAccount());
                        friend.setMsg(message.getContent());
                        friend.setType(TYPE_FRIEND);
                        friend.setHead(queryUserHeadUrl(message.getFromAccount()));
                        list.add(friend);
                        adapter.notifyDataSetChanged();
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);

        initView();
        initData();

        back.setOnClickListener(this);
        userinfo.setOnClickListener(this);
        send.setOnClickListener(this);

        msg.addTextChangedListener(this);

        adapter = new ChatAdapter(this, list);
        chatList.setAdapter(adapter);
        chatList.smoothScrollToPosition(adapter.getCount() - 1);

        //true表示注册消息接收观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    private String queryUserHeadUrl(String account){
        String url = "";
        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        if(mInfo != null){
            url = mInfo.getAvatar();
        }
        return url;
    }

    private void initView() {
        final ImageView add = new ImageView(this);
        add.setImageResource(R.drawable.menu_add);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(add)
                .build();


        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.edit));
        type_text = itemBuilder.setContentView(itemIcon).build();

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.voice));
        type_voice_text = itemBuilder.setContentView(itemIcon2).build();

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(type_text)
                .addSubActionView(type_voice_text)
                .attachTo(actionButton)
                .build();

        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {

            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // 逆时针旋转90°
                add.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, -90);

                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(add, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // 顺时针旋转90°
                add.setRotation(-90);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(add, pvhR);
                animation.start();

            }
        });

        setButtonsClickListener();
    }

    private void setButtonsClickListener() {
        type_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                textmsg.setVisibility(View.VISIBLE);
            }
        });

        type_voice_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                openVoice();
            }
        });
    }



    private void selectPic(){
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, Common.PHOTO);
    }

    private void initData() {
        mAccount = PreferencesUtil.getString(ChatActivity.this, "config", "account", "");
        Intent intent = getIntent();
        curAccount = intent.getStringExtra("curAccount");
        curUsername = intent.getStringExtra("curUsername");

        if (curUsername.equals("")) {
            username.setText(curAccount);
        } else {
            username.setText(curUsername);
        }

        NIMClient.getService(MsgService.class).setChattingAccount(curAccount, SessionTypeEnum.P2P);

        if (isNetConnected) {
            list.clear();
            long time = System.currentTimeMillis() + 10000;
            NIMClient.getService(MsgService.class).queryMessageListEx(
                    MessageBuilder.createEmptyMessage(curAccount, SessionTypeEnum.P2P, time),
                    QueryDirectionEnum.QUERY_OLD, LIMIT, true)
                    .setCallback(new RequestCallback<List<IMMessage>>() {
                        @Override
                        public void onSuccess(List<IMMessage> allMessages) {
                            for (IMMessage message : allMessages) {
                                String account = message.getFromAccount();
                                String content = message.getContent();
                                if (account.equals(curAccount)) {
                                    Friend friend = null;
                                    if (!message.getFromNick().equals("")) {
                                        friend = new Friend(message.getFromNick());
                                    } else {
                                        friend = new Friend(account);
                                    }

                                    friend.setAccount(account);
                                    friend.setMsg(content);
                                    friend.setType(TYPE_FRIEND);

                                    friend.setHead(queryUserHeadUrl(account));
                                    list.add(friend);
                                } else if (account.equals(mAccount)) {
                                    Me me = new Me();
                                    me.setMsg(content);
                                    me.setType(TYPE_ME);
                                    me.setHead(queryUserHeadUrl(mAccount));
                                    list.add(me);
                                }
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailed(int code) {

                        }

                        @Override
                        public void onException(Throwable exception) {

                        }
                    });
        } else {
            // TODO: 2017/4/21 加载本地消息
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
                this.finish();
                break;
            case R.id.userinfo:
                Intent intent = new Intent(ChatActivity.this, ContactInfoActivity.class);
                intent.putExtra("account", curAccount);
                startActivity(intent);
                break;
            case R.id.send:
                sendMsg(curAccount);
                break;
        }
    }

    private void openVoice() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
                //已经禁止提示了
                Toast.makeText(ChatActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Common.CALL_VOICE);
            }
        } else {
            initIflyService();
        }
    }

    private void initIflyService() {
        IflyService iflyService = new IflyService(ChatActivity.this);
        iflyService.getResultOnline();
        iflyService.setListener(this);
    }

    private void sendMsg(String id) {
        final String content = msg.getText().toString();
        // 创建文本消息
        IMMessage message = MessageBuilder.createTextMessage(
                id,             //接收者ID
                SessionTypeEnum.P2P, // 单聊
                content // 文本内容
        );
        Me me = new Me();
        me.setMsg(content);
        me.setType(TYPE_ME);
        me.setHead(queryUserHeadUrl(mAccount));
        list.add(me);
        adapter.notifyDataSetChanged();

        msg.setText("");

        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {

            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtils.isNotEmpty(msg.getText().toString())) {
            send.setEnabled(true);
            send.setBackgroundResource(R.drawable.bg_sendmsg_enabled);
        } else {
            send.setEnabled(false);
            send.setBackgroundResource(R.drawable.bg_sendmsg_normal);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //false表示注销消息接收观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    @Override
    public void onNetChange(int netType) {
        isNetConnected = isNetConnect(netType);
        if (isNetConnected) {
            // TODO: 2017/4/21 刷新消息列表
        }
    }

    @Override
    public void onRecordFinish(String result) {
        textmsg.setVisibility(View.VISIBLE);
        msg.setText(result);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Common.CALL_VOICE:
                if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //用户同意授权
                    initIflyService();
                }else{
                    //用户拒绝授权
//                    Toast.makeText(this, "您已拒绝录音权限，语音识别不可用", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");

                    String pkg = "com.android.settings";
                    String cls = "com.android.settings.applications.InstalledAppDetails";

                    intent.setComponent(new ComponentName(pkg, cls));
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        finish();
    }
}
