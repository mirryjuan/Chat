package com.example.mirry.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Mirry on 2017/3/16.
 */

public class CommonUtil {
    public static void hideKeyBoard(Context context, View view){

        //隐藏软键盘
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
