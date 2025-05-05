package com.example.morim.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

        public static Point getScreenSize(Context context) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                return size;  // size.x is the width, size.y is the height
            }
            return new Point();  // In case window manager is not available
        }
    }
