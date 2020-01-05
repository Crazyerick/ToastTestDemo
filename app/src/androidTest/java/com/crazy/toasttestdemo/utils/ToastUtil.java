package com.crazy.toasttestdemo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Crazy on 2017-06-4.
 */
public class ToastUtil {

    private static final String TAG = "ToastUtil";
    private static ToastUtil instance = null;
    private static Handler mWorkerHandler;
    private Context mContext;

    static {
        Thread workerThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                mWorkerHandler = new Handler();
                Looper.loop();
            }
        };
        workerThread.start();
    }

    private ToastUtil(Context context) {
        this.mContext = context;
    }

    public static ToastUtil getInstance(Context context) {
        if (instance == null) {
            instance = new ToastUtil(context);
        }
        return instance;
    }

    /**
     * 显示Toast公开方法
     */
    public void showToast(String msg) {
        ShowToastRunnable runnable = new ShowToastRunnable(msg);
        if (mWorkerHandler != null) {
            mWorkerHandler.post(runnable);
        }
    }

    private class ShowToastRunnable implements Runnable {

        private String msg;

        ShowToastRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
