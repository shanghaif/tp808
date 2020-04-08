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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.library.live.PictureCallback;
import com.library.live.Publish;
import com.library.live.stream.TcpSend;
import com.library.live.view.PublishView;

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
        //  悬浮框点击事件的处理
//        initFloating();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        init() ;
        initNotification() ;
//        initWindow();
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
        destoryFloatWindow();
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
        publish = new Publish.Buider(this, publishView)
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
        if(autoCancel){
//            notification.flags = Notification.FLAG_NO_CLEAR; // 点击清除按钮时就会清除消息通知,但是点击通知栏的通知时不会消失
//            notification.flags = Notification.FLAG_ONGOING_EVENT; // 点击清除按钮不会清除消息通知,可以用来表示在正在运行
//            notification.flags |= Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
//            notification.flags |= Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应
            notification.flags |= Notification.FLAG_AUTO_CANCEL ;
        }else
            notification.flags = Notification.FLAG_NO_CLEAR;
//        notification.flags |= autoCancel ? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_INSISTENT ;
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



    //  ----------------------------- test float window preview
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private LayoutInflater inflater;
    //  浮动布局view
    private View floatingLayout;
    //  容器父布局
    private View mainView;
    private PublishView publishView ;
    /**
     * 设置悬浮框基本参数（位置、宽高等）
     */
    private void initWindow() {
        Log.d(TAG, "initWindow ------------------") ;
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //设置好悬浮窗的参数
        layoutParams = getParams();
        // 悬浮窗默认显示以左上角为起始坐标
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从右上角开始，所以屏幕左上角是x=屏幕最大值;y=0
        layoutParams.x = 10;
        layoutParams.y = 120;
        //得到容器，通过这个inflater来获得悬浮窗控件
        inflater = LayoutInflater.from(getApplicationContext());
        // 获取浮动窗口视图所在布局
        floatingLayout = inflater.inflate(R.layout.view_float_video, null);
        publishView = floatingLayout.findViewById(R.id.publishView) ;
        // 添加悬浮窗的视图
        windowManager.addView(floatingLayout, layoutParams);
    }

    private WindowManager.LayoutParams getParams() {
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置可以显示在状态栏上
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        //设置悬浮窗口长宽数据
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return layoutParams;
    }

    //  加载远端视屏：在这对悬浮窗内内容做操作
    private void initFloating() {
//        //将子View加载进悬浮窗View
//        mainView = floatingLayout.findViewById(R.id.trtc_video_view_layout_float);  //  悬浮窗父布局
//        View mChildView = renderView.getChildView();    //  加载进悬浮窗的子View，这个VIew来自天转过来的那个Activity里面的那个需要加载的View
//        mainView.addView(mChildView);   //  将需要悬浮显示的Viewadd到mTXCloudVideoView中

//        windowManager.addView(mChildView, layoutParams);

//        //悬浮框触摸事件，设置悬浮框可拖动
//        mTXCloudVideoView.setOnTouchListener(this::onTouch);
//        //悬浮框点击事件
//        mTXCloudVideoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //在这里实现点击重新回到Activity
//                Intent intent =
//                        new Intent(FloatWindowService.this, RtcActivity.class);//从该service跳转至该activity会将该activity从后台唤醒，所以activity会走onReStart（）
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//从Service跳转至RTCActivity，需要Intent.FLAG_ACTIVITY_NEW_TASK，不然会崩溃
//                startActivity(intent);
//            }
//        });

    }

    private void destoryFloatWindow(){

        if (floatingLayout != null) {
            // 移除悬浮窗口
            windowManager.removeView(floatingLayout);
            floatingLayout = null;
        }
    }
}
