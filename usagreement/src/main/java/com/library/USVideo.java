package com.library;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import cn.com.erayton.usagreement.VideoPushAIDL;
import cn.com.erayton.usagreement.VideoPushCallback;
import cn.com.erayton.usagreement.service.VideoPushService;
import cn.com.erayton.usagreement.utils.LogUtils;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 视频开放接口类
 * 避免 aidl 暴露
 * */
public class USVideo {
    private String TAG = USVideo.class.getName() ;
    private Context context ;
    private static USVideo instance ;
    private VideoPushAIDL videoPushAIDL ;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            videoPushAIDL = VideoPushAIDL.Stub.asInterface(service) ;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    } ;


    //  初始化
    public static USVideo getInstance(Context context) {
        if (instance == null){
            instance = new USVideo(context) ;
        }
        return instance;
    }


    private USVideo(Context context) {
        this.context = context ;
        bindServiceAidl();
    }


    //  初始化视频服务
    private void bindServiceAidl(){
        Intent intent = new Intent(context, VideoPushService.class) ;
        context.bindService(intent, serviceConnection ,Context.BIND_AUTO_CREATE) ;
//        context.bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                videoPushAIDL = VideoPushAIDL.Stub.asInterface(service) ;
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//        },Context.BIND_AUTO_CREATE) ;
    }

    /**  设置视频服务参数，设置之后自动开始视频录制
     *
     * @param phone 手机号
     * @param ip    视频服务器
     * @param port  视频服务器端口
     * @param channelNum    通道号
     */
    public void initVideoServer(String phone, String ip, int port, int channelNum, boolean needPublish) throws RemoteException {
        if (videoPushAIDL != null){
            Log.d(TAG,"initVideoServer --------videoPushAIDL != null") ;
            videoPushAIDL.setServerAddress(phone, ip, port, channelNum, needPublish);

        }
    }

//    /**视频录制
//     * @param isStart   true 开始录制， false 停止录制
//     */
//    public void startRecord(boolean isStart) throws RemoteException {
//        videoPushAIDL.recordVideo(isStart);
//    }

    /** 停止视频传输
     *
     * */
    public void stopRecord() throws RemoteException {
        assert videoPushAIDL != null ;
        videoPushAIDL.closeVideo() ;
    }

//    /**截屏
//     *
//     * @throws RemoteException
//     */
//    public void tackPicture() throws RemoteException {
//        videoPushAIDL.tackPicture();
//    }
    /**截屏
     *
     * @throws RemoteException
     */
    public void tackPicture() throws RemoteException {
        assert videoPushAIDL != null ;
        videoPushAIDL.tackPicture();
    }

    /** 开启关闭摄像头
     *
     * @param isOpen    是否开启摄像头
     */
    public void openCamera(boolean isOpen) throws RemoteException {
        assert videoPushAIDL != null ;
        videoPushAIDL.openCamera(isOpen);
    }

    /** 服务状态回调注册
     *
     * @throws RemoteException
     * */
    public void registerCallbackListener(VideoPushCallback callback) throws RemoteException {
        videoPushAIDL.registerCallback(callback);
    }


    /** 服务状态回调取消注册
     *
     * @throws RemoteException
     * */
    public void unRegisterCallbackListener(VideoPushCallback callback) throws RemoteException {
        videoPushAIDL.unRegisterCallback(callback);
    }

    public void onDestory(){
        try {
            if (videoPushAIDL != null) {
                videoPushAIDL.distoryVideo();
            }
            if (isRunService(context, "cn.com.erayton.usagreement.service.VideoPushService")) {
                LogUtils.d("service---------------------------------------running");
                context.unbindService(serviceConnection);
//                IllegalArgumentException
            }
            instance = null ;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断服务是否在运行
     * @param context
     * @param serviceName
     * @return
     * 服务名称为全路径 例如com.ghost.WidgetUpdateService
     */
    public boolean isRunService(Context context,String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
