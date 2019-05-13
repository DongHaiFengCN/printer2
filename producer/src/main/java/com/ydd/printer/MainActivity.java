package com.ydd.printer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ydd.mylibrary.RqManager;


public class MainActivity extends AppCompatActivity {


    RqManager rqManager;
    int sum = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRq();
        findViewById(R.id.submit_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rqManager.submit("" + (sum++));

            }
        });
    }

    private void initRq() {
        //queue和rootingkey一样可以设置成channelId（一家店一个队列统一命名为channelId）
        rqManager = new RqManager.Builder("202.102.188.56", 43216,
                "admin", "Ydd.app@609", "B", "B", new AsynchronousConfirmListener(),
                null,new AsynchronousExceptionCallback()).isProducer(true).create();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        rqManager.close();

    }

    public static class AsynchronousConfirmListener implements RqManager.AsynchronousConfirmListener {
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

    public static class AsynchronousExceptionCallback implements RqManager.AsynchronousExceptionCallback {
        @Override
        public void callback(String exception) {

            Log.e("DOAING", exception);
        }
    }
}

