package cn.com.erayton.testGateWay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.speedtalk.protocol.tscobjs.Login;
import com.speedtalk.protocol.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.usagreement.utils.LogUtils;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button button ,button2 ,button3 ,button4 ;
    EditText editText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editText = findViewById(R.id.test_path) ;
        button = findViewById(R.id.testBtn) ;
        button2 = findViewById(R.id.testBtn2) ;
        button3 = findViewById(R.id.testBtn3) ;
        button4 = findViewById(R.id.testBtn4) ;
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.testBtn:
                FileUtils.getVideoFileName(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/") ;
                break;
            case R.id.testBtn2:
                FileUtils.getVideoMsg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString()) ;
                break;
            case R.id.testBtn3:
                FileUtils.getFileMsg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString()) ;
                break;
                default:
                    FileUtils.getNativeVideo(getContentResolver());
                    break;
        }

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                FileUtils.getFileLastModifiedTime(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString()));
////                FileUtils.getFileMsg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString());
//                FileUtils.getVideoFileName(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies");
////                FileUtils.getNativeVideo(getContentResolver());
//            }
//        });


//        LogUtils.d("onClick---------------------------");
////        RxView.clicks(v).throttleFirst(2, TimeUnit.SECONDS)
////                .doOnNext(
////                        new Consumer<Object>() {
////                    @Override
////                    public void accept(Object o) throws Exception {
////                        LogUtils.d("accept -----------"+o);
////
////                    }
////                }
////                )
////                .timeInterval(TimeUnit.SECONDS)
////        .subscribe(new Observer<Object>() {
////            @Override
////            public void onSubscribe(Disposable d) {
////                LogUtils.d("onSubscribe -----------");
////            }
////
////            @Override
////            public void onNext(Object o) {
////                LogUtils.d("onNext -----------"+o);
////            }
////
////            @Override
////            public void onError(Throwable e) {
////
////                LogUtils.d("onError -----------"+e);
////            }
////
////            @Override
////            public void onComplete() {
////                LogUtils.d("onComplete -----------");
////            }
////        })
//////                .subscribe(new Consumer<Object>() {
//////                    @Override
//////                    public void accept(Object o) throws Exception {
//////                        LogUtils.d("click -----------"+o);
//////                    }
//////
//////
//////                })
////                ;
//        switch (v.getId()){
//            case R.id.testBtn:
//
//                //??????1???????????????
//                final Login login = new Login();
//                RxSocket.getInstance().request(login);
//
////??????????????????
////        RxSocket.getInstance().request(login,login1,login2,login3);
////        RxSocket.getInstance().request(login);
//
////?????????????????????
//                RxSocket.getInstance().setResultListener(new SocketListener(){
//                    @Override
//                    public void onSuccess(BaseRequestBean bean){
//
//                        LogUtils.d("BaseRequestBean");
//                        if(bean.method.equals(login.method)){
//                            LogUtils.d("bean.method???"+bean.method);
//                        }
////                else if(bean.method.eqals(login3.method)){
////
////                }
//                    }
//                    @Override
//                    public void onExecption(Throwable throwable){
//                        LogUtils.d("onExecption:"+throwable.getMessage());
//                    }
//                });
//                break;
//            case R.id.testBtn2:
//                gateWay() ;
//                break;
//                default:
//                    break;
//        }

    }


    //???????????????bean
//    class LoginRes {
//
//        String name;
//    }
//
//    //???????????????bean???????????????????????? ???????????????
//    class Login extends BaseRequestBean<LoginRes>{
//        public Login(){
//            super();
//        }
//
//        @Override
//        String getRequsetData() {
//            return "BBFFAAA";
//        }
//
//        @Override
//        LoginRes parseData(String data) {
//            //????????????,??????????????????bean
//            return new LoginRes();
//        }
//
//        @Override
//        String methods() {
//            return "login";
//        }
//    }
//
//    private void gateWay() {
//        LogUtils.d("gateWay---------------------------");
//        USGate gateWay = new USGate();
////        GateWay gateWay = new GateWay() ;
////        getGateWay("123:imei:version", new IPCallback() {
//
////        Disposable disposable =gateWay.setCommand("23803560285", "123456789012", "1.0")
//        Disposable disposable =gateWay.setCommand("23803560285")
//        .getGateWay(
//                new USGate.IPCallback() {
//                    @Override
//                    public void IPCallback(String ipCount) {
//                        LogUtils.d("IPCallback,ipCount:"+ipCount);
//                    }
//                    @Override
////                        public void IPCallback(int  ipCount, String s) {
////                                LogUtils.d("IPCallback,ipCount:"+ipCount+",msg:"+s);
////                        public void IPCallback(int  ipCount) {
////                            LogUtils.d("IPCallback,ipCount:"+ipCount);
//                        public void IPCallback(String ipCount, List<String[]> ipList) {
//                            LogUtils.d("IPCallback,ipCount:"+ipCount);
//                            for (String[] s: ipList){
//                                LogUtils.d("ipList"+ Arrays.toString(s));
//                            }
//                        }
//
//                        @Override
//                        public void error(int errorCode, Object s) {
//                            LogUtils.e("error"+errorCode+s);
//                        }
//                    }
////                new GateWay.IPCallback() {
////            @Override
////            public void IPCallback(String s, int tPort, int uPort, String type, String result) {
////                LogUtils.d(s+","+tPort+","+uPort+","+type+","+result);
////            }
////
////            @Override
////            public void error(int errorCode, Object s) {
////                LogUtils.d("error ---------------------------------"+errorCode+","+s);
////
////            }
////        }
//        ).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                LogUtils.d(s);
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                LogUtils.d(throwable.getMessage());
//            }
//        });
////        if(!disposable.isDisposed()){
////            disposable.dispose();
////        }
//    }
//
//    private void testThrottleFirst(){
//        Flowable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
//                .throttleFirst(1, TimeUnit.SECONDS)//???1??????????????????????????????
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        LogUtils.i(String.valueOf(aLong)) ;
//                    }
//                });
//    }

}
