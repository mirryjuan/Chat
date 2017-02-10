package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mirry.chat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InfoSetActivity extends Activity {

    @InjectView(R.id.back)
    TextView back;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.age)
    EditText age;
    @InjectView(R.id.male)
    RadioButton male;
    @InjectView(R.id.female)
    RadioButton female;
    @InjectView(R.id.sex)
    RadioGroup sex;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.confirmPwd)
    EditText confirmPwd;
    @InjectView(R.id.accomplish)
    Button accomplish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_set);
        ButterKnife.inject(this);
    }
}
