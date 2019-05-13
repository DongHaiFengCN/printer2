package com.ydd.mylibrary;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LongSparseArray;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RqManager {

    private Channel channel;
    private Connection connection;
    private ExecutorService cachedThreadPool;
    private LongSparseArray<String> longSparseArray;
    private static final String TAG = "DOAING";
    private String routingKey;
    private static final String exchange = TAG;
    private static boolean producer = false;
    private ConnectionFactory factory;

    private String queue;
    /**
     * 生产者模式下，返回发送的信息是否成功的回调接口
     */
    private AsynchronousConfirmListener asynchronousConfirmListener;

    /**
     * 消费者异步回调监听
     */
    private AsynchronousConsumerListener asynchronousConsumerListener;

    /**
     * 错误信息的异步回调
     */
    private AsynchronousExceptionCallback asynchronousExceptionCallback;

    private RqManager(final String host, final int port,
                      final String userName, final String passWord,
                      final String routingKey, final String queue,
                      final AsynchronousConfirmListener asynchronousConfirmListener,
                      final AsynchronousConsumerListener asynchronousConsumerListener,
                      final AsynchronousExceptionCallback asynchronousExceptionCallback) {

        this.queue = queue;
        this.asynchronousConfirmListener = asynchronousConfirmListener;
        this.asynchronousConsumerListener = asynchronousConsumerListener;
        this.routingKey = routingKey;
        this.asynchronousExceptionCallback = asynchronousExceptionCallback;

        cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                factory = new ConnectionFactory();
                factory.setUsername(userName);// 用户名
                factory.setPassword(passWord);// 密码
                factory.setHost(host);//主机地址
                factory.setPort(port);// 端口号
                factory.setRequestedHeartbeat(60);//心跳时间（秒）
                factory.setAutomaticRecoveryEnabled(true);// 设置连接恢复
                factory.setNetworkRecoveryInterval(3000);//重连时间
                connect(factory, queue, routingKey, asynchronousConfirmListener, asynchronousConsumerListener);

            }
        });
    }

    /**
     * 重新连接消息队列
     */
    public void retryConnect() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                connect(factory, queue, routingKey, asynchronousConfirmListener, asynchronousConsumerListener);

            }
        });
    }

    private void connect(ConnectionFactory factory, String queue, String routingKey, final AsynchronousConfirmListener asynchronousConfirmListener, final AsynchronousConsumerListener asynchronousConsumerListener) {
        try {

            Log.e("DOAING", "正在连接。。");
            connection = factory.newConnection();//创建连接
            Log.e("DOAING", "连接成功");
            Log.e("DOAING", "创建通道。。");
            channel = connection.createChannel();
            Log.e("DOAING", "创建通道成功");

            if (producer) {

                longSparseArray = new LongSparseArray<>();

                // 1  创建一个type="direct" 持久化的，非自动删除的交换器
                channel.exchangeDeclare(exchange, "direct", true, false, null);

                // 2  创建一个持久化，非排他的，非自动删除的队列
                channel.queueDeclare(queue, true, false, false, null);

                //3 绑定交换器与队列通过路由键进行绑定(队列，交换机)
                channel.queueBind(queue, exchange, routingKey);
                Log.e(TAG, "生产者模式");

                // 4 开启校验模式
                channel.confirmSelect();

                channel.addConfirmListener(new ConfirmListener() {
                    @Override
                    public void handleAck(long deliveryTag, boolean multiple) {

                        asynchronousConfirmListener.callback(true, longSparseArray.get(deliveryTag));
                        longSparseArray.remove(deliveryTag);

                    }

                    @Override
                    public void handleNack(long deliveryTag, boolean multiple) {

                        asynchronousConfirmListener.callback(false, longSparseArray.get(deliveryTag));
                        longSparseArray.remove(deliveryTag);

                    }
                });

            } else {

                channel.basicQos(1);//一次接收一个
                Log.e(TAG, "消费者模式");

                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {

                        asynchronousConsumerListener.consumer(new String(body), envelope.getDeliveryTag());

                    }
                };
                //绑定消费者与队列，不自动消费掉信息
                channel.basicConsume(queue, false, consumer);
            }

        } catch (Exception e) {

            //TODO 初始化错误的信息在这里返回
            asynchronousExceptionCallback.callback(ExceptionUtil.getMsg(e));
        }

    }

    /**
     * 手动消费掉信息
     * @param tag
     */
    public void basicAck(long tag) {

        try {

            //通过这句代码来控制是否接收下一个数据
            channel.basicAck(tag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        RqManager.Params params;

        public Builder(@NonNull String host, int port, @NonNull String userName, @NonNull String passWord,
                       @NonNull String routingKey, @NonNull String queue, AsynchronousConfirmListener asynchronousConfirmListener,
                       AsynchronousConsumerListener asynchronousConsumerListener, @NonNull AsynchronousExceptionCallback asynchronousExceptionCallback) {
            params = new Params(host, port, userName, passWord, routingKey, queue, asynchronousConfirmListener, asynchronousConsumerListener, asynchronousExceptionCallback);
        }

        public Builder isProducer(boolean p) {

            producer = p;
            return this;
        }

        public RqManager create() {

            return new RqManager(params.host, params.port, params.userName
                    , params.passWord, params.routingKey, params.queue,
                    params.asynchronousConfirmListener,
                    params.asynchronousConsumerListener, params.asynchronousExceptionCallback);
        }
    }

    /**
     * 发送数据
     *
     * @param msg 数据 字符串格式
     */
    public void submit(final String msg) throws NullPointerException {

        if (channel == null){

            asynchronousExceptionCallback.callback("还没连上服务器呢，检查一下网络，再试试吧～");

            return;
        }

        if (producer) {

            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (RqManager.class) {

                        long nextSetNo;
                        nextSetNo = channel.getNextPublishSeqNo();
                        longSparseArray.append(nextSetNo, msg);
                        try {

                            channel.basicPublish("DOAING", routingKey,
                                    MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                            Log.e(TAG, "发送：" + msg);

                        } catch (Exception e) {

                            asynchronousExceptionCallback.callback(ExceptionUtil.getMsg(e));
                        }
                    }
                }
            });
        }
    }

    /**
     * 释放连接资源
     */
    public void close() {

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    if (channel != null) {
                        channel.close();
                    }

                    if (connection != null) {
                        connection.close();
                    }
                    Log.e(TAG, "关闭rq连接了");
                } catch (Exception e) {
                    Log.e(TAG, ExceptionUtil.getMsg(e));
                }
            }
        });
    }

    private static class Params {
        Params(String host, int port, String userName, String passWord, String routingKey, String queue,
               AsynchronousConfirmListener asynchronousConfirmListener, AsynchronousConsumerListener asynchronousConsumerListener,
               AsynchronousExceptionCallback asynchronousExceptionCallback) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.passWord = passWord;
            this.routingKey = routingKey;
            this.queue = queue;
            this.asynchronousConfirmListener = asynchronousConfirmListener;
            this.asynchronousConsumerListener = asynchronousConsumerListener;
            this.asynchronousExceptionCallback = asynchronousExceptionCallback;
        }

        String host;
        int port;
        AsynchronousExceptionCallback asynchronousExceptionCallback;
        String userName;
        String passWord;
        String routingKey;
        String queue;
        AsynchronousConfirmListener asynchronousConfirmListener;
        AsynchronousConsumerListener asynchronousConsumerListener;
    }

    public interface AsynchronousConfirmListener {
        void callback(boolean isSuccess, String msg);

    }

    public interface AsynchronousConsumerListener {
        void consumer(String msg, long envelope);

    }

    public interface AsynchronousExceptionCallback {
        void callback(String exception);
    }

}
