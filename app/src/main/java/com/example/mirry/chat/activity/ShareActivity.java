package com.example.mirry.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.example.mirry.chat.R;
import com.example.mirry.chat.view.IconFontTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShareActivity extends Activity {

    @InjectView(R.id.back)
    IconFontTextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share);
        ButterKnife.inject(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
