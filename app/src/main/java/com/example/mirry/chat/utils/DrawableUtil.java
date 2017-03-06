package com.example.mirry.chat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.RadioButton;

/**
 * Created by Mirry on 2017/3/6.
 */

public class DrawableUtil {
    public static void setDrawableSize(Context context, RadioButton view, int id,int size){
        Drawable drawable = context.getResources().getDrawable(id);
        drawable.setBounds(0, 0, size, size);          //四个参数依次是距左右边距、距上下边距、长度、宽度
        view.setCompoundDrawables(null, drawable, null, null);     //只放上面
    }
}
