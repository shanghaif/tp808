package cn.com.erayton.testGateWay;

import com.speedtalk.protocol.utils.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.com.erayton.usagreement.utils.LogUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * 1402, msg is null
 * 1403, return msg length is wrong ,smaller than 5
 * 1404, 网络错误, network error , throw Exception
 * */
public class GateWay {

    private static String host = "poc.rayton.com.cn";
    private int port = 29992;
//    private boolean isInit=false;
    private Socket socket ;
    private String command;

    public String getCommand() {
        return command;
    }

    public GateWay setCommand(String id ,String imei,String version) {
        command =id+ ":"+imei+":"+version;
        return this;
    }

//    public void setCommand(String id ,String imei,String version) {
//        command =id+ ":"+imei+":"+version;
//    }

    private Socket initSocket() throws IOException {
        socket = new Socket(host, port) ;
        return socket ;
    }

    private void send(String msg) throws IOException {
//        if (isInit){
//            return;
//        }
        String reqStr = new String(MessageUtils.encryptAndDecrypt(msg.getBytes())) ;
        reqStr +="\n" ;
        socket.getOutputStream().write(reqStr.getBytes());
    }


    public Observable<String> getGateWay(final IPCallback ipCallback){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    e.onNext("start socket:"+getCommand());
                    initSocket() ;
                    e.onNext("init socket");
                    send(getCommand());
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
    }

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
        String[] tempStr = null ;
        System.out.println(respStr);
        LogUtils.e(respStr);
        tempStr = respStr.split(":") ;
        if (callback != null){
            if (tempStr.length >= 5) {
                //  全部为 0 的情况， 账号错误

                callback.IPCallback(tempStr[0], Integer.parseInt(tempStr[1]),
                        Integer.parseInt(tempStr[2]), tempStr[3], tempStr[4]);
//                isInit=true;
            }else {
                callback.error(1403, "return msg length is wrong, length:"+tempStr.length);
//                isInit=true;
            }
        }else {
            LogUtils.d("callback == null");
        }
//        else {
//            callback.error(1404, "callback == null");
////            isInit=false;
//        }

    }

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
        void IPCallback(String s, int tPort, int uPort, String type, String result) ;
        void error(int errorCode, Object s) ;
    }

}
