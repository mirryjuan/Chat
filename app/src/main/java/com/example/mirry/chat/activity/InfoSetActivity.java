package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.common.Common;
import com.example.mirry.chat.common.MyOpenHelper;
import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InfoSetActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.male)
    RadioButton male;
    @InjectView(R.id.female)
    RadioButton female;
    @InjectView(R.id.sex)
    RadioGroup sex;
    @InjectView(R.id.accomplish)
    Button accomplish;
    @InjectView(R.id.next)
    TextView next;
    @InjectView(R.id.birthday)
    EditText birthday;
    @InjectView(R.id.phone)
    EditText phone;
    private String accid;
    private int mSex = Common.MALE;
    private String mNickname;
    private String mBirthday;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_set);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        accid = intent.getStringExtra("accid");

        initData();

        next.setOnClickListener(this);
        accomplish.setOnClickListener(this);
        sex.setOnCheckedChangeListener(this);
    }

    private void initData() {
        // TODO: 2017/4/2 设置默认头像
        nickname.setText(accid);   //昵称默认为账号
        sex.check(R.id.male);      //性别默认选中 "男"
    }

    @Override
    public void onClick(View v) {
        mNickname = nickname.getText().toString();
        mBirthday = birthday.getText().toString();
        mPhone = phone.getText().toString();
        switch (v.getId()) {
            case R.id.next:
                updateRemoteData(mNickname, mBirthday, mPhone);
                finish();
                break;
            case R.id.accomplish:
                //更新本地数据库信息
//                updateDatabaseInfo(mNickname, mBirthday, mPhone);
                //更新服务器用户信息
                updateRemoteData(mNickname, mBirthday, mPhone);
                break;
        }
    }

    private void updateDatabaseInfo(String mNickname, String mBirthday,String mPhone) {
        MyOpenHelper helper = new MyOpenHelper(InfoSetActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // TODO: 2017/4/12 头像
        values.put("nickname",mNickname);
        values.put("sex",mSex);
        values.put("birthday",mBirthday);
        values.put("phone",mPhone);
        db.update("users",values,"account = ?",new String[]{ accid });
        db.close();
    }

    private void updateRemoteData(String mNickname, String mBirthday, String mPhone) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        // TODO: 2017/4/12 头像
        fields.put(UserInfoFieldEnum.Name, mNickname);
        fields.put(UserInfoFieldEnum.GENDER, mSex);
        if(mBirthday != null && !mBirthday.equals("")){
            fields.put(UserInfoFieldEnum.BIRTHDAY, mBirthday);
        }

        if(mPhone != null && !mPhone.equals("")){
            fields.put(UserInfoFieldEnum.MOBILE, mPhone);
        }
        NIMClient.getService(UserService.class).updateUserInfo(fields)
                .setCallback(new RequestCallbackWrapper<Void>() {

                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        Toast.makeText(InfoSetActivity.this, "用户信息设置完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.male:
                mSex = Common.MALE;
                break;
            case R.id.female:
                mSex = Common.FEMALE;
                break;
        }
    }
}
