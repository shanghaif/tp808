package cn.com.erayton.jt_t808.video;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;

import com.library.bean.FileMsg;
import com.library.live.Publish;
import com.library.live.view.PublishView;
import com.library.param.Buider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.jt_t808.video.eventBus.EventBusUtils;
import cn.com.erayton.jt_t808.video.eventBus.event.BroadCastMainEvent;
import cn.com.erayton.jt_t808.video.manager.USManager;
import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.decode.ServerFileUploadMsg;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.socket.client.SocketClientSender;
import cn.com.erayton.usagreement.utils.FTPUtils;
import cn.com.erayton.usagreement.utils.FileUtils;
import cn.com.erayton.usagreement.utils.LogUtils;
import cn.com.erayton.usagreement.utils.TimeUtils;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA = 666;
    //    private String host1 = "192.168.1.145" ;
//    private int port1 =5508 ;
    private int port =0 ;
    private String host1 = "106.14.186.44" ;
    private int port1 = 7000 ;
    private String host = "" ;
    private String host2 = "video.erayton.cn" ;
//    private String host2 = "60.13.227.76" ;
    private int port2 = 7000 ;
//        private String phone ="23803560303" ;
//    private String phone ="23703930979" ;
    private String phone ="23803560285" ;
//    private String phone ="23803560317" ;
//        private String phone ="23803641388" ;
    PublishView publishView ;
    Publish publish ;
    Button button , ipButton;
    AppCompatSeekBar seekBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        EventBusUtils.register(this);
        requestpermission();
        host = host2 ;
        port = port2 ;
        initView();


    }

    //判断sd卡是否存在并返回根目录
    private String getSDCardPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取根目录
        }
        LogUtils.d("cjh", "getSDCardPath:"+sdDir);
        return sdDir.toString();
    }

    public void buttonClick(View view){
        switch (view.getId()){
            case R.id.changeIp:
                if (host.equalsIgnoreCase(host1)){
                    host = host2 ;
                    port = port2;
                }else {
                    host = host1 ;
                    port = port1 ;
                }
                ipButton.setText(host);
                USManager.getSingleton().ServerLogin(phone, host,
                        port, port, false);
                break;
        }

    }

    private void requestpermission() {
        //  SD卡读写权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //权限已授权，功能操作
//            gostart();
        } else {
            //未授权，提起权限申请
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
//                gostart();
            } else {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CAMERA);
            }

        }
    }

    private boolean isRecord = false ;
    private void initView() {
        publishView = findViewById(R.id.publishView) ;
        button = findViewById(R.id.record_btn) ;
        ipButton = findViewById(R.id.changeIp) ;
        ipButton.setText(host);
        seekBar = findViewById(R.id.camera_zoom) ;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                publish.updateZoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ipButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ipButton.setVisibility(View.GONE);
//                publish.flashMode(com.library.data.Constants.CameraSettings.FLASH_VALUE_TORCH);
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecord){
                    USManager.getSingleton().setIsLocation(false);

                    publish.stopRecode();
                    button.setText("start Record");
                    isRecord = false ;
                    MediaScannerConnection.scanFile(MainActivity.this, new String[]{getSDCardPath()}, new String[]{"video/*", "audio/*"}, null);
                }else {
                    USManager.getSingleton().setIsLocation(true);
                    publish.startRecode();
                    publish.takePicture();
                    button.setText("stop Record");
                    isRecord = true ;
                }
                USManager.getSingleton().SendGPS(true, true, true, true) ;
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ipButton.getVisibility() == View.GONE){
                    ipButton.setVisibility(View.VISIBLE);
//                    publish.flashMode(com.library.data.Constants.CameraSettings.FLASH_VALUE_AUTO);
                    publish.rotate();

                }
                return false;
            }
        });
        USManager.getSingleton().ServerLogin(phone, host,
                port, port, false);


        publishView.setVisibility(View.VISIBLE);
        publish = new Buider(this, publishView)
