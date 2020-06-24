package cn.com.erayton.testGateWay;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.library.bean.FileMsg;
import com.library.util.FTPUtils;
import com.library.util.FileUtils;

import java.io.File;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.usagreement.utils.LogUtils;
import cn.com.erayton.usagreement.utils.TimeUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button button ,button2 ,button3 ,button4 , button5;
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
        button5 = findViewById(R.id.testBtn5) ;
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.testBtn:
                FileUtils.getVideoFileName(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Media/Video/") ;
                space();
//                FileUtils.getVideoFileName(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Media/Video/") ;
//                FileUtils.getVideoFileName(Environment.getExternalStoragePublicDirectory().getAbsolutePath() + "/Media/Video/") ;
                break;
            case R.id.testBtn2:
                FileUtils.getVideoMsg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString()) ;
                break;
            case R.id.testBtn3:
                FileUtils.getFileMsg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString()) ;
                break;
            case R.id.testBtn5:
                fileUpload();
                break;
            default:
                FileUtils.getNativeVideo(getContentResolver(), 0);
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
//                //只有1个网络请求
//                final Login login = new Login();
//                RxSocket.getInstance().request(login);
//
////多个网络请求
////        RxSocket.getInstance().request(login,login1,login2,login3);
////        RxSocket.getInstance().request(login);
//
////注册请求的回调
//                RxSocket.getInstance().setResultListener(new SocketListener(){
//                    @Override
//                    public void onSuccess(BaseRequestBean bean){
//
//                        LogUtils.d("BaseRequestBean");
//                        if(bean.method.equals(login.method)){
//                            LogUtils.d("bean.method："+bean.method);
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
//
//    public String getPath(){
//        try {
//            Class<?> mClass = Class.forName("android.os.Environment");
//            Method method = mClass.getMethod("getInternalStoragePath", null);  //第二个参数：表示调用方法的参数，当前方法不需要参数，因此null.
//            if(!method.isAccessible()){            //检测方法是否可访问.
//                method.setAccessible(true);
//            }
//            method.invoke(null, null) ;
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

    public void space(){
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(sdcardDir.getPath()); //sdcardDir.getPath())值为/mnt/sdcard，想取外置sd卡大小的话，直接代入/mnt/sdcard2
        long blockSize = sf.getBlockSizeLong(); //总大小
        long blockCount = sf.getBlockCountLong();
        long availCount = sf.getAvailableBlocksLong(); //有效大小
        LogUtils.d("blockCount:"+blockCount+",blockSize:"+blockSize+",availCount:"+availCount);
    }


    //定义解析的bean
//    class LoginRes {
//
//        String name;
//    }
//
//    //定义请求的bean，可以写成一个类 把泛型写上
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
//            //解析规则,并返回对应的bean
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
//                .throttleFirst(1, TimeUnit.SECONDS)//每1秒中只处理第一个元素
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        LogUtils.i(String.valueOf(aLong)) ;
//                    }
//                });
//    }

    String uri = null ;
    private void fileUpload(){
        //  文件上传，通过开始时间和结束时间，找到文件路径
        final String startTime ="200622144041" ;
//        final String startTime ="200622101728" ;
        LogUtils.d("cjh", "to :"+ TimeUtils.date2TimeStamp(startTime, "yyMMddHHmmss"));
        FTPUtils.getInstance().initFtpClient("222.222.19.35", 21,
                "erayton", "erayton.01");
        for (FileMsg v:FileUtils.getNativeVideo(getContentResolver(), 0, TimeUtils.date2TimeStamp(startTime, "yyMMddHHmmss"))){
            LogUtils.d("cjh", "getStartTime():"+v);
        }

//        FTPUtils.getInstance().initFtpClient("192.168.43.25", 2121,
//                "ftp", "ftp");
//        final String uri =DbTools.queryVideoRecord(Long.parseLong(msg.getStartTime())).getName() ;    //  文件路径
//        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0)){
//            LogUtils.d("cjh", "getStartTime():"+v.getStartTime());
//            if (v.getStartTime().equals(startTime)){
//                LogUtils.d("cjh", "v.getStartTime():"+v.getFilePath());
//                uri = v.getFilePath()  ;
//                break;
//            }
//        }
//        LogUtils.d("FTP", "uri:"+uri);
//        if (uri == null){
//            return;
//        }
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                        FTPUtils.getInstance().connectFtp() ;
//
//                FTPUtils.getInstance().uploadFile("/"+editText.getText().toString(),
//                        startTime + ".mp4", uri, new FTPUtils.FTPListener() {
//                            @Override
//                            public void Success() {
//                                SocketClientSender.sendUploadStatus(10, 0, false, false) ;
//                            }
//
//                            @Override
//                            public void Status(int code, String msg) {
//
//                            }
//
//                            @Override
//                            public void Failer(String errorMsg) {
//                                SocketClientSender.sendUploadStatus(10, 1, false, false) ;
//                            }
//                        });
//            }
//        }).start();

    }

}
