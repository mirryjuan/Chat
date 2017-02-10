package com.example.mirry.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mirry.chat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InfoSetActivity extends Activity {

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

        accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoSetActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "按下Back键", Toast.LENGTH_SHORT).show();
    }
}
