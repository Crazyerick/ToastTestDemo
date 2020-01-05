package com.crazy.toasttestdemo.utils;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.test.uiautomator.UiDevice;

import java.io.IOException;

/**
 * Created by Crazy on 2017/6/4.
 */
public class CommonUtil {

    private static final String TAG = "CommonUtil";
    private static CommonUtil instance = null;
    private Instrumentation mInstrumentation;
    private UiDevice mUiDevice;
    private Context mContext;

    public static  CommonUtil getInstance(Instrumentation instrumentation) {
        if(instance == null) {
            instance = new CommonUtil(instrumentation);
        }
        return instance;
    }

    private CommonUtil(Instrumentation instrumentation) {
        this.mInstrumentation = instrumentation;
        this.mUiDevice = UiDevice.getInstance(instrumentation);
        this.mContext = instrumentation.getContext();
    }

    public void sleep(long timeout) {
        SystemClock.sleep(timeout);
    }

    public void startApp(String packageName) {
        PackageManager pm = mContext.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        mContext.startActivity(intent);
    }

    public void exitApp(String packageName) throws IOException {
        this.exec("am force-stop " + packageName);
    }

    private String exec(String command) throws IOException {
        return mUiDevice.executeShellCommand(command);
    }

}