//                .setPushMode(new UdpSend(phone, ip, port, channelNum))
//                .setPushMode(new TcpSend(phone, ip, port, channelNum))
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
//                .setPublishSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
                //  预览分辨率
                .setPreviewSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
                //  摄像头选择
                .setRotate(Constants.CAMERA)
                .setVideoDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonLive")
                .setPictureDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonPicture")
                //  按比例显示图像
                .setCenterScaleType(true)
                .setScreenshotsMode(com.library.data.Constants.CameraSettings.CONVERSION)
                .build();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断请求码，确定当前申请的权限
        if (requestCode == REQUEST_CAMERA) {
            //判断权限是否申请通过
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //授权成功
//                gostart();
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                //授权失败
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
//                gostart();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
        publish.destroy();
    }

    /**
     * 主页事件
     * 接收到IP之后跳转到推流参数设置界面
     * 关闭时关闭视频
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BroadCastMainEvent event) {
        LogUtils.e("onEventMainThread ----------------"+event);
        switch (event.getCode()){
            case EventBusUtils.EventCode.OPEN_VIDEO:
//                ServerAVTranslateMsg msg = (ServerAVTranslateMsg) event.getData();
//                setService(event.getHost(), event.getTcpPort(), event.getChannelNum());
                initVideo(event.getHost(), event.getTcpPort(), event.getChannelNum());

                break;
            case EventBusUtils.EventCode.CLOSE_VIDEO:
//                    videoPushAIDL.distoryVideo() ;
                publish.stop();
//                publish.destroy();
//                stopVideo();

                break;
            case EventBusUtils.EventCode.QUERY_RESOURCE:
//                    videoPushAIDL.distoryVideo() ;
//                    publish.stop();
                Log.e("cjh", "FILEUPLOAD_REQ------------") ;
                queryVideo((Integer) event.getData());

                break;
            case EventBusUtils.EventCode.FILEUPLOAD_REQ:
//                    videoPushAIDL.distoryVideo() ;
//                    publish.stop();
                fileUpload(event.getFlowId(), (ServerFileUploadMsg)event.getData());
                break;
            default:
                break;
        }

    }


    private void initVideo(String ip, int port, int channelNum){
//        publishView.setVisibility(View.VISIBLE);
//        button.setVisibility(View.VISIBLE);
//        publish = new Publish.Buider(this, publishView)
////                .setPushMode(new UdpSend(phone, ip, port, channelNum))
////                .setPushMode(new TcpSend(phone, ip, port, channelNum))
//                //  帧率
//                .setFrameRate(Constants.FRAME_RATE)
//                //  编码方式
//                .setVideoCode(Constants.VIDEO_ENCODING)
//                //  是否预览
//                .setIsPreview(Constants.PREVIEW)
//                //  推流码率
//                .setPublishBitrate(Constants.VIDEO_PUSH_RATE)
//                //  采集码率
//                .setCollectionBitrate(Constants.VIDEO_SAMPLING_RATE)
//                //  音频采集码率
//                .setCollectionBitrateVC(Constants.VOICE_SAMPLING_RATE)
//                //  音频推流码率
//                .setPublishBitrateVC(Constants.VOICE_PUSH_RATE)
//                //  推流分辨率
//                .setPublishSize(Constants.PUSHER_RESOLUTION_W, Constants.PUSHER_RESOLUTION_H)
//                //  预览分辨率
//                .setPreviewSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
//                //  摄像头选择
//                .setRotate(Constants.CAMERA)
//                .setVideoDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonLive")
//                .setPictureDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonPicture")
//                .setCenterScaleType(true)
//                .setScreenshotsMode(Publish.TAKEPHOTO)
//                .build();
//        publish.initTcp(phone, ip, port, channelNum, false);
        publish.initTcp(phone, ip, port, channelNum, true);
        publish.start();
    }

    //    public void queryVideo(int seNum){
    List<TerminalResourceInfo> infos = new ArrayList<>() ;
//        byte[] a = { 1, 1, 1, 1, 1, 1, 1, 1 };
//        for (VideoRecord v:DbTools.queryVideoRecord()){
//            TerminalResourceInfo info = new TerminalResourceInfo() ;
//            info.setChannelNum(v.getChannel());
//            info.setStartTime(String.valueOf(v.getStartTime()));
//            info.setEndTime(String.valueOf(v.getEndTime()));
//            // byte[] a = { 0, 0, 0, 0, 0, 0, 0, 0 };
//            info.setWrang(a);
//            info.setResourceType(v.getSourceType());
//            info.setSteamType(v.getStreamType());
//            info.setMemoryType(v.getMemoryType());
//            info.setFileSize(v.getSize());
//            infos.add(info) ;
//        }
//
//        USManager.getSingleton().SendAVResourceList(seNum, infos) ;
//    }

    public void queryVideo(int seNum){

        List<TerminalResourceInfo> infos = new ArrayList<>() ;
        long a = 0;
//        byte[] a = { 1, 1, 1, 1, 1, 1, 1, 1 };
        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0)){
//        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0)){
//        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0,
//                TimeUtils.dayAgo(-1, "yyMMddHHmmss"),
//                TimeUtils.dayAgo(1, "yyMMddHHmmss"))){
//        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0, "1592616780", "1593273599")){
            LogUtils.d("cjh", ""+v+","+seNum) ;
            TerminalResourceInfo info = new TerminalResourceInfo() ;
            info.setChannelNum(1);
            info.setStartTime(v.getStartTime());
            info.setEndTime(v.getEndTime());
            // byte[] a = { 0, 0, 0, 0, 0, 0, 0, 0 };
            info.setWrang(a);
            info.setResourceType(v.getFileType());
            info.setSteamType(1);
            info.setMemoryType(1);
            info.setFileSize(v.getFileSize());
            infos.add(info) ;
        }

        USManager.getSingleton().SendAVResourceList(seNum, infos) ;
    }


    String uri = null ;
    private void fileUpload(final int seNum, final ServerFileUploadMsg msg){
        //  文件上传，通过开始时间和结束时间，找到文件路径
        LogUtils.d("cjh", "fileUpload:"+msg);
        FTPUtils.getInstance().initFtpClient(msg.getHost(), msg.getPort(),
                msg.getUserName(), msg.getPassword());
//        final String uri =DbTools.queryVideoRecord(Long.parseLong(msg.getStartTime())).getName() ;    //  文件路径
        for (FileMsg v:FileUtils.getVideoPath(getContentResolver(), msg.getResourceType(),
                TimeUtils.date2TimeStamp(msg.getStartTime(), "yyMMddHHmmss"))){
            LogUtils.d("cjh", "getStartTime():"+v);
            uri = v.getFilePath()  ;
            break;
        }
//        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), msg.getResourceType())){
//            if (v.getStartTime().equals(msg.getStartTime())){
//                LogUtils.d("cjh", "v.getStartTime():"+v.getFilePath());
//                uri = v.getFilePath()  ;
//                break;
//            }
//        }
        LogUtils.d("FTP", "uri:"+uri);
        if (uri == null){
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
//                        FTPUtils.getInstance().connectFtp() ;

                FTPUtils.getInstance().uploadFile(msg.getUploadPath(),
                        msg.getStartTime() + ".mp4", uri, new FTPUtils.FTPListener() {
                            @Override
                            public void Success() {
                                boolean su = SocketClientSender.sendUploadStatus(seNum, 0, true, false) ;
                                LogUtils.d("FTP", "Success --------------------"+seNum+":result:"+su);
                            }

                            @Override
                            public void Status(int code, String msg) {

                            }

                            @Override
                            public void Failer(String errorMsg) {
                                SocketClientSender.sendUploadStatus(seNum, 1, true, false) ;
                            }
                        });
            }
        }).start();

//        SocketClientSender.sendUploadStatus(seNum, 0, false, false) ;
    }


}
