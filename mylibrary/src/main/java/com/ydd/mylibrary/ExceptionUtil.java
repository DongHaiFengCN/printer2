package com.ydd.mylibrary;

import com.rabbitmq.client.AlreadyClosedException;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

public class ExceptionUtil {
    public static String getMsg(Exception e){

        String msg = "消息队列服务器连接错误";

        if(e instanceof ConnectException ){

            msg = "消息队列服务器连接失败请重试";
        }else if(e instanceof TimeoutException){

            msg = "消息队列服务器连接超时请重试";

        }else if (e instanceof AlreadyClosedException){

            msg = "网络断开警告，检查网络状态重新连接";

        }
        return msg;
    }
}
