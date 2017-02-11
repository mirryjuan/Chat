package com.example.mirry.chat.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.example.mirry.chat.R;
import com.example.mirry.chat.fragment.AppsFragment;
import com.example.mirry.chat.fragment.ContactFragment;
import com.example.mirry.chat.fragment.MeFragment;
import com.example.mirry.chat.fragment.MessageFragment;
import com.example.mirry.chat.view.CircleImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends SlidingFragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

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
    private SlidingMenu slidingMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initData();

        add.setOnClickListener(this);
        head.setOnClickListener(this);
        tabsGroup.setOnCheckedChangeListener(this);
    }

    private void initData() {
        //设置侧边栏
        setBehindContentView(R.layout.menu_left);
        slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffset(200);// 设置预留屏幕的宽度

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_left_menu,new MeFragment());
        transaction.commit();

        tabsGroup.check(R.id.message);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        title.setText("消息");
        FragmentManager fragmentManager = getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new MessageFragment());
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
                transaction.replace(R.id.content, new MessageFragment());
                break;
            case R.id.contact:
                title.setText("联系人");
                transaction.replace(R.id.content, new ContactFragment());
                break;
            case R.id.miniApps:
                title.setText("小应用");
                transaction.replace(R.id.content, new AppsFragment());
                break;
        }
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head:
                slidingMenu.toggle();
                break;
            case R.id.add:
                showPopupWindow();
                break;
            case R.id.addFriend:
                Toast.makeText(this, "加好友", Toast.LENGTH_SHORT).show();
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

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
}
