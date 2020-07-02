package com.library.live.stream.socket.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.LogUtils;


/**
 * 视频发送 TCP
 * */
public class TCPClient implements Runnable {
    public interface TCPClientListener {
        void onTcpConnect(int result);
        void onTcpDisConnect();
        void onTcpSend(byte[] data, boolean result);
        void onTcpReceive(byte[] receiveBytes);
    }

    public TCPClientListener listener = null ;

//    -----------------------------------------------------------------

    private Socket socket = null ;

    private Thread receiveThread = null ;

    private Object sendLock = new Object() ;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            1, 1, 1,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardPolicy()) ;

//    -----------------------------------------------------------------

    private boolean receiveStop = true ;
    private Object receiveStopLock = new Object() ;

    public boolean getReceiveStop() {synchronized (receiveStopLock){
        return receiveStop;
    }}

    public void setReceiveStop(boolean receiveStop) {synchronized (receiveStopLock){
        this.receiveStop = receiveStop;
    }}

//    -----------------------------------------------------------------

    public void openAsyn(final String host, final int port){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connect(host, port) ;
            }
        });
        thread.start();
    }

    public int connect(String host, int port){
        if (isConnect()){
            close() ;
        }

        if (host == null || host.isEmpty() || port <= 0){
            return 1 ;
        }

        int result = 0 ;


        try {
            socket = new Socket() ;
            InetSocketAddress socketAddress = new InetSocketAddress(host, port) ;
            socket.connect(socketAddress, Constants.TCPTHREAD_TIMEOUT);
            socket.setKeepAlive(true);
            socket.setSoTimeout(Constants.TCPTHREAD_SO_TIMEOUT);
            startReceiveThread() ;
        } catch (SocketException e) {
            e.printStackTrace();
            result = -1 ;
        } catch (IOException e) {
            e.printStackTrace();
            result = -2 ;
        }
        if (listener != null) listener.onTcpConnect(result);

        return result ;

    }

    public void close(){
        setReceiveStop(true);

        if (socket == null){
            return;
        }
        try {
            if (!socket.isInputShutdown()) {
                socket.shutdownInput();
            }

            if (!socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }

            if (!socket.isClosed()) {
                socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        socket = null;
    }

    public boolean isConnect(){
        if (socket == null) return false ;
        return socket.isConnected() ;
    }

    public boolean sendAsyn(final byte[] bytes){
        if (socket == null) return false ;
        if (!socket.isConnected()) return  false ;
        LogUtils.d("sendAsyn---------------:"+bytes.length);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                listener.onTcpSend(bytes, send(bytes));
            }
        });

        return true ;
    }

    public boolean send(byte[] bytes){
        if( socket == null ){
            LogUtils.d("socket == null");
            return false;
        }

        synchronized (sendLock) {
            try {
                OutputStream sokectWrite = socket.getOutputStream();
                sokectWrite.write(bytes);
                sokectWrite.flush();
                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
                LogUtils.d("IOException:"+e.getMessage()+"\t"+e.toString());
            }
            LogUtils.d("send  == null");
            return false;
        }
    }

    public void start(){
        startReceiveThread();
    }

    private void startReceiveThread(){
        if (receiveThread != null){
            setReceiveStop(true);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        receiveThread = new Thread(this) ;
        setReceiveStop(false);
        receiveThread.setName("video TCP RECEIVE THREAD");
        if (receiveThread != null &&!receiveThread.isAlive())
            receiveThread.start();
    }

    @Override
    public void run() {
//        try {
//            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
//        }
//        catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//        catch (SecurityException e) {
//            e.printStackTrace();
//        }
//
//        while (!getReceiveStop()) {
//            byte[] receiveBytes = null;
//            try {
//                InputStream inputStream = socket.getInputStream();
//                byte[] buffer = new byte[socket.getReceiveBufferSize()];
////                byte[] buffer = new byte[256];
//                int count = inputStream.read(buffer);
//                if (count > 0) {
//                    receiveBytes = new byte[count];
////                    receiveBytes = new byte[256];
//                    System.arraycopy(buffer, 0, receiveBytes, 0, count);
//                }
//            }
//            catch (SocketTimeoutException e) {
//                e.printStackTrace();
////                continue;     //  跳过此异常
//            }
//            catch (SocketException e) {
//                e.printStackTrace();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//
////            if(receiveBytes == null || receiveBytes.length <= 0) {
//
////            if(receiveBytes == null) {
////                if(listener != null) {
////                    listener.onTcpDisConnect();
////                }
////                break;
////            }
////
////            if(listener != null) {
////                listener.onTcpReceive(receiveBytes);
////            }
//        }
    }
}
