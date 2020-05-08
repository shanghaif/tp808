package com.library.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class CameraUtil {
    public static Point getDisplaySize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point;
    }
}
