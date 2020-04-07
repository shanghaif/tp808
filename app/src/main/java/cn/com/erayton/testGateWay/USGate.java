package cn.com.erayton.testGateWay;

import com.speedtalk.protocol.utils.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cn.com.erayton.usagreement.utils.LogUtils;
import cn.com.erayton.usagreement.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * 1402, msg is null
 * 1403, is init
 * 1404, 网络错误, network error , throw Exception
 * 1405, 账号错误, ip is zero , ip length is zero
 * */

public class USGate
{
    private static final String TAG = "USGate" ;
    private boolean isInit=false;
    private String command;
//    private IPCallback callback;
    private Socket socket;
    private static final String host = "poc.rayton.com.cn" ;
    private static final int port = 29992 ;
    public USGate() {
    }

    public String getCommand() {
        Utils.print("getCommand");
        return command;
    }

//    public USGate setCommand(String id ,String imei,String version) {
//        command =id+ ":"+imei+":"+version;
//        command ="B:"+msid;
//        return this;
//    }
    public USGate setCommand(String msid) {
        command ="B:"+msid;
        return this;
    }

    private Socket initSocket() throws IOException {
        socket = new Socket(host, port) ;
        return socket ;
    }

    private void send(String msg, IPCallback callback) throws IOException {
        if (isInit){
            callback.error(1403, "is init"+isInit);
            return;
        }
        String reqStr = new String(MessageUtils.encryptAndDecrypt(msg.getBytes())) ;
        reqStr +="\n" ;
        socket.getOutputStream().write(reqStr.getBytes());
    }

//    private void send(String msg) throws IOException {
////        if (isInit){
////            callback.error(1403, "is init"+isInit);
////            return;
////        }
//        String reqStr = new String(MessageUtils.encryptAndDecrypt(msg.getBytes())) ;
//        reqStr +="\n" ;
//        socket.getOutputStream().write(reqStr.getBytes());
//    }


    public Observable<String> getGateWay(final IPCallback ipCallback){
        Utils.print("getGateWay");
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    Utils.print("subscribe");
                    e.onNext("start socket:"+getCommand());
                    initSocket() ;
                    e.onNext("init socket");
                    send(getCommand(), ipCallback);
                    e.onNext("send msg:"+getCommand());
                    receive(ipCallback);
                    e.onNext("receive msg");
                    close();
                    e.onNext("close socket");
                    e.onComplete();
                }catch (IOException ex){
                    //  Unable to resolve host "poc.rayton.com.cn": No address associated with hostname
                    ipCallback.error(1404, ex.getMessage());
                    ipCallback.error(1404, "network error");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) ;
//                .observeOn(Schedulers.single()) ;
    }
//    public Observable<IPCallback> getGateWay(){
//        Utils.print("getGateWay");
//        return Observable.create(new ObservableOnSubscribe<IPCallback>() {
//            @Override
//            public void subscribe(ObservableEmitter<IPCallback> ipCallback) throws Exception {
//                try {
//                    Utils.print("subscribe");
////                    e.onNext("start socket:"+getCommand());
//                    initSocket() ;
////                    e.onNext("init socket");
//                    send(getCommand());
////                    e.onNext("send msg:"+getCommand());
////                    receive(ipCallback);
//                    ipCallback.onNext(receive());
////                    e.onNext("receive msg");
//                    close();
////                    e.onNext("close socket");
////                    e.onComplete();
//                }catch (IOException ex){
//                    //  Unable to resolve host "poc.rayton.com.cn": No address associated with hostname
////                    ipCallback.error(1404, ex.getMessage());
////                    ipCallback.error(1404, "network error");
//                }
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()) ;
////                .observeOn(Schedulers.single()) ;
//    }

    private void receive(IPCallback callback) throws IOException {
        byte[] resp = new byte[256] ;
        int read = socket.getInputStream().read(resp) ;
        if (read<0){
            callback.error(1402, "msg is null, read:"+read);
            return ;
        }
        byte[] temp = new byte[read -1] ;
        System.arraycopy(resp, 0, temp, 0, temp.length);
        String respStr = new String(MessageUtils.encryptAndDecrypt(temp)) ;
        String[] temp1 = null ;
        System.out.println(respStr);
        LogUtils.e(respStr);
        temp1 = respStr.split(":") ;
        List<String[]> ipList = new ArrayList<>() ;
        if (callback != null){
            callback.IPCallback(respStr);
            if (!temp1[1].equals("0")) {                 //  成功判断
                for (int i = 1; i < temp1.length; i++) {
//                    temp1[i].split(":") ;                    //  [1,222.222.19.34,7808,7808]
                    ipList.add(temp1[i].split(":"));
                    LogUtils.d("ipList："+temp1[i]);
                }
                isInit = true;
                LogUtils.d("--------------------- 1 ---------------------");
                if (temp1[0] .equals("0")){     //  ip 数量为 0 时错误
                    callback.error(1405, "ip length is zero");
                }else {
                    LogUtils.d("--------------------- 4 ---------------------");
                    callback.IPCallback(temp1[0], ipList);
                }
                LogUtils.d("--------------------- 5 ---------------------");
            }else {
                isInit = false;
                callback.error(1405, "ip is zero");
            }
        }

    }

//    private IPCallback receive() throws IOException {
//        IPCallback callback ;
//        byte[] resp = new byte[256] ;
//        int read = socket.getInputStream().read(resp) ;
//        if (read<0){
//            callback.error(1402, "msg is null, read:"+read);
//        }
//        byte[] temp = new byte[read -1] ;
//        System.arraycopy(resp, 0, temp, 0, temp.length);
//        String respStr = new String(MessageUtils.encryptAndDecrypt(temp)) ;
//        String[] temp1 = null ;
//        System.out.println(respStr);
//        LogUtils.e(respStr);
//        temp1 = respStr.split(":") ;
//        List<String[]> ipList = new ArrayList<>() ;
//        if (callback != null){
//            callback.IPCallback(respStr);
//            if (!temp1[1].equals("0")) {                 //  成功判断
//                for (int i = 1; i < temp1.length; i++) {
////                    temp1[i].split(":") ;                    //  [1,222.222.19.34,7808,7808]
//                    ipList.add(temp1[i].split(":"));
//                    LogUtils.d("ipList："+temp1[i]);
//                }
//                isInit = true;
//                LogUtils.d("--------------------- 1 ---------------------");
//                if (temp1[0] .equals("0")){     //  ip 数量为 0 时错误
//                    callback.error(1405, "ip length is zero");
//                }else {
//                    LogUtils.d("--------------------- 4 ---------------------");
//                    callback.IPCallback(temp1[0], ipList);
//                }
//                LogUtils.d("--------------------- 5 ---------------------");
//            }else {
//                isInit = false;
//                callback.error(1405, "ip is zero");
//            }
//        }
//
//        return callback ;
//    }

    private void close(){
        if (socket != null){
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                InputStream inputStream = socket.getInputStream() ;
                OutputStream outputStream = socket.getOutputStream() ;

                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface IPCallback{
        /**
         *
         * @param ipCount ip 数量
//         * @param ipList    ip列表
         */
//        void IPCallback(int ipCount, List<String[]> ipList) ;
//        void IPCallback(int ipCount, String ipList) ;
//        void IPCallback(int ipCount) ;
        void IPCallback(String ipCount) ;
        void IPCallback(String ipCount, List<String[]> ipList) ;

        /**
         *
         * @param errorCode 错误代码
         * @param s 错误原因
         */
        void error(int errorCode, Object s) ;
    }
}