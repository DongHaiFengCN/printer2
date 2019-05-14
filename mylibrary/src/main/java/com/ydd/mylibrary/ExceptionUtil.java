package com.ydd.mylibrary;

import com.rabbitmq.client.AlreadyClosedException;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

public class ExceptionUtil {

    public static final String AlreadyClosedExceptionMsg = "网络好像断开了，检查一下，再试试吧";

    public static final String ConnectException = "网络没有连接，检查一下，再试试吧";

    static String getMsg(Exception e) {

        String msg = "消息队列服务器连接错误";

        if (e instanceof ConnectException) {

            msg = ConnectException;
        } else if (e instanceof TimeoutException) {

            msg = "服务器有点忙，等下再试试";

        } else if (e instanceof AlreadyClosedException) {

            msg = AlreadyClosedExceptionMsg;

        }
        return msg;
    }
}
