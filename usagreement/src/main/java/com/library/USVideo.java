package com.library;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import cn.com.erayton.usagreement.VideoPushAIDL;
import cn.com.erayton.usagreement.service.VideoPushService;

/**
 * 视频开放接口类
 * 避免 aidl 暴露
 * */
public class USVideo {
    private String TAG = USVideo.class.getName() ;
    private Context context ;
    private static USVideo instance ;
    private VideoPushAIDL videoPushAIDL ;


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
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                videoPushAIDL = VideoPushAIDL.Stub.asInterface(service) ;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },Context.BIND_AUTO_CREATE) ;
    }

    /**  设置视频服务参数，设置之后自动开始视频录制
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

    /**
     * 停止视频传输
     * */
    public void stopRecord() throws RemoteException {
        videoPushAIDL.distoryVideo() ;
    }

    /**截屏
     *
     * @throws RemoteException
     */
    public void tackPicture() throws RemoteException {
        videoPushAIDL.tackPicture();
    }

    /**
     * 开启关闭摄像头
     * @param isOpen    是否开启摄像头
     */
    public void opCamera(boolean isOpen) throws RemoteException {
        videoPushAIDL.openCamera(isOpen);
    }
}
