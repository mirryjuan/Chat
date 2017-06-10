package com.example.mirry.chat.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.example.mirry.chat.notes.NoteActivity;
import com.example.mirry.chat.service.IflyService;
import com.example.mirry.chat.utils.DrawableUtil;
import com.example.mirry.chat.utils.HeadUtil;
import com.example.mirry.chat.utils.ImageUtil;
import com.example.mirry.chat.utils.PreferencesUtil;
import com.example.mirry.chat.view.CircleImageView;
import com.example.zxing.activity.CaptureActivity;
import com.example.zxing.activity.CodeUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.tencent.open.utils.Global.getPackageName;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, IflyService.OnRecordFinishListener {

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

    private static final int LOAD_MESSAGE_COUNT = 10;

    private Observer<RecentContact> deleteRecentObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            String deleteAccount = null;
            String fromAccount = recentContact.getFromAccount();
            String contactId = recentContact.getContactId();
            if(mAccount.equals(fromAccount) || fromAccount == null){
                deleteAccount = contactId;
            }else{
                deleteAccount = fromAccount;
            }

            if(deleteAccount != null){
                Iterator<Map<String, String>> iterator = msgList.iterator();
                while(iterator.hasNext()){
                    if(iterator.next().get("fromAccount").equals(deleteAccount)){
                        iterator.remove();
                    }
                }
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

    private Observer<FriendChangedNotify> friendChangedNotifyObserver = new Observer<FriendChangedNotify>() {
        @Override
        public void onEvent(FriendChangedNotify friendChangedNotify) {
            List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends(); // 新增的好友
            List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends(); // 删除好友或者被解除好友
        }
    };

    private long exitTime = 0;

    private Bundle msgBundle;
    private Bundle contactBundle;
    private String mAccount;
    private String mHead;

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
        transaction.replace(R.id.fl_left_menu,new MeFragment(),"me");
        transaction.commit();

        ButterKnife.inject(this);

        DrawableUtil.setDrawableSize(this,message,R.drawable.message_tab,60);
        DrawableUtil.setDrawableSize(this,contact,R.drawable.contact_tab,60);
        DrawableUtil.setDrawableSize(this,miniApps,R.drawable.apps_tab,60);

        initData();

        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(messageObserver,true);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentObserver,true);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContactDeleted(deleteRecentObserver,true);
        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, true);

        add.setOnClickListener(this);
        head.setOnClickListener(this);
        tabsGroup.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mAccount = PreferencesUtil.getString(MainActivity.this,"config","account","");
        tabsGroup.check(R.id.message);

        NimUserInfo mInfo = NIMClient.getService(UserService.class).getUserInfo(mAccount);
        if(mInfo != null){
            mHead = mInfo.getAvatar();
            if(mHead != null && !mHead.equals("")){
                HeadUtil.setHead(head,mHead);
            }
        }

        List<SystemMessageType> types = new ArrayList<>();
        types.add(SystemMessageType.AddFriend);

//        // 查询“添加好友”类型的系统通知
//        List<SystemMessage> temps = NIMClient.getService(SystemMessageService.class)
//                .querySystemMessageByTypeBlock(types, 0, LOAD_MESSAGE_COUNT);
//        for (SystemMessage temp:temps) {
//            Map<String,String> newFriendInfo = new HashMap<>();
//            newFriendInfo.put("account",temp.getFromAccount());
//            newFriendInfo.put("content",temp.getContent());
//            newFriendList.add(newFriendInfo);
//        }

        //查询最近联系人列表
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
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
        switch (view.getId()) {
            case R.id.head:
                menu.toggle();
                break;
            case R.id.add:
                showPopupWindow();
                break;
            case R.id.addFriend:
                startActivity(new Intent(MainActivity.this,AddFriendActivity.class));
                break;
            case R.id.scan:
                openCamera();
                break;
            case R.id.voice:
                openVoice();
                break;
        }
    }

    private void openCamera() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                //已经禁止提示了
                Toast.makeText(MainActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Common.CALL_CAMERA);
            }
        } else {
            startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), Common.REQUEST_CODE);
        }
    }

    private void openVoice() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECORD_AUDIO)){
                //已经禁止提示了
                Toast.makeText(MainActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Common.CALL_VOICE);
            }
        } else {
            initIflyService();
        }
    }

    private void initIflyService() {
        IflyService iflyService = new IflyService(MainActivity.this);
        iflyService.getResultOnline();
        iflyService.setListener(this);
    }

    private void openPage(String text) {
        if(popupWindow.isShowing()){
            popupWindow.dismiss();
        }
        Intent intent = null;
        if(text != null){
             if(text.contains(getResources().getString(R.string.voice_friend_add))){
                 intent = new Intent(MainActivity.this,AddFriendActivity.class);
                 startActivity(intent);
                 return;
             }

            if(text.contains(getResources().getString(R.string.voice_scan))){
                openCamera();
                return;
            }

            if(text.contains(getResources().getString(R.string.voice_robot))){
                startActivity(new Intent(MainActivity.this,RobotActivity.class));
                return;
            }

            if(text.contains(getResources().getString(R.string.voice_record))){
                startActivity(new Intent(MainActivity.this,NoteActivity.class));
                return;
            }

            if(text.contains(getResources().getString(R.string.voice_news))){
                intent = new Intent(MainActivity.this,AppsActivity.class);
                intent.putExtra("item","news");
                startActivity(intent);
                return;
            }

            if(text.contains(getResources().getString(R.string.voice_weather))){
                intent = new Intent(MainActivity.this,AppsActivity.class);
                intent.putExtra("item","weather");
                startActivity(intent);
                return;
            }

            if(text.contains(getResources().getString(R.string.voice_joke))){
                intent = new Intent(MainActivity.this,AppsActivity.class);
                intent.putExtra("item","joke");
                startActivity(intent);
                return;
            }
        }else{
            Toast.makeText(this, "未能识别到语音", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopupWindow() {
        View popupView = View.inflate(this, R.layout.popup_add, null);
        Button addFriend = (Button) popupView.findViewById(R.id.addFriend);
        Button scan = (Button) popupView.findViewById(R.id.scan);
        Button voice = (Button) popupView.findViewById(R.id.voice);

        addFriend.setOnClickListener(this);
        scan.setOnClickListener(this);
        voice.setOnClickListener(this);

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

        int xoff = (int) (getScreenWidth() * 0.8);

        popupWindow.showAsDropDown(titleBar, xoff, 20);
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
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentObserver,false);
        NIMClient.getService(MsgServiceObserve.class).observeRecentContactDeleted(deleteRecentObserver,false);
        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, false);
    }

    public Fragment getCurrentFragment(String tag){
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        return fragment;
    }

    @Override
    public void onRecordFinish(String result) {
        Toast.makeText(this, "识别到的文字："+result, Toast.LENGTH_SHORT).show();
        openPage(result);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == Common.REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    Pattern pattern = Pattern
                            .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
                    // 忽略大小写的写法
                    Matcher matcher = pattern.matcher(result);
                    // 字符串是否与正则表达式相匹配
                    boolean rs = matcher.matches();
                    if(rs == true){
                        Uri uri = Uri.parse(result);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "解析结果："+result, Toast.LENGTH_LONG).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }

        /**
         * 选择系统图片并解析
         */
        else if (requestCode == Common.REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(MainActivity.this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else if (requestCode == Common.REQUEST_CAMERA_PERM) {
            Toast.makeText(MainActivity.this, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }
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
            case Common.CALL_CAMERA:
                if(grantResults.length >0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //用户同意授权
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), Common.REQUEST_CODE);
                }else{
                    //用户拒绝授权
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


}
