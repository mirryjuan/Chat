package com.example.mirry.chat.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Mirry on 2017/3/7.
 */

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
