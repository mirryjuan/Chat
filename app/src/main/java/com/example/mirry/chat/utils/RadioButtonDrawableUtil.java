package com.example.mirry.chat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.RadioButton;

public class RadioButtonDrawableUtil {
    public static void setDrawableSize(Context context, RadioButton radioButton, int id, int length){
        Drawable drawable = context.getResources().getDrawable(id);
        drawable.setBounds(0, 2, length, length);
        radioButton.setCompoundDrawables(null, drawable, null, null);//只放上面
    }
}
