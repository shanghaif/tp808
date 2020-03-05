package cn.com.erayton.usagreement.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.library.live.Publish;
import com.library.live.stream.UdpSend;
import java.io.File;
import java.util.concurrent.TimeUnit;
import cn.com.erayton.usagreement.VideoPushAIDL;
import cn.com.erayton.usagreement.data.Constants;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;


public class VideoPushService extends Service {
    private String TAG = VideoPushService.class.getName() ;
//    private String IP ;
//    private int Port ;
    private Publish publish ;
    private String phone ;
    private int VIDEO_NOTIFICATION_CODE = 0x120 ;
    private String DEFAULT_NAME = "易对讲" ;
    private String DEFAULT_CONTENT = "易对讲" ;

//    private int VIDEO_NOTIFICATION_LEVEL = NotificationManager.IMPORTANCE_DEFAULT ;
    private NotificationManager notificationManager ;


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        initNotification() ;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //  连接 1078 服务器
        //  设置参数，并推流

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destoryVideo() ;
    }

    private Binder binder = new VideoPushAIDL.Stub() {
        @Override
        public void setServerAddress(String userName, String ip, int port, int channelNum, boolean needPublish) throws RemoteException {
            Log.d(TAG, "setServerAddress -------------------- "+ip+","+port+","+channelNum) ;
//            IP = ip ;
//            Port = port ;
            phone = userName ;
            if (TextUtils.isEmpty(ip) || port == 0){
                Log.d(TAG, "setServerAddress -------------------- null ") ;
            }else {
                initPushVideo(ip, port, channelNum);
                if (needPublish) timeDisposable();
            }
        }

        @Override
        public boolean openVideo() throws RemoteException {
            return startVideo();
        }

        @Override
        public boolean closeVideo() throws RemoteException {
            return stopVideo();
        }

        @Override
        public void setVideoParameter(int streamType, boolean isVideo, int k) throws RemoteException {

        }

        @Override
        public void distoryVideo() throws RemoteException {
            onDestroy();
        }

        @Override
        public void tackPicture() throws RemoteException {
            tackPhoto();
        }

        @Override
        public void recordVideo(boolean isRecord) throws RemoteException {
            startRecord(isRecord);
        }

        @Override
        public void openCamera(boolean isOpen) throws RemoteException {
            cameraStatus(isOpen);
        }
    } ;


    private void initPushVideo(String ip, int port, int channelNum){
        publish = new Publish.Buider(this, null)
                .setPushMode(new UdpSend(phone, ip, port, channelNum))
                //  帧率
                .setFrameRate(Constants.FRAME_RATE)
                //  编码方式
                .setVideoCode(Constants.VIDEO_ENCODING)
                //  是否预览
                .setIsPreview(Constants.PREVIEW)
                //  推流码率
                .setPublishBitrate(Constants.VIDEO_PUSH_RATE)
                //  采集码率
                .setCollectionBitrate(Constants.VIDEO_SAMPLING_RATE)
                //  音频采集码率
                .setCollectionBitrateVC(Constants.VOICE_SAMPLING_RATE)
                //  音频推流码率
                .setPublishBitrateVC(Constants.VOICE_PUSH_RATE)
                //  推流分辨率
                .setPublishSize(Constants.PUSHER_RESOLUTION_W, Constants.PUSHER_RESOLUTION_H)
                //  预览分辨率
                .setPreviewSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
                //  摄像头选择
                .setRotate(Constants.CAMERA)
                .setVideoDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonLive")
                .setPictureDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonPicture")
                .setCenterScaleType(true)
                .setScreenshotsMode(Publish.TAKEPHOTO)
                .build();
    }


    Disposable disposable ;
    private void timeDisposable(){

        disposable = Flowable.intervalRange(0, Constants.VIDEO_INIT_TIME, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //  倒计时完毕，处理方式
//                        tuistar.performClick() ;
                        startVideo() ;

                    }
                })
                .subscribe() ;
    }

    private boolean startVideo(){
//        setNotificationMessage(DEFAULT_NAME, getString(R.string.tip_video_recording)) ;
        try {
            publish.start();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

    private boolean stopVideo(){
//        setNotificationMessage(DEFAULT_NAME, getString(R.string.tip_video_record_pause)) ;
        try {
            publish.stop();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

    private boolean destoryVideo(){
//        setNotificationMessage(DEFAULT_NAME, getString(R.string.tip_video_record_finish)) ;
        try {
            publish.destroy();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

//    /**
//     * 初始化通知
//     * 初始化文字为
//     * */
//    private void initNotification(){
//        Log.d("cjh", "initNotification -------------------- ") ;
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        setNotificationMessage(DEFAULT_NAME, DEFAULT_CONTENT);
//
//    }

//    private void setNotificationMessage(String name, String msg){
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  //  Android4.1以上
//            builder.setSmallIcon(R.drawable.loading)
//                    .setContentTitle(name)
//                    .setContentText(msg)
//                    .setOnlyAlertOnce(false)        //  用于多次通知
//            ;
//            notificationManager.notify(VIDEO_NOTIFICATION_CODE, builder.getNotification());
//        }
//
//    }

//    private void delNotification(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notificationManager.cancel(VIDEO_NOTIFICATION_CODE);
//        }
//    }


    //  拍照
    private void tackPhoto(){
        publish.takePicture();
    }

    //  录制
    private void startRecord(boolean isStartRecord){
        if (isStartRecord) {
            publish.startRecode();
        } else {
            publish.stopRecode();
        }
    }

    //  是否释放摄像头
    private void cameraStatus(boolean isOpen){
        if (isOpen) {
            publish.open();
        }else {
            publish.release();
        }
    }
}
