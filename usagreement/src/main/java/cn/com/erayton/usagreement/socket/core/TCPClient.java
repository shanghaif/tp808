package cn.com.erayton.usagreement.socket.core;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.data.Constants;

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
            Log.d("cjh", "--------------1---------------- "+socket.isConnected()+" ----------------------------") ;
            InetSocketAddress socketAddress = new InetSocketAddress(host, port) ;
            Log.d("cjh", "---------------2--------------- "+socket.isConnected()+" ----------------------------") ;
            socket.connect(socketAddress, Constants.TCPTHREAD_TIMEOUT);
            Log.d("cjh", "----------------3-------------- "+socket.isConnected()+" ----------------------------") ;
            socket.setKeepAlive(true);
            Log.d("cjh", "-----------------4------------- "+socket.isConnected()+" ----------------------------") ;
            socket.setSoTimeout(Constants.TCPTHREAD_SO_TIMEOUT);
            Log.d("cjh", "------------------5------------ "+socket.isConnected()+" ----------------------------") ;
            startReceiveThread() ;
            Log.d("cjh", "-------------------6----------- "+socket.isConnected()+" ----------------------------") ;
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

    private boolean isConnect(){
        if (socket == null) return false ;
        return socket.isConnected() ;
    }

    public boolean sendAsyn(final byte[] bytes){
        if (socket == null) return false ;
        if (!socket.isConnected()) return  false ;

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("cjh", "------------------------------ onTcpSend ----------------------------") ;
                listener.onTcpSend(bytes, send(bytes));
            }
        });

        return true ;
    }

    public boolean send(byte[] bytes){
        if( socket == null ){
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
            }
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
        receiveThread.setName("TCP RECEIVE THREAD");
        receiveThread.start();
    }

    @Override
    public void run() {
        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        while (!getReceiveStop()) {
            byte[] receiveBytes = null;
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[socket.getReceiveBufferSize()];
//                byte[] buffer = new byte[256];
                int count = inputStream.read(buffer);
                Log.i("cjh", "count:"+count) ;
                if (count > 0) {
                    receiveBytes = new byte[count];
//                    receiveBytes = new byte[256];
                    System.arraycopy(buffer, 0, receiveBytes, 0, count);
                }
            }
            catch (SocketTimeoutException e) {
                e.printStackTrace();
//                continue;     //  跳过此异常
            }
            catch (SocketException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

//            if(receiveBytes == null || receiveBytes.length <= 0) {
            if(receiveBytes == null) {
                if(listener != null) {
                    listener.onTcpDisConnect();
                }
                break;
            }

            if(listener != null) {
                listener.onTcpReceive(receiveBytes);
            }
        }
    }
}
