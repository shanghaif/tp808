package com.library;


import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

import com.library.live.Publish;

import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.VideoPushAIDL;
import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.LogUtils;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * 视频开放接口类
 * 避免 aidl 暴露
 * */
public class USVideo2 {
    private String TAG = USVideo2.class.getName() ;
    private Context context ;
    private static USVideo2 instance ;
    private VideoPushAIDL videoPushAIDL ;
    Intent intent ;
    Publish publish ;
    private String host ;
    private int tPort ;
    private String phone ;
    Disposable disposable ;

    public static USVideo2 getInstance(Context context) {
        if (instance == null){
            instance = new USVideo2(context) ;
        }
        return instance;
    }

    private USVideo2(Context context) {
        this.context = context;
    }

    public void initVideo(){
        publish = new Publish.Buider(context, null)
                //  帧率
                .setFrameRate(Constants.FRAME_RATE)
                //  编码方式
                .setVideoCode(Constants.VIDEO_ENCODING)
                //  是否预览
                .setIsPreview(false)
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
                .setVideoDirPath(Constants.VIDEOSAVEPATH)
                .setPictureDirPath(Constants.PICTURESAVEPATH)
                .setCenterScaleType(true)
                .setScreenshotsMode(Publish.TAKEPHOTO)
                .build();
    }

    public void setServerAddress(String userName, String ip, int port, int channelNum, boolean needPublish) throws RemoteException {
        LogUtils.d("setServerAddress -------------------- " + ip + "," + port + "," + channelNum);
//            IP = ip ;
//            Port = port ;
        phone = userName;
        host = ip;
        tPort = port;
        if (TextUtils.isEmpty(ip) || port == 0){
            LogUtils.d("setServerAddress -------------------- null ") ;
        }else {
            if (needPublish)
                initPushVideo(ip, port, channelNum);
        }
    }


    private void initPushVideo(final String ip, final int port, final int channelNum){
        disposable = Flowable.intervalRange(0, Constants.VIDEO_INIT_TIME, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //  倒计时完毕，处理方式
//                        tuistar.performClick() ;
//                        startVideo() ;
                        publish.initTcp(phone, ip, port, channelNum, false);
                        startVideo() ;
                    }
                }).subscribe() ;




//        publish.initTcp(phone, ip, port, channelNum, false);
//        startVideo() ;
    }



    public boolean startVideo(){
        LogUtils.d("startVideo----------------------------------");
        try {
            publish.start();
//            alertPromission();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

    public boolean stopVideo(){
        try {
            publish.stop();
//            destoryFloatWindow() ;
            return true ;
        }catch (Exception e) {
            return false;
        }
    }

    public boolean destoryVideo(){
        try {
            publish.destroy();
            return true ;
        }catch (Exception e) {
            return false;
        }
    }


    //  录制
    public void startRecord(boolean isStartRecord){
        if (isStartRecord) {
            publish.startRecode();
        } else {
            publish.stopRecode();
        }
    }

    //  是否释放摄像头
    public void cameraStatus(boolean isOpen){
        if (isOpen) {
            publish.open();
        }else {
            publish.release();
        }
    }



}
