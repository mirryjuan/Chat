package com.example.mirry.chat.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.mirry.chat.activity.FriendInfoActivity;

/**
 * Created by Mirry on 2017/3/5.
 */

public class IconFontTextView extends TextView {
    public  IconFontTextView(Context context) {
        super(context);
        init(context);
    }

    public IconFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "iconFonts/iconfont.ttf");
        setTypeface(iconfont);
    }


}
