package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mirry.chat.Common;
import com.example.mirry.chat.MyOpenHelper;
import com.example.mirry.chat.R;
import com.example.mirry.chat.view.CircleImageView;

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
        switch (v.getId()) {
            case R.id.next:
                finish();
                break;
            case R.id.accomplish:
                // TODO: 2017/4/2 服务器数据库
                updateUserData();
                finish();
                break;
        }
    }

    private void updateUserData() {
        MyOpenHelper helper = new MyOpenHelper(InfoSetActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname",nickname.getText().toString());
        values.put("sex",mSex);
        values.put("birthday",birthday.getText().toString());
        values.put("phone",phone.getText().toString());
        db.update("users",values,"account = ?",new String[]{ accid });
        db.close();
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
