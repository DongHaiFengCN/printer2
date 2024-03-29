package com.ydd.printer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ydd.mylibrary.ExceptionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.ydd.printer.ProducerApplication.rqManager;

public class MainActivity extends AppCompatActivity {
    int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.submit_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rqManager.submit("" + (sum++));

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final String event) {

            Log.e("DOAING",event);
            new AlertDialog
                    .Builder(MainActivity.this)
                    .setTitle("消息队列警告")
                    .setMessage(event)
                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(ExceptionUtil.ConnectException.equals(event)){
                                rqManager.retryConnect();
                                Log.e("DOAING","------");
                            }


                        }
                    }).show();


    }


}

