package cn.com.erayton.usagreement.socket.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.data.Constants;

public class UDPClient implements Runnable{
    public interface UDPClientListener{
        void OnUdpSend(byte[] data, boolean result) ;
        void OnUdpReceive(byte[] receiveBytes) ;
    }

    public UDPClientListener listener = null ;

//    -----------------------------------------------------------------

    private DatagramSocket socket = null ;
    private Thread receiveThread = null ;

    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            1, 1, 1,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),
            new ThreadPoolExecutor.DiscardPolicy()) ;

//    -----------------------------------------------------------------

    private String host = "" ;
    private Object hostLock = new Object() ;
    public String getHost() {synchronized (hostLock){
        return host;
    }}
    public void setHost(String host) {synchronized (hostLock){
        this.host = host;
    }}

    private int port = 0;
    private Object portLock = new Object() ;
    public int getPort() {synchronized (portLock){
        return port;
    }}
    public void setPort(int port) {synchronized (portLock){
        this.port = port;
    }}

    private boolean receiveStop = true ;
    private Object receiveStopLock = new Object() ;
    public boolean getReceiveStop() {synchronized (receiveStopLock){
        return receiveStop;
    }}
    public void setReceiveStop(boolean receiveStop) {synchronized (receiveStopLock){
        this.receiveStop = receiveStop;
    }}

//    -----------------------------------------------------------------

    public boolean start(){
        if (getHost() == null || getHost().isEmpty() || getPort() <=0){
            return false ;
        }

        setReceiveStop(false);

        if (receiveThread == null){
            if (open() == false){
                return false ;
            }

            receiveThread = new Thread(this) ;
            receiveThread.setName("receiveThread");
            receiveThread.start();
        }
        return true ;
    }

    public void close(){
        setReceiveStop(true);
    }

    /*
    * 开启 socket 端口
    * */
    private boolean open(){
        try {
            socket = new DatagramSocket(getPort()) ;
        } catch (SocketException e) {
            e.printStackTrace();
            return false ;
        }
        return true ;
    }

    public boolean sendAsyn(final byte[] bytes){
        if (socket == null){
            try {
                socket = new DatagramSocket(getPort()) ;
            } catch (SocketException e) {
                e.printStackTrace();
                return false ;
            }
        }
        if (socket == null){
            return false ;
        }

        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                listener.OnUdpSend(bytes, send(bytes));
            }
        });

        return true ;
    }


    /**
    * udp send
    * @return boolean
    * */
    public boolean send(byte[] bytes){
        if (socket == null) return false ;

        boolean isSucc = false ;

        try {
            InetAddress inetAddress = InetAddress.getByName(getHost()) ;
            socket.send(new DatagramPacket(bytes, bytes.length, inetAddress, getPort()));
            isSucc = true ;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSucc ;
    }

    /**
     *  upd 信息接收
     * @return boolean
     * */
    private boolean receive(){
        byte[] receiveBytes = null ;

        try {
            int size = socket.getReceiveBufferSize() ;
            byte[] buf = new byte[size] ;
            DatagramPacket datagramPacket = new DatagramPacket(buf ,buf.length) ;
            socket.receive(datagramPacket);
            int count = datagramPacket.getLength() ;
            if (count > 0){
                receiveBytes = new byte[count] ;
                System.arraycopy(datagramPacket.getData(), datagramPacket.getOffset(), receiveBytes, 0 ,count);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return false ;
        } catch (IOException e) {
            e.printStackTrace();
            return false ;
        }

        if (listener == null || receiveBytes == null || receiveBytes.length <= 0)   return true ;

        listener.OnUdpReceive(receiveBytes);
        return true ;
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

        while (true) {
            if (getReceiveStop()) {
                continue;
            }

            if (receive() ) {
                continue;
            }

            if (socket != null) {
                if (socket.isConnected()) {
                    socket.disconnect();
                }

                if (!socket.isClosed()) {
                    socket.close();
                }
            }

            if (open()) {
                continue;
            }

            try {
                Thread.sleep(Constants.UDPTHREADT_SLEEPIME);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
