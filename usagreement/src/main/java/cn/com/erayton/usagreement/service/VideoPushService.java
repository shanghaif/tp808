package cn.com.erayton.usagreement.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.library.live.PictureCallback;
import com.library.live.Publish;
import com.library.live.stream.TcpSend;
import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.R;
import cn.com.erayton.usagreement.VideoPushAIDL;
import cn.com.erayton.usagreement.VideoPushCallback;
import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.LogUtils;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;


public class VideoPushService extends Service {
    private String TAG = VideoPushService.class.getName() ;
    private Publish publish ;
    private String phone ;

    private NotificationManager notificationManager ;
    //	前台服务序列号
    public static final int NOTICE_ID = 0x0100;
    private RemoteCallbackList<VideoPushCallback> remoteCallbackList = new RemoteCallbackList<>() ;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        init() ;
        initNotification() ;
    }

    private void init() {
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
            LogUtils.d("setServerAddress -------------------- "+ip+","+port+","+channelNum) ;
//            IP = ip ;
//            Port = port ;
            phone = userName ;
            if (TextUtils.isEmpty(ip) || port == 0){
                LogUtils.d("setServerAddress -------------------- null ") ;
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
        public void registerCallback(VideoPushCallback callback){
            if (remoteCallbackList != null) remoteCallbackList.register(callback) ;
        }
        public void unRegisterCallback(VideoPushCallback callback){
                remoteCallbackList.unregister(callback) ;
        }

//        @Override
//        public void tackPicture() throws RemoteException {
//            tackPhoto();
//        }

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
//                .setPushMode(new UdpSend(phone, ip, port, channelNum))
                .setPushMode(new TcpSend(phone, ip, port, channelNum))
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
        setNotificationMessage(getString(R.string.tip_video_recording), false) ;
        try {
            publish.start();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

    private boolean stopVideo(){
        setNotificationMessage(getString(R.string.tip_video_record_pause), true) ;
        try {
            publish.stop();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

    private boolean destoryVideo(){
        setNotificationMessage(getString(R.string.tip_video_record_finish), true) ;
        try {
            publish.destroy();
            delNotification();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

//    /**
//     * 初始化通知
//     * 初始化文字为
//     * */
    private void initNotification(){
        Log.d("cjh", "initNotification -------------------- ") ;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setNotificationMessage(getString(R.string.tip_video_record), true);

    }

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

    private void delNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (notificationManager != null)
                notificationManager.cancel(NOTICE_ID);
        }
    }

    private void setNotificationMessage(String msg, boolean autoCancel){
//        Intent nfIntent = new Intent(this, LoginActivity.class);
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext())
//                .setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setSmallIcon(R.drawable.loading) // 设置状态栏内的小图标
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(msg) // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        //----------------  新增代码 --------------------------------------
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //  修改安卓8.1以上系统报错

//		NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,NotificationManager.IMPORTANCE_MIN);
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(NOTICE_ID),
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(String.valueOf(NOTICE_ID));
        }


        Notification notification = builder.build(); // 获取构建好的Notification
//        notification.defaults = Notification.DEFAULT_SOUND; //  设置为默认的声音
//	notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags = autoCancel?Notification.FLAG_AUTO_CANCEL:Notification.FLAG_NO_CLEAR;

        startForeground(NOTICE_ID, notification);
    }


    //  拍照
    private void tackPhoto(){
        publish.setPictureCallback(new PictureCallback() {
            @Override
            public void Success(String path) {
                int N = remoteCallbackList.beginBroadcast() ;
                try {
                    for (int i=0 ;i<N ; i++) {
                        remoteCallbackList.getBroadcastItem(i).setPicturePath(path);
                    }

                    remoteCallbackList.finishBroadcast();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

        });

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
