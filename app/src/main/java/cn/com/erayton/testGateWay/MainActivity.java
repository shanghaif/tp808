package cn.com.erayton.testGateWay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.speedtalk.protocol.utils.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.usagreement.utils.LogUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button button ,button2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button = findViewById(R.id.testBtn) ;
        button2 = findViewById(R.id.testBtn2) ;
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        LogUtils.d("onClick---------------------------");
        switch (v.getId()){
            case R.id.testBtn:

                //只有1个网络请求
                final Login login = new Login();
                RxSocket.getInstance().request(login);

//多个网络请求
//        RxSocket.getInstance().request(login,login1,login2,login3);
//        RxSocket.getInstance().request(login);

//注册请求的回调
                RxSocket.getInstance().setResultListener(new SocketListener(){
                    @Override
                    public void onSuccess(BaseRequestBean bean){
                        if(bean.method.equals(login.method)){
                            LogUtils.d(bean.method);
                        }
//                else if(bean.method.eqals(login3.method)){
//
//                }
                    }
                    @Override
                    public void onExecption(Throwable throwable){
                    }
                });
                break;
            case R.id.testBtn2:
                gateWay() ;
                break;
                default:
                    break;
        }

    }


    //定义解析的bean
    class LoginRes {

        String name;
    }

    //定义请求的bean，可以写成一个类 把泛型写上
    class Login extends BaseRequestBean<LoginRes>{
        public Login(){
            super();
        }

        @Override
        String getRequsetData() {
            return "BBFFAAA";
        }

        @Override
        LoginRes parseData(String data) {
            //解析规则,并返回对应的bean
            return new LoginRes();
        }

        @Override
        String methods() {
            return "login";
        }
    }

    private void gateWay(){


//        getGateWay("123:imei:version", new IPCallback() {
        getGateWay("23803560285:123456789012:1.0", new IPCallback() {
            @Override
            public void IPCallback(String s, int tPort, int uPort, String type, String result) {
                LogUtils.d(s+tPort+uPort+type+result);
            }

            @Override
            public void error(Object s) {
                LogUtils.d("error ---------------------------------"+s);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.d(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.d(throwable.getMessage());
            }
        });

    }

    private Observable<String> getGateWay(final String msg, final IPCallback ipCallback){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    e.onNext("----------------------"+msg);
                    initSocket() ;
                    e.onNext("initSocket");
                    send(msg);
                    e.onNext("send:"+msg);
                    receive(ipCallback);
                    e.onNext("receive");
                    close();
                    e.onNext("close");
                    e.onComplete();
                }catch (IOException ex){
                    //  Unable to resolve host "poc.rayton.com.cn": No address associated with hostname
                    ipCallback.error(ex.getMessage());
                    ipCallback.error("网络错误");
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()) ;
    }

    String host = "poc.rayton.com.cn";
    int port = 29992;
    Socket socket ;
    private Socket initSocket() throws IOException {
        socket = new Socket(host, port) ;
        return socket ;
    }

    private void send(String msg) throws IOException {
        String reqStr = new String(MessageUtils.encryptAndDecrypt(msg.getBytes())) ;
        reqStr +="\n" ;
        socket.getOutputStream().write(reqStr.getBytes());
    }

    private void receive(IPCallback callback) throws IOException {
        byte[] resp = new byte[256] ;
        int read = socket.getInputStream().read(resp) ;
        if (read<0){
            callback.error("msg is null, read:"+read);
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
                callback.IPCallback(tempStr[0], Integer.parseInt(tempStr[1]),
                        Integer.parseInt(tempStr[2]), tempStr[3], tempStr[4]);
            }else {
                callback.error("return msg length is wrong, length:"+tempStr.length);
            }
        }else {
            callback.error("callback == null");
        }

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
        void error(Object s) ;
    }

}
