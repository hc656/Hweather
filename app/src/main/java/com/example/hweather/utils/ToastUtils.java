package com.example.hweather.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showLongToast(Context context,CharSequence tx){
        Toast.makeText(context.getApplicationContext(), tx, Toast.LENGTH_LONG).show();
    }
    public static void showShortToast(Context context,CharSequence tx){
        Toast.makeText(context.getApplicationContext(), tx, Toast.LENGTH_SHORT).show();
    }
}
