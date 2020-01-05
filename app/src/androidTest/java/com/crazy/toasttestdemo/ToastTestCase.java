package com.crazy.toasttestdemo;

import android.app.Instrumentation;
import android.app.Notification;
import android.app.UiAutomation;
import android.content.Context;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.crazy.toasttestdemo.utils.CommonUtil;
import com.crazy.toasttestdemo.utils.ToastUtil;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

/**
 * Created by Crazy on 2017/6/4.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ToastTestCase {

    private static final String TAG = "ToastTestCase";
    private static final String testedPackageName = "com.crazy.demo";
    private static final long TIMEOUT = 3000;
    private static Instrumentation mInstrumentation;
    private static Context mContext;
    private static Context mTargetContext;
    private static UiDevice mDevice;
    private static CommonUtil mCommonUtil;
    private static ToastUtil mToastUtil;

    /* Toast的内容 */
    private static String toastMessage;

    /* Toast产生时间 */
    private static long toastOccurTime ;

    @BeforeClass
    public static void beforeClass() {
        //初始化参数
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = InstrumentationRegistry.getContext();
        mTargetContext = InstrumentationRegistry.getTargetContext();
        mDevice = UiDevice.getInstance(mInstrumentation);
        mCommonUtil = CommonUtil.getInstance(mInstrumentation);
        mToastUtil = ToastUtil.getInstance(mTargetContext);

        //初始化ToastListener
        initToastListener();
    }

    @AfterClass
    public static void afterClass() {

    }

    @Before
    public void before() {
        mDevice.pressHome();

        //启动应用
        mCommonUtil.startApp(testedPackageName);
    }

    @After
    public void after() {
        mDevice.pressHome();

        //退出应用
        try {
            mCommonUtil.exitApp(testedPackageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCatchToast() {
        mCommonUtil.sleep(2000);
        final long startTimeMillis = SystemClock.uptimeMillis();
        mDevice.wait(Until.findObject(By.res("com.crazy.demo:id/button")), TIMEOUT).click();
        boolean isSuccessfulCatchToast;
        while (true) {
            long currentTimeMillis = SystemClock.uptimeMillis();
            long elapsedTimeMillis = currentTimeMillis - startTimeMillis;
            if (elapsedTimeMillis > 5000L) {
                Log.i(TAG, "超过5s未能捕获到预期Toast!");
                isSuccessfulCatchToast = false;
                break;
            }
            if (toastOccurTime > startTimeMillis) {
                isSuccessfulCatchToast = "测试抓取Toast".equals(toastMessage);
                break;
            }
        }
        Assert.assertTrue("捕获预期Toast失败!", isSuccessfulCatchToast);
    }

    @Test
    public void testShowToast() {
        mCommonUtil.sleep(2000);
        mDevice.wait(Until.findObject(By.desc("更多选项")), TIMEOUT).click();
        mCommonUtil.sleep(1000);
        mDevice.pressBack();
        mToastUtil.showToast("测试结束");
    }

    private static void initToastListener() {
        mInstrumentation.getUiAutomation().setOnAccessibilityEventListener(new UiAutomation.OnAccessibilityEventListener() {
            @Override
            public void onAccessibilityEvent(AccessibilityEvent event) {
                Log.i(TAG, "onAccessibilityEvent: " + event.toString());
                //判断是否是通知事件
                if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                    return;
                }
                //获取消息来源
                String sourcePackageName = (String) event.getPackageName();
                //获取事件具体信息
                Parcelable parcelable = event.getParcelableData();
                //如果不是下拉通知栏消息，则为其它通知信息，包括Toast
                if (!(parcelable instanceof Notification)) {
                    toastMessage = (String) event.getText().get(0);
                    toastOccurTime = event.getEventTime();
                    Log.i(TAG, "Latest Toast Message: " + toastMessage + " [Time: " + toastOccurTime + ", Source: " + sourcePackageName + "]");
                }
            }
        });
    }

}
