package com.ydd.consumer;

import android.annotation.SuppressLint;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求管理，初始化网络配置
 */
 class RxNetWorkManager {
    @SuppressLint("StaticFieldLeak")
    private static RxNetWorkManager ourInstance;
    private Retrofit retrofit;

     static RxNetWorkManager getInstance() {

        if (ourInstance == null) {

            synchronized (RxNetWorkManager.class) {

                if (ourInstance == null) {
                    ourInstance = new RxNetWorkManager();
                    ourInstance.initRetrofit();
                }
            }
        }
        return ourInstance;
    }

    /**
     * 初始化必要对象和参数
     */
    private void initRetrofit() {

        //加入okhttp的日志
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        //打印body级别的数据
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        // 初始化Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

    }


     Retrofit getRetrofit() {

        return retrofit;
    }
}
