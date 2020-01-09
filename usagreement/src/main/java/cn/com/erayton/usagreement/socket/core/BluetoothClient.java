package cn.com.erayton.usagreement.socket.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;

import com.speedtalk.protocol.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.utils.LogUtils;

public class BluetoothClient implements Runnable {
    public interface BlueToothClientListener{
        void onBluetoothConnect(int result) ;
        void onBluetoothDisConnect() ;
        void onBluetoothSend(byte[] data) ;
        void onBluetoothReceive(byte[] data) ;
    }


    private BlueToothClientListener listener = null ;
//    00001101-0000-1000-8000-00805F9B34FB  默认 UUID
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ;
    private BluetoothDevice bluetoothDevice ;
    private BluetoothSocket bluetoothSocket ;

//    -----------------------------------------------------------------
    private boolean receiveStop = true ;
    private Object receiveStopLock = new Object() ;

    public boolean getReceiveStop() {synchronized (receiveStopLock){
        return receiveStop;
    }}

    public void setReceiveStop(boolean receiveStop) {synchronized (receiveStopLock){
        this.receiveStop = receiveStop;
    }}

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            1, 1, 1,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardPolicy()) ;




//    -----------------------------------------------------------------





    public int connect(String mac, String uuid){
        int result = 0 ;
        if (isConnect()){
            close();
        }
        if (TextUtils.isEmpty(uuid)){
            uuid = "00001101-0000-1000-8000-00805F9B34FB" ;
        }

        if (bluetoothDevice == null)
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac) ;
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid)) ;
            bluetoothSocket.connect();
            startReceiveThread();
        } catch (IOException e) {
            e.printStackTrace();
            result = -2 ;
        }
        if (listener != null)   listener.onBluetoothConnect(result);
        return result ;
    }

    public void close(){
        setReceiveStop(true);
        try {
            if (bluetoothSocket != null){
                bluetoothSocket.close();
                bluetoothSocket = null ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnect(){
        if (bluetoothSocket == null )return false ;
        return bluetoothSocket.isConnected() ;
    }

    private Object sendLock = new Object() ;

    public boolean sendAsyn(final byte[] msg){
        if (bluetoothSocket == null)    return false ;
        if (!bluetoothSocket.isConnected()) return false ;

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                send(msg) ;
            }
        });
        return true ;
    }

    private Thread receiveThread = null ;
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
        receiveThread.setName("bluetooth receive thread");
        receiveThread.start();

    }

    public boolean send(byte[] msg){
        if (bluetoothSocket == null){
            return false ;
        }

        if (listener != null)   listener.onBluetoothSend(msg);

        synchronized (sendLock){
            try {
                OutputStream outputStream = bluetoothSocket.getOutputStream() ;
                outputStream.write(msg);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false ;
        }

    }








    @Override
    public void run() {
        while (!getReceiveStop()){
            byte[] receiveBytes = new byte[1024] ;
            try {
                InputStream inputStream = bluetoothSocket.getInputStream() ;

                inputStream.read(receiveBytes) ;
                LogUtils.d(Arrays.toString(receiveBytes));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (receiveBytes == null){
                if (listener != null)   listener.onBluetoothDisConnect();
            }
            if (listener != null)   listener.onBluetoothReceive(receiveBytes);
        }
    }

}
