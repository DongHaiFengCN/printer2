package com.ydd.consumer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ydd.mylibrary.RqManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static RqManager rqManager;
    public TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorTv = findViewById(R.id.error_tv);

        initRq();
        initDevice();

    }

    private void initDevice() {

        RxNetWorkManager
                .getInstance(getApplicationContext())
                .getRetrofit()
                .create(DeviceService.class)
                .getDevice("733d2f51")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<DeviceResponse>() {
                    @Override
                    protected void onCustomNext(DeviceResponse o) {

                        if (o.getCode() == 0) {

                            Log.e("DOAING", o.getData().size() + "");
                        }
                    }
                    @Override
                    protected void onCustomError(String o) {

                        Log.e("DOAING", o);
                    }
                });

    }

    private void initRq() {

        //queue和rootingkey一样可以设置成channelId（一家店一个队列统一命名为channelId）
        rqManager = new RqManager.Builder("202.102.188.56", 43216,
                "admin", "Ydd.app@609", "B", "B",
                null, new AsynchronousConsumerListener(),new AsynchronousExceptionCallback()).isProducer(false).create();

    }

    public static class AsynchronousConsumerListener implements RqManager.AsynchronousConsumerListener {
        @Override
        public void consumer(String msg, long deliveryTag) {

            Log.e("DOAING", msg);
            rqManager.basicAck(deliveryTag);
        }

    }

    public static class AsynchronousExceptionCallback implements RqManager.AsynchronousExceptionCallback {
        @Override
        public void callback(String exception) {

            Log.e("DOAING", exception);
        }
    }
}
