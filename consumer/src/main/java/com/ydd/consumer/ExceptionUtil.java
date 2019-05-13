package com.ydd.consumer;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

public class ExceptionUtil {
    public static String Exception(Exception e){

        String msg = "连接失败";

        if(e instanceof ConnectException ){

            msg = "连接失败请重试";
        }else if(e instanceof TimeoutException){

            msg = "连接超时请重试";

        }
        return msg;
    }
}
