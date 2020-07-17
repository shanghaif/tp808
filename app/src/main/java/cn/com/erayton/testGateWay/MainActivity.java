package cn.com.erayton.testGateWay;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.library.bean.FileMsg;

import net.rayton.netstatelib.NetWorkMonitor;
import net.rayton.netstatelib.NetWorkMonitorManager;
import net.rayton.netstatelib.NetWorkState;

import java.io.File;
import java.util.Arrays;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.usagreement.utils.FTPUtils;
import cn.com.erayton.usagreement.utils.FileUtils;
import cn.com.erayton.usagreement.utils.LogUtils;
import cn.com.erayton.usagreement.utils.StorageUtils;
import cn.com.erayton.usagreement.utils.TimeUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button button ,button2 ,button3 ,button4 , button5, button6;
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
        button6 = findViewById(R.id.testBtn6) ;
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NetWorkMonitorManager.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetWorkMonitorManager.getInstance().unregister(this);
    }

    //不加注解默认监听所有的状态，方法名随意，只需要参数是一个NetWorkState即可
      @NetWorkMonitor(monitorFilter = {NetWorkState.GPRS, NetWorkState.NONE, NetWorkState.WIFI})//只接受网络状态变为GPRS类型的消息
    public void onNetWorkStateChange(NetWorkState netWorkState) {
        LogUtils.i("TAG", "onNetWorkStateChange >>> :" + netWorkState.name());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.testBtn:
//                MediaScannerConnection.scanFile(this,
////                        new String[]{"/storage/emulated", "/storage/74A2-A457"},
//                        StorageUtils.getTFCardList(),
//                        new String[]{"video/*", "audio/*"},
////                        new MediaScannerConnection.OnScanCompletedListener() {
////                    @Override
////                    public void onScanCompleted(String path, Uri uri) {
////                        LogUtils.w("path:"+path+",uri:"+uri);
////                    }
////                }
//                        null
//                );
                StorageUtils.reflashMedia(MainActivity.this) ;

//                FileUtils.getNativeVideo(getContentResolver(), 0) ;
//                FileUtils.getVideoFileName(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Media/Video/") ;
//                space();
//                FileUtils.getVideoFileName(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Media/Video/") ;
//                FileUtils.getVideoFileName(Environment.getExternalStoragePublicDirectory().getAbsolutePath() + "/Media/Video/") ;
                break;
            case R.id.testBtn2:
//                FileUtils.getVideoMsg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/"+editText.getText().toString()) ;
//                StorageUtils.getTFCardPath();
                LogUtils.e("testBtn2:"+ Arrays.toString(StorageUtils.getTFCardList())) ;

//                OtherUtil.executeCmd(editText.getText().toString(), false) ;
                break;
            case R.id.testBtn3:
                FileUtils.getVideoPath(getContentResolver(), 0, editText.getText().toString()) ;
                break;
            case R.id.testBtn5:
                fileUpload(true);
                break;
            case R.id.testBtn6:
                fileUpload(false);
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
//    private void fileUpload(boolean isChange){
//        //  文件上传，通过开始时间和结束时间，找到文件路径
//        final String startTime ="200622144041" ;
////        final String startTime ="200622101728" ;
//        LogUtils.d("FTP", "to :"+ TimeUtils.date2TimeStamp(startTime, "yyMMddHHmmss"));
//        if (isChange) {
//            FTPUtils.getInstance().initFtpClient("222.222.19.35", 21,
//                    "erayton", "erayton.01");
//        }else {
//            FTPUtils.getInstance().initFtpClient("60.13.227.76", 21,
//                    "31gpsftp1", "1q2w3e");
//        }
//
//        for (FileMsg v:FileUtils.getVideoPath(getContentResolver(), 0, TimeUtils.date2TimeStamp(startTime, "yyMMddHHmmss"))){
////        for (FileMsg v:FileUtils.getVideoPath(getContentResolver(), 0, TimeUtils.date2TimeStamp(startTime, "yyMMddHHmmss"))){
//            LogUtils.d("FTP", "getStartTime():"+v);
//            uri = v.getFilePath() ;
//            break;
//        }
//
////        FTPUtils.getInstance().initFtpClient("192.168.43.25", 2121,
////                "ftp", "ftp");
////        final String uri = DbTools.queryVideoRecord(Long.parseLong(msg.getStartTime())).getName() ;    //  文件路径
////        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0)){
////            LogUtils.d("cjh", "getStartTime():"+v.getStartTime());
////            if (v.getStartTime().equals(startTime)){
////                LogUtils.d("cjh", "v.getStartTime():"+v.getFilePath());
////                uri = v.getFilePath()  ;
////                break;
////            }
////        }
//        LogUtils.d("FTP", "uri:"+uri);
//        if (uri == null){
//            return;
//        }
////
////
////        new UploadTask().execute(uri,startTime + ".mp4");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                FTPUtils.getInstance().uploadFile("/"+editText.getText().toString(),
////                startTime + ".mp4", uri, new FTPUtils.FTPListener() {
////                            @Override
////                        public void Success() {
////                            SocketClientSender.sendUploadStatus(10, 0, true, false) ;
////                        }
////
////                        @Override
////                        public void Status(int code, String msg) {
////
////                        }
////
////                        @Override
////                        public void Failer(String errorMsg) {
////                            SocketClientSender.sendUploadStatus(10, 1, true, false) ;
////                        }
//                boolean succ = FTPUtils.getInstance().uploadFile("/"+editText.getText().toString(),
//                startTime + ".mp4", uri);
//                LogUtils.d("FTP", "succ():"+succ);
//            }
//        }) .start();
//
//    }







//
//    class UploadTask extends AsyncTask<String, Object, Integer> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Integer doInBackground(String... params) {
////        "60.13.227.76", 21, "31gpsftp1", "1q2w3e"
//            String ftp_url = "60.13.227.76";
//            String ftp_name = "31gpsftp1";
//            String ftp_pwd = "1q2w3e";
//
//            String filePath = params[0];
//            String fileName = params[1];
//
////            String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
////            String filePath = basePath + File.separator + "caiyun" + File.separator + fileName;
////            String remotePath = File.separator + fileName.substring(0, 2);
//            String remotePath = File.separator + filePath.substring(0, 2);
//            FTPClient ftpClient = new FTPClient();
//            FileInputStream fis;
//            int returnMessage = 0;
//            try {
//                ftpClient.connect(ftp_url, 21);
//                boolean loginResult = ftpClient.login(ftp_name, ftp_pwd);
//                int returnCode = ftpClient.getReplyCode();
//                if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
//                    ftpClient.makeDirectory(remotePath);
//                    // 设置上传目录
//                    ftpClient.changeWorkingDirectory(remotePath);
//                    ftpClient.setBufferSize(1024);
//                    ftpClient.setControlEncoding("UTF-8");
//                    ftpClient.enterLocalPassiveMode();
//                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//                    fis = new FileInputStream(filePath);
//
//                    //不计算进度条
//                    //ftpClient.storeFile(fileName, fis);
//
//                    //计算进度条
//                    int n = -1;
//                    long pContentLength = fis.available();
//                    long trans = 0;
//                    int bufferSize = ftpClient.getBufferSize();
//                    byte[] buffer = new byte[bufferSize];
//                    OutputStream outputstream = ftpClient.storeFileStream(new String(fileName.getBytes("utf-8"), "iso-8859-1"));
//                    while ((n = fis.read(buffer)) != -1) {
//                        outputstream.write(buffer, 0, n);
//                        trans += n;
//                        //trans已传输字节  pContentLength总字节
//                        publishProgress(trans, pContentLength);
//                    }
//                    fis.close();
//                    outputstream.flush();
//                    outputstream.close();
//
//                    returnMessage = 1;   //上传成功
//                } else {// 如果登录失败
//                    returnMessage = 0;
//                }
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException("FTP客户端出错！", e);
//            } finally {
//                try {
//                    ftpClient.disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    throw new RuntimeException("关闭FTP连接发生异常！", e);
//                }
//            }
//            return returnMessage;
//        }
//
//        @Override
//        protected void onPostExecute(Integer result) {
//            if (result == 1) {
//                //上传成功后调用
//                LogUtils.e("FTP", "success ---------------------");
//            }
//
//        }
//
//        @Override
//        protected void onProgressUpdate(Object... values) {
//            super.onProgressUpdate(values);
//            //获取进度
//            long trans = (long) values[0];
//            long pContentLength = (long) values[1];
//            int progress = (int) (trans * 100 / pContentLength);
//            LogUtils.e("FTP", "progress ---"+progress);
//        }
//    }

    public void fileUpload(boolean isChange){
        fileUpload(this, isChange).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                LogUtils.e("FTP", "-------------result:"+aBoolean);
//                USManager.getSingleton().SendUploadStatus(msg.getSerNum(), aBoolean) ;
//                USManager.getSingleton().SendUploadStatus(msg.getSerNum(), aBoolean) ;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.d("accept:"+throwable.getMessage());
            }
        }) ;
    }


    public Observable<Boolean> fileUpload(final Context context, boolean isChange){
        LogUtils.e("FTP", "fileUpload------");
        final String startTime ="200622144041" ;
        if (isChange) {
            FTPUtils.getInstance().initFtpClient("222.222.19.35", 21,
                    "erayton", "erayton.01");
        }else {
            FTPUtils.getInstance().initFtpClient("60.13.227.76", 21,
                    "31gpsftp1", "1q2w3e");
        }
//        FTPUtils.getInstance().initFtpClient(msg.getHost(), msg.getPort(),
//                msg.getUserName(), msg.getUserPass());
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {

                //  文件上传，通过开始时间和结束时间，找到文件路径
//                final String uri =DbTools.queryVideoRecord(Long.parseLong(msg.getStartTime())).getName() ;    //  文件路径
                for (FileMsg v: FileUtils.getVideoPath(context.getContentResolver(),
                        0, TimeUtils.date2TimeStamp(startTime,"yyMMddHHmmss"))) {
//                    msg.getResourceType(), TimeUtils.date2TimeStamp(msg.getStartTime(),"yyMMddHHmmss"))) {
                    uri = v.getFilePath()  ;
                    break;
                }
                if (TextUtils.isEmpty(uri)){
                    //  查找失败，返回失败，不继续下去
//                    USManager.getSingleton().SendUploadResp(serNum, false);
                    LogUtils.e("FTP", "SendUploadResp false");
                    e.onComplete();
                } else {
//                    USManager.getSingleton().SendUploadResp(serNum, true);

                    LogUtils.e("FTP", "SendUploadResp true");
                }

//                boolean result = FTPUtils.getInstance().uploadFile(msg.getUpLoadPath(),
//        msg.getStartTime() + ".mp4", uri);
//                e.onNext(result);
//                e.onComplete();
                FTPUtils.getInstance().uploadFile("/"+editText.getText().toString(),
                        startTime + ".mp4", uri, new FTPUtils.FTPListener() {
                            @Override
                            public void Success() {
                                e.onNext(true);
                            }

                            @Override
                            public void Status(int code, String msg) {
                                LogUtils.d("FTP", "Status"+code+","+msg);
                            }

                            @Override
                            public void Failer(String errorMsg) {
                                e.onNext(false);
                            }
                        });
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())  ;
    }
}
