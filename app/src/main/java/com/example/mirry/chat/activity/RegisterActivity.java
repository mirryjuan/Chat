package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mirry.chat.Common;
import com.example.mirry.chat.MyOpenHelper;
import com.example.mirry.chat.R;
import com.example.mirry.chat.bean.User;
import com.example.mirry.chat.view.IconFontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterActivity extends Activity implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    @InjectView(R.id.account)
    EditText account;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.confirmPwd)
    EditText confirmPwd;
    @InjectView(R.id.register)
    Button register;
    @InjectView(R.id.delete_account)
    IconFontTextView deleteAccount;
    @InjectView(R.id.delete_pwd)
    IconFontTextView deletePwd;
    @InjectView(R.id.delete_confirm)
    IconFontTextView deleteConfirm;
    private String mAccid;
    private String mPwd;
    private String mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        account.addTextChangedListener(this);
        register.setOnClickListener(this);

        deleteAccount.setOnClickListener(this);
        deletePwd.setOnClickListener(this);
        deleteConfirm.setOnClickListener(this);

        account.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        confirmPwd.setOnFocusChangeListener(this);
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(account.getText().toString())) {
                    deleteAccount.setVisibility(View.VISIBLE);
                } else {
                    deleteAccount.setVisibility(View.GONE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(password.getText().toString())) {
                    if (deletePwd.getVisibility() != View.VISIBLE) {
                        deletePwd.setVisibility(View.VISIBLE);
                    }
                } else {
                    deletePwd.setVisibility(View.GONE);
                }
            }
        });
        confirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isNotEmpty(confirmPwd.getText().toString())) {
                    if (deleteConfirm.getVisibility() != View.VISIBLE) {
                        deleteConfirm.setVisibility(View.VISIBLE);
                    }
                } else {
                    deleteConfirm.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_account:
                account.setText("");
                password.setText("");
                confirmPwd.setText("");
                deletePwd.setVisibility(View.GONE);
                break;
            case R.id.delete_pwd:
                password.setText("");
                confirmPwd.setText("");
                deletePwd.setVisibility(View.GONE);
                break;
            case R.id.delete_confirm:
                confirmPwd.setText("");
                deletePwd.setVisibility(View.GONE);
                break;
            case R.id.register:
                mAccid = account.getText().toString();
                mPwd = password.getText().toString();
                mConfirm = confirmPwd.getText().toString();
                if (mAccid.equals("")) {
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                } else if (mPwd.equals("")) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (mConfirm.equals(mPwd)) {
                        User newUser = new User();
                        newUser.setAccount(mAccid);
                        newUser.setPassword(mPwd);

                        // TODO: 2017/4/2 调用接口注册   成功后保存信息到数据库,页面跳转
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        insertIntoDatabase();    //插入本地数据库
                        Intent intent = new Intent(RegisterActivity.this, InfoSetActivity.class);
                        intent.putExtra("accid", mAccid);
                        startActivity(intent);
                        this.finish();
                    } else {
                        Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void insertIntoDatabase() {
        MyOpenHelper helper = new MyOpenHelper(RegisterActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("account",mAccid);
        values.put("password",mPwd);
        values.put("nickname",mAccid);
        values.put("sex", Common.MALE);
        db.insert("users",null,values);
        db.close();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO: 2017/4/2 调用接口查询账号是否存在，给出提示
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.account:
                if(hasFocus){
                    if (deletePwd.getVisibility() != View.GONE) {
                        deletePwd.setVisibility(View.GONE);
                    }
                    if(deleteConfirm.getVisibility() != View.GONE){
                        deleteConfirm.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(account.getText().toString())) {
                        deleteAccount.setVisibility(View.VISIBLE);
                    }else{
                        deleteAccount.setVisibility(View.GONE);
                    }
                }else{
                    deleteAccount.setVisibility(View.GONE);
                }
                break;
            case R.id.password:
                if(hasFocus){
                    if (deleteAccount.getVisibility() != View.GONE) {
                        deleteAccount.setVisibility(View.GONE);
                    }
                    if(deleteConfirm.getVisibility() != View.GONE){
                        deleteConfirm.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(password.getText().toString())) {
                        deletePwd.setVisibility(View.VISIBLE);
                    }else{
                        deletePwd.setVisibility(View.GONE);
                    }
                }else{
                    deletePwd.setVisibility(View.GONE);
                }
                break;
            case R.id.confirmPwd:
                if(hasFocus){
                    if (deleteAccount.getVisibility() != View.GONE) {
                        deleteAccount.setVisibility(View.GONE);
                    }
                    if(deletePwd.getVisibility() != View.GONE){
                        deletePwd.setVisibility(View.GONE);
                    }
                    if (StringUtils.isNotEmpty(confirmPwd.getText().toString())) {
                        deleteConfirm.setVisibility(View.VISIBLE);
                    }else{
                        deleteConfirm.setVisibility(View.GONE);
                    }
                }else{
                    deleteConfirm.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }
}
