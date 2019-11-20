package com.danbai.dbysapp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class PmUtil {
    private static Display getDisplay(Context context) {
        WindowManager wm;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            wm = activity.getWindowManager();
        } else {
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        if (wm != null) {
            return wm.getDefaultDisplay();
        }
        return null;
    }

    /**
     * 获取应用屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        Display display = getDisplay(context);
        if (display == null) {
            return 0;
        }
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    /**
     * 获取应用屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        Display display = getDisplay(context);
        if (display == null) {
            return 0;
        }
        Point point = new Point();
        display.getSize(point);
        return point.y;
    }

    /**
     * 获取实际屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenRealHeight(Context context) {
        Display display = getDisplay(context);
        if (display == null) {
            return 0;
        }
        Point outSize = new Point();
        display.getRealSize(outSize);
        return outSize.y;
    }

    /**
     * 取实际屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenRealWidth(Context context) {
        Display display = getDisplay(context);
        if (display == null) {
            return 0;
        }
        Point outSize = new Point();
        display.getRealSize(outSize);
        return outSize.x;
    }

    public static int getW(Context context) {
        return getScreenWidth(context) / 4 - 12;

    }
}
