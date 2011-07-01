package com.galapk.litelisten.util;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

/**
 * The Toast util class
 * @author pangsy
 * @createDate 2011-7-1
 * @version v0.1
 */
public final class ToastUtil {

    /**
     * Toast the long text
     * @param context
     * @param text
     */
    public static void ToastLong(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Toast the long text
     * @param context
     * @param resId
     */
    public static void ToastLong(Context context, int resId) {
    	Resources res = context.getResources();
    	Toast.makeText(context, res.getString(resId), Toast.LENGTH_LONG).show();
    }
    
    /**
     * Toast the short text
     * @param context
     * @param resId
     */
    public static void ToastShort(Context context, int resId) {
        Resources res = context.getResources();
    	Toast.makeText(context, res.getString(resId), Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Toast the short text
     * @param context
     * @param text
     */
    public static void ToastShort(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}