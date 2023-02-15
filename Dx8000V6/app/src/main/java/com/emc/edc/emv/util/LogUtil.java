package com.emc.edc.emv.util;

import android.util.Log;

import com.emc.edc.constant.DemoConfig;

public class LogUtil {

    public static void d(String message) {
        Log.d(DemoConfig.TAG, message);
    }

    public static void e(String message) {
        Log.e(DemoConfig.TAG, message);
    }
}
