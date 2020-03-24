package cn.com.erayton.testGateWay;

import android.util.Log;

import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.utils.LogUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


public class RxSocket {

    static RxSocket rxSocket;
    OutputStream outputStream = null;
    Socket socket = null;
    InputStream inputStream = null;
    /** 读取数据超时时间 */
    final int READ_TIMEOUT = 15 * 1000;
    /** 连接超时时间 */
    final int CONNECT_TIMEOUT = 5 * 1000;
    final String IP = "218.29.74.138";
    final int PORT = 11274;
    final String TAG = "RxSocket-->",SUCCEED="初始化成功",TIMEOUT="连接超时",SEND_ERROR="发送数据异常";
    /** 网络返回的监听 */
    SocketListener observer;
    public boolean isCancle = false;

    private RxSocket() {
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        initSocket(IP, PORT) ;
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, SUCCEED);
            }
        });
//                .doOnNext(s -> initSocket(IP, PORT))
//                .subscribe(s -> Log.d(TAG, SUCCEED));
    }

    public static RxSocket getInstance() {
        if (rxSocket == null) {
            rxSocket = new RxSocket();
        }
        return rxSocket;
    }


    /**
     * 初始化蓝牙通信，需要放在子线程
     * @param ip {@link RxSocket#IP}  ip地址
     * @param port {@link RxSocket#PORT} 端口号
     */
    private void initSocket(String ip,int port) throws Exception{
        try {
            socket = new Socket();
            socket.setSoTimeout(READ_TIMEOUT);
            Log.d(TAG, ip+":"+port);
            socket.connect(new InetSocketAddress(InetAddress.getByName(ip),port),CONNECT_TIMEOUT);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            Log.d(TAG, TIMEOUT);
            e.printStackTrace();
//            throw new Exception(TIMEOUT);
        }
    }

    /**
     * 发送数据
     * @return 接口返回的数据
     */
    private String sendData(String data) throws Exception{
        StringBuilder result = new StringBuilder("");
        try {
            outputStream.write(data.getBytes("UTF-8"));
            byte[] b = new byte[1024];
            int reads = inputStream.read(b);
            while (reads > 0) {
                byte[] bytes = Arrays.copyOfRange(b, 8, reads);
                String temp = new String(bytes);
                result.append(temp);
                reads = 0;
                b = new byte[1024];
                reads = inputStream.read(b);
            }
            Log.d(TAG, result.toString());
            return result.toString();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            throw new Exception(SEND_ERROR);
        }
    }

    /**
     * 取消所有的请求
     * @param isCancle true:取消访问  false:允许访问
     */
    public void cancleAll(boolean isCancle) {
        this.isCancle = isCancle;
        observer.cancleListen();
    }

    /**
     * socket 发送数据，并返回数据
     * @param baseRequestBean  可以一次发送多个请求
     *                         （后期可以添加重试机制）
     */
    public void request(BaseRequestBean ...baseRequestBean) {
        if (observer!=null)
            Observable.fromArray(baseRequestBean)
                    .subscribeOn(Schedulers.io())
            .filter(new Predicate<BaseRequestBean>() {
                @Override
                public boolean test(BaseRequestBean baseRequestBean) throws Exception {
                    return isCancle;
                }
            })
            .map(new Function<BaseRequestBean, Object>() {
                @Override
                public Object apply(BaseRequestBean baseRequestBean) throws Exception {
                        String result = sendData(baseRequestBean.get());
                        if (result.length()>0 && isCancle)
                            baseRequestBean.parseData(result);
                        return baseRequestBean;
//                    return null;
                }
            }).filter(new Predicate<Object>() {
                @Override
                public boolean test(Object o) throws Exception {
                    return isCancle;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe();
//                    .filter(baseRequestBean1 -> isCancle)
//                    .map(baseRequestBean1 -> {
//                        String result = sendData(baseRequestBean1.get());
//                        if (result.length()>0 && isCancle)
//                            baseRequestBean1.parseData(result);
//                        return baseRequestBean1;
//                    })
//                    .filter(baseRequestBean1 -> isCancle)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(observer);
    }

    /**
     * 设置网络返回的监听
     * @param listener
     */
    public void setResultListener(SocketListener listener) {
        this.observer = listener;
    }



//
//    /*  常量
//     * */
//    private String TAG = "RxSocket";
//    private boolean OpenLog = false;
//    private long WRITE_TIME_OUT = 3000;
//    private long CONNECT_TIME_OUT = 3000;
//
//    /*  单例
//     * */
//    private Subject<Object,byte[]> readSubject;
//    private Subject<Object,SocketStatus> connectStatus;
//    private static volatile RxSocket defaultInstance;
//    private RxSocket() {
//        readSubject = new SerializedSubject(PublishSubject.create());
//        connectStatus = new SerializedSubject(PublishSubject.create());
//    }
//    public static RxSocket getInstance() {
//        RxSocket rxSocket = defaultInstance;
//        if (defaultInstance == null) {
//            synchronized (RxSocket.class) {
//                rxSocket = defaultInstance;
//                if (defaultInstance == null) {
//                    rxSocket = new RxSocket();
//                    defaultInstance = rxSocket;
//                }
//            }
//        }
//        return rxSocket;
//    }
//
//    /*  变量
//     * */
//    private SocketStatus socketStatus = SocketStatus.DIS_CONNECT;
//    private Selector selector = null;
//    private SocketChannel socketChannel = null;
//    private SelectionKey selectionKey = null;
//    private ReadThread readThread = null;
//    private boolean isReadThreadAlive = true;
//    private SocketReconnectCallback socketReconnectCallback = null;
//
//    /*  方法
//     * */
//    /**
//     * 监听Socket的状态
//     * @return Rx SocketStatus 状态
//     */
//    public Observable<SocketStatus> socketStatusListener () {
//        return connectStatus;
//    }
//
//    /**
//     * 建立Socket连接，只是尝试建立一次
//     * @param ip    IP or 域名
//     * @param port  端口
//     * @return  Rx true or false
//     */
//    public Observable<Boolean> connectRx(final String ip, final int port) {
//        return Observable
//                .create(new ObservableOnSubscribe<Boolean>() {
//                    @Override
//                    public void subscribe(final ObservableEmitter<Boolean> subscriber) throws Exception {
//
//
//                        LogUtils.i("connectRx:"+"status:"+socketStatus.name());
//
//                        //正在连接
//                        if (socketStatus == SocketStatus.CONNECTING) {
//                            subscriber.onNext(false);
//                            subscriber.onComplete();
//                            return;
//                        }
//
//                        //未连接 | 已经连接，关闭Socket
//                        socketStatus = SocketStatus.DIS_CONNECT;
//                        isReadThreadAlive = false;
//                        readThread = null;
//                        if (selector!=null)
//                            try {
//                                selector.close();
//                            } catch (Exception e) {
//                                LogUtils.i("selector.close");
//                            }
//                        if (selectionKey!=null)
//                            try {
//                                selectionKey.cancel();
//                            } catch (Exception e) {
//                                LogUtils.i("selectionKey.cancel");
//                            }
//                        if (socketChannel!=null)
//                            try {
//                                socketChannel.close();
//                            } catch (Exception e) {
//                                LogUtils.i("socketChannel.close");
//                            }
//
//                        //重启Socket
//                        isReadThreadAlive = true;
//                        readThread = new ReadThread(ip,port);
//                        readThread.start();
//                        socketReconnectCallback = new SocketReconnectCallback() {
//                            @Override
//                            public void onSuccess() {
//                                LogUtils.i("connectRx:"+"CONNECTED");
//                                socketStatus = SocketStatus.CONNECTED;
//                                subscriber.onNext(true);
//                                subscriber.onComplete();
//                            }
//
//                            @Override
//                            public void onFail(String msg) {
//                                LogUtils.i("connectRx:"+msg);
//                                subscriber.onNext(false);
//                                subscriber.onComplete();
//                            }
//                        };
//                    }
//                })
//                .subscribeOn(Schedulers.newThread())
//                .map(new Function<Boolean, Boolean>() {
//                    @Override
//                    public Boolean apply(Boolean aBoolean) throws Exception {
//                        socketReconnectCallback = null;
//                        return aBoolean;
//                    }
//                })
//                .timeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS, Observable.just(false));
//    }
//
//    /**
//     * 断开当前的Socket
//     *  还能再继续连接
//     * @return Rx true or false
//     */
//    public Observable<Boolean> disConnect() {
//        return Observable.create(new ObservableOnSubscribe<Boolean>() {
//            @Override
//            public void subscribe(ObservableEmitter<Boolean> subscriber) throws Exception {
//                try {
//                    if (socketStatus == SocketStatus.DIS_CONNECT) {
//                        subscriber.onNext(true);
//                        subscriber.onComplete();
//                    }
//                    else {
//                        socketStatus = SocketStatus.DIS_CONNECT;
//                        isReadThreadAlive = false;
//                        readThread = null;
//                        if (selector!=null)
//                            try {
//                                selector.close();
//                            } catch (Exception e) {
//                                LogUtils.i("selector.close");
//                            }
//                        if (selectionKey!=null)
//                            try {
//                                selectionKey.cancel();
//                            } catch (Exception e) {
//                                LogUtils.i("selectionKey.cancel");
//                            }
//                        if (socketChannel!=null)
//                            try {
//                                socketChannel.close();
//                            } catch (Exception e) {
//                                LogUtils.i("socketChannel.close");
//                            }
//                        subscriber.onNext(true);
//                        subscriber.onComplete();
//                    }
//                } catch (Exception e) {
//                    subscriber.onNext(false);
//                    subscriber.onComplete();
//                }
//            }
//        });
//    }
//
//    /**
//     * 读取Socket的消息
//     * @return  Rx error 或者 有数据
//     */
//    public Observable<byte[]> read() {
//        return readSubject;
//    }
//
//    /**
//     * 向Socket写消息
//     * @param buffer    数据包
//     * @return  Rx true or false
//     */
//    public Observable<Boolean> write(final ByteBuffer buffer) {
//        return Observable
//                .create(new ObservableOnSubscribe<Boolean>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<Boolean> subscriber) throws Exception {
//                        if (socketStatus != SocketStatus.CONNECTED) {
//                            LogUtils.i( "write." + "SocketStatus.DISCONNECTED");
//                            subscriber.onNext(false);
//                            subscriber.onComplete();
//                        }
//                        else {
//                            if (socketChannel!=null && socketChannel.isConnected()) {
//                                try {
//                                    int result = socketChannel.write(buffer);
//                                    if (result<0) {
//                                        LogUtils.i( "write." + "发送出错");
//                                        subscriber.onNext(false);
//                                        subscriber.onComplete();
//                                    }
//                                    else {
//                                        LogUtils.i( "write." + "success!");
//                                        subscriber.onNext(true);
//                                        subscriber.onComplete();
//                                    }
//                                } catch (Exception e) {
//                                    LogUtils.i("write."+e.getMessage());
//                                    subscriber.onNext(false);
//                                    subscriber.onComplete();
//                                }
//                            }
//                            else {
//                                LogUtils.i("write."+"close");
//                                subscriber.onNext(false);
//                                subscriber.onComplete();
//                            }
//                        }
//                    }
//                })
//                .subscribeOn(Schedulers.newThread())
//                .timeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS, Observable.just(false));
//    }
//
//    /**
//     * 获取Socket的链接状态
//     * @return  状态
//     */
//    public SocketStatus getSocketStatus() {
//        return socketStatus;
//    }
//
//    /*  类 && 枚举 && 接口
//     * */
//    private class ReadThread extends Thread {
//        private String ip;
//        private int port;
//        ReadThread(String ip, int port) {
//            this.ip = ip;
//            this.port = port;
//        }
//        @Override
//        public void run() {
//            LogUtils.i("ReadThread:"+"start");
//            while (isReadThreadAlive) {
//                //连接
//                if (socketStatus == SocketStatus.DIS_CONNECT) {
//                    try {
//                        if (selectionKey != null) selectionKey.cancel();
//                        socketChannel = SocketChannel.open();
//                        socketChannel.configureBlocking(false);
//                        selector = Selector.open();
//                        socketChannel.connect(new InetSocketAddress(ip, port));
//                        selectionKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);
//                        socketStatus = SocketStatus.CONNECTING;
//                        connectStatus.onNext(SocketStatus.CONNECTING);
//                    } catch (Exception e) {
//                        isReadThreadAlive = false;
//                        socketStatus = SocketStatus.DIS_CONNECT;
//                        connectStatus.onNext(SocketStatus.DIS_CONNECT);
//                        LogUtils.e( "ReadThread:init:" + e.getMessage());
//                        if (socketReconnectCallback!=null)
//                            socketReconnectCallback.onFail("SocketConnectFail1");
//                    }
//                }
//                //读取
//                else if (socketStatus == SocketStatus.CONNECTING || socketStatus  == SocketStatus.CONNECTED) {
//                    try {
//                        selector.select();
//                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
//                        while (it.hasNext()) {
//                            SelectionKey key = it.next();
//                            if (key.isConnectable()) {
//                                if (socketChannel.isConnectionPending()) {
//                                    try {
//                                        socketChannel.finishConnect();
//                                        socketStatus = SocketStatus.CONNECTED;
//                                        connectStatus.onNext(SocketStatus.CONNECTED);
//                                        socketChannel.configureBlocking(false);
//                                        socketChannel.register(selector, SelectionKey.OP_READ);
//                                        if (socketReconnectCallback!=null)
//                                            socketReconnectCallback.onSuccess();
//                                    } catch (Exception e) {
//                                        isReadThreadAlive = false;
//                                        socketStatus = SocketStatus.DIS_CONNECT;
//                                        connectStatus.onNext(SocketStatus.DIS_CONNECT);
//                                        LogUtils.e( "ReadThread:finish:" + e.getMessage());
//                                        if (socketReconnectCallback!=null)
//                                            socketReconnectCallback.onFail("SocketConnectFail2");
//                                    }
//                                }
//                            } else if (key.isReadable()) {
//                                ByteBuffer buf = ByteBuffer.allocate(10000);
//                                int length = socketChannel.read(buf);
//                                if (length <= 0) {
//                                    LogUtils.e( "服务器主动断开链接！");
//                                    isReadThreadAlive = false;
//                                    socketStatus = SocketStatus.DIS_CONNECT;
//                                    connectStatus.onNext(SocketStatus.DIS_CONNECT);
//                                    if (socketReconnectCallback!=null)
//                                        socketReconnectCallback.onFail("SocketConnectFail3");
//                                } else {
//                                    LogUtils.i( "readSubject:msg！"+ "length:" + length);
//                                    byte[] bytes = new byte[length];
//                                    for (int i = 0; i < length; i++) {
//                                        bytes[i] = buf.get(i);
//                                    }
//                                    readSubject.onNext(bytes);
//                                }
//                            }
//                        }
//                        it.remove();
//                    } catch (Exception e) {
//                        isReadThreadAlive = false;
//                        socketStatus = SocketStatus.DIS_CONNECT;
//                        connectStatus.onNext(SocketStatus.DIS_CONNECT);
//                        LogUtils.e( "ReadThread:read:" + e.getMessage());
//                        if (socketReconnectCallback!=null)
//                            socketReconnectCallback.onFail("SocketConnectFail4");
//                    }
//                }
//            }
//        }
//    }
//
//    public enum SocketStatus {
//        DIS_CONNECT,
//        CONNECTING,
//        CONNECTED,
//    }
//
//    private interface SocketReconnectCallback {
//        void onSuccess();
//        void onFail(String msg);
//    }

}
