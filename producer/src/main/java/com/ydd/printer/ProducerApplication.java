package com.ydd.printer;

import android.app.Application;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ydd.mylibrary.RqManager;

import org.greenrobot.eventbus.EventBus;

public class ProducerApplication extends Application {
    static RqManager rqManager;

    @Override
    public void onCreate() {
        super.onCreate();

        initRq();

    }

    private void initRq() {
        //queue和rootingkey一样可以设置成channelId（一家店一个队列统一命名为channelId）
        rqManager = new RqManager.Builder("202.102.188.56", 43216,
                "admin", "Ydd.app@609", "B", "B", new AsynchronousConfirmListener(),
                null, new AsynchronousExceptionCallback()).isProducer(true).create();

    }

    /**
     * 消费者投递确认回调
     */
    public class AsynchronousConfirmListener implements RqManager.AsynchronousConfirmListener {
        @Override
        public void callback(boolean isSuccess, String msg) {

            if (isSuccess) {

                Log.e("DOAING", "发送成功：" + msg);

            } else {

                //投递消息失败，dialog 进行重试
                Log.e("DOAING", "发送失败：" + msg);

            }
        }
    }

    /**
     * 连接错误信息
     */
    public class AsynchronousExceptionCallback implements RqManager.AsynchronousExceptionCallback {
        @Override
        public void callback(String exception) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            EventBus.getDefault().post(exception);

        }
    }
}
