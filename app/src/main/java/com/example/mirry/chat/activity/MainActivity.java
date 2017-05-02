package com.example.mirry.chat.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.R;
import com.example.mirry.chat.fragment.AppsFragment;
import com.example.mirry.chat.fragment.ContactFragment;
import com.example.mirry.chat.fragment.MeFragment;
import com.example.mirry.chat.fragment.MsgFragment;
import com.example.mirry.chat.utils.DrawableUtil;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

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
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.add)
    TextView add;
    @InjectView(R.id.titleBar)
    RelativeLayout titleBar;
    private SlidingMenu menu;
    private PopupWindow popupWindow;

    private List<Map<String,String>> newFriendList = new ArrayList<>();
    private List<Map<String,String>> msgList = new ArrayList<>();

    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            for (MessageReceipt receipt:messageReceipts) {

            }
        }
    };

    private Observer<List<RecentContact>> recentObserver =
            new Observer<List<RecentContact>>() {
                @Override
                public void onEvent(List<RecentContact> messages) {
                    for (RecentContact message : messages) {
                        Bundle bundle = new Bundle();
                        if(!message.getFromAccount().equals(mAccount)){
                            bundle.putString("fromAccount",message.getFromAccount());
                            bundle.putString("fromNick",message.getFromNick());
                            bundle.putString("content",message.getContent());
                            bundle.putInt("count",message.getUnreadCount());

                            Map<String,String> msg = new HashMap<>();
                            msg.put("fromAccount",message.getFromAccount());
                            msg.put("fromNick",message.getFromNick());
                            msg.put("content",message.getContent());
                            msg.put("count", String.valueOf(message.getUnreadCount()));
                            msgList.add(msg);
                        }else{
                            NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(message.getContactId());
                            bundle.putString("fromAccount",message.getContactId());
                            bundle.putString("fromNick",user.getName());
                            bundle.putString("content",message.getContent());
                            bundle.putInt("count",message.getUnreadCount());

                            Map<String,String> msg = new HashMap<>();
                            msg.put("fromAccount",message.getContactId());
                            msg.put("fromNick",user.getName());
                            msg.put("content",message.getContent());
                            msg.put("count", String.valueOf(message.getUnreadCount()));
                            msgList.add(msg);
                        }

                        MsgFragment msgFragment = (MsgFragment) getCurrentFragment("message");
                        if(msgFragment != null){
                            Handler handler = msgFragment.getHandler();
                            Message msg = handler.obtainMessage();
                            msg.what = Common.MSG_COMING;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                }
            };

    private Observer<SystemMessage> messageObserver = new Observer<SystemMessage>() {

        @Override
        public void onEvent(SystemMessage message) {
            if (message.getType() == SystemMessageType.AddFriend) {
                AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                if (attachData != null) {
                    if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                        // 对方直接添加你为好友
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                        // 对方通过了你的好友验证请求
                        ContactFragment contactFragment = (ContactFragment) getCurrentFragment("contact");
                        if(contactFragment != null){
                            Handler handler = contactFragment.getHandler();
                            Message msg = handler.obtainMessage();
                            msg.what = Common.FRIEND_ADD;
                            msg.obj = message.getFromAccount();
                            handler.sendMessage(msg);
                        }
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                        // 对方拒绝了你的好友验证请求
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。
                        Map<String,String> newFriendInfo = new HashMap<>();
                        newFriendInfo.put("account",message.getFromAccount());
                        newFriendInfo.put("content",message.getContent());
                        newFriendList.add(newFriendInfo);
                    }
                }
            }
        }
    };

    private Observer<Integer> countObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {

        }
    };

    private long exitTime = 0;

    private Bundle msgBundle;
    private Bundle contactBundle;
    private String mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindOffset(200);

        menu.setFadeDegree(0.35f);       // 设置渐入渐出效果的值
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        menu.setMenu(R.layout.menu_left); //为侧滑菜单设置布局;


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_left_menu,new MeFragment());
        transaction.commit();

        ButterKnife.inject(this);

        DrawableUtil.setDrawableSize(this,message,R.drawable.message_tab,60);
        DrawableUtil.setDrawableSize(this,contact,R.drawable.contact_tab,60);
        DrawableUtil.setDrawableSize(this,miniApps,R.drawable.apps_tab,60);

        initData();

        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(messageObserver,true);
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(countObserver,true);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentObserver,true);
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, true);

        add.setOnClickListener(this);
        head.setOnClickListener(this);
        tabsGroup.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mAccount = PreferencesUtil.getString(MainActivity.this,"config","account","");
        tabsGroup.check(R.id.message);
        NIMClient.getService(MsgService.class).queryRecentContacts()
                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable e) {
                        for (RecentContact recent:recents) {
                            if(!recent.getFromAccount().equals(mAccount)) {
                                Map<String, String> msg = new HashMap<>();
                                msg.put("fromAccount", recent.getFromAccount());
                                msg.put("fromNick", recent.getFromNick());
                                msg.put("content", recent.getContent());
                                msg.put("count", String.valueOf(recent.getUnreadCount()));
                                msgList.add(msg);
                            }else{
                                NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(recent.getContactId());
                                Map<String,String> msg = new HashMap<>();
                                msg.put("fromAccount",recent.getContactId());
                                msg.put("fromNick",user.getName());
                                msg.put("content",recent.getContent());
                                msg.put("count", String.valueOf(recent.getUnreadCount()));
                                msgList.add(msg);
                            }
                        }
                        setDefaultFragment();
                    }
                });
    }

    private void setDefaultFragment() {
        title.setText("消息");
        FragmentManager fragmentManager = getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MsgFragment msgFragment = new MsgFragment();
        if(msgBundle == null){
            msgBundle = new Bundle();
        }
        msgBundle.putSerializable("messages",(Serializable)msgList);
        msgFragment.setArguments(msgBundle);
        transaction.replace(R.id.content, msgFragment,"message");
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fragmentManager = getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            case R.id.message:
                title.setText("消息");
                MsgFragment msgFragment = new MsgFragment();
                if(msgBundle == null){
                    msgBundle = new Bundle();
                }
                msgBundle.putSerializable("messages",(Serializable)msgList);
                msgFragment.setArguments(msgBundle);
                transaction.replace(R.id.content, msgFragment,"message");
                break;
            case R.id.contact:
                title.setText("联系人");
                ContactFragment contactFragment = new ContactFragment();
                if(contactBundle == null){
                    contactBundle = new Bundle();
                }
                contactBundle.putSerializable("newFriend", (Serializable) newFriendList);
                contactFragment.setArguments(contactBundle);
                transaction.replace(R.id.content, contactFragment,"contact");
                break;
            case R.id.miniApps:
                title.setText("小应用");
                transaction.replace(R.id.content, new AppsFragment(),"miniApps");
                break;
        }
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head:
                menu.toggle();
                break;
            case R.id.add:
                showPopupWindow();
                break;
            case R.id.addFriend:
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                startActivity(new Intent(MainActivity.this,AddFriendActivity.class));
                break;
            case R.id.scan:
                Toast.makeText(this, "扫一扫", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showPopupWindow() {
        View popupView = View.inflate(this, R.layout.popup_add, null);
        Button addFriend = (Button) popupView.findViewById(R.id.addFriend);
        Button scan = (Button) popupView.findViewById(R.id.scan);

        addFriend.setOnClickListener(this);
        scan.setOnClickListener(this);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(popupView);
        popupWindow.setAnimationStyle(R.anim.anim_popup);  //设置加载动画

        //点击非PopupWindow区域，PopupWindow会消失的
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;       // 如果返回true，touch事件将被拦截
            }
        });

        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        int xoff = (int) (getScreenWidth() * 0.74);

        popupWindow.showAsDropDown(titleBar, xoff, 20);
    }

    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    public void onBackPressed() {
        if(menu.isMenuShowing()){
            menu.toggle();
        }else {
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(messageObserver,false);
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(countObserver,false);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentObserver,false);
        NIMClient.getService(MsgServiceObserve.class).observeMessageReceipt(messageReceiptObserver, false);
    }

    public Fragment getCurrentFragment(String tag){
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        return fragment;
    }

}
