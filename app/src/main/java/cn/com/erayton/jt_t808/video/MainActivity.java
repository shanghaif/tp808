package cn.com.erayton.jt_t808.video;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;

import com.library.live.Publish;
import com.library.live.view.PublishView;
import com.library.util.FTPUtils;

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
import cn.com.erayton.usagreement.data.db.DbTools;
import cn.com.erayton.usagreement.data.db.table.VideoRecord;
import cn.com.erayton.usagreement.model.decode.ServerFileUploadMsg;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.socket.client.SocketClientSender;
import cn.com.erayton.usagreement.utils.FileMsg;
import cn.com.erayton.usagreement.utils.FileUtils;
import cn.com.erayton.usagreement.utils.LogUtils;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA = 666;
//    private String host1 = "192.168.1.145" ;
//    private int port1 =5508 ;
    private int port =0 ;
    private String host1 = "106.14.186.44" ;
    private int port1 = 7000 ;
    private String host = "" ;
    private String host2 = "video.erayton.cn" ;
    private int port2 = 7000 ;
//    private String phone ="23803560303" ;
    private String phone ="23803560285" ;
//    private String phone ="23803641388" ;
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
        //  SD???????????????
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //??????????????????????????????
//            gostart();
        } else {
            //??????????????????????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
//                gostart();
            } else {
                //????????????
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
                publish.flashMode(com.library.data.Constants.CameraSettings.FLASH_VALUE_TORCH);
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
                }else {
                    USManager.getSingleton().setIsLocation(true);
                    publish.startRecode();
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
                    publish.flashMode(com.library.data.Constants.CameraSettings.FLASH_VALUE_AUTO);
                }
                return false;
            }
        });
        USManager.getSingleton().ServerLogin(phone, host,
                port, port, false);


        publishView.setVisibility(View.VISIBLE);
        publish = new Publish.Buider(this, publishView)
//                .setPushMode(new UdpSend(phone, ip, port, channelNum))
//                .setPushMode(new TcpSend(phone, ip, port, channelNum))
                //  ??????
                .setFrameRate(Constants.FRAME_RATE)
                //  ????????????
                .setVideoCode(Constants.VIDEO_ENCODING)
                //  ????????????
                .setIsPreview(Constants.PREVIEW)
                //  ????????????
                .setPublishBitrate(Constants.VIDEO_PUSH_RATE)
                //  ????????????
                .setCollectionBitrate(Constants.VIDEO_SAMPLING_RATE)
                //  ??????????????????
                .setCollectionBitrateVC(Constants.VOICE_SAMPLING_RATE)
                //  ??????????????????
                .setPublishBitrateVC(Constants.VOICE_PUSH_RATE)
                //  ???????????????
                .setPublishSize(Constants.PUSHER_RESOLUTION_W, Constants.PUSHER_RESOLUTION_H)
//                .setPublishSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
                //  ???????????????
                .setPreviewSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
                //  ???????????????
                .setRotate(Constants.CAMERA)
                .setVideoDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonLive")
                .setPictureDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonPicture")
                //  ?????????????????????
                .setCenterScaleType(true)
                .setScreenshotsMode(Publish.CONVERSION)
                .build();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //?????????????????????????????????????????????
        if (requestCode == REQUEST_CAMERA) {
            //??????????????????????????????
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //????????????
//                gostart();
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            } else {
                //????????????
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
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
     * ????????????
     * ?????????IP???????????????????????????????????????
     * ?????????????????????
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
//                //  ??????
//                .setFrameRate(Constants.FRAME_RATE)
//                //  ????????????
//                .setVideoCode(Constants.VIDEO_ENCODING)
//                //  ????????????
//                .setIsPreview(Constants.PREVIEW)
//                //  ????????????
//                .setPublishBitrate(Constants.VIDEO_PUSH_RATE)
//                //  ????????????
//                .setCollectionBitrate(Constants.VIDEO_SAMPLING_RATE)
//                //  ??????????????????
//                .setCollectionBitrateVC(Constants.VOICE_SAMPLING_RATE)
//                //  ??????????????????
//                .setPublishBitrateVC(Constants.VOICE_PUSH_RATE)
//                //  ???????????????
//                .setPublishSize(Constants.PUSHER_RESOLUTION_W, Constants.PUSHER_RESOLUTION_H)
//                //  ???????????????
//                .setPreviewSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
//                //  ???????????????
//                .setRotate(Constants.CAMERA)
//                .setVideoDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonLive")
//                .setPictureDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "erayTonPicture")
//                .setCenterScaleType(true)
//                .setScreenshotsMode(Publish.TAKEPHOTO)
//                .build();
        publish.initTcp(phone, ip, port, channelNum, false);
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
        byte[] a = { 1, 1, 1, 1, 1, 1, 1, 1 };
        for (FileMsg v: FileUtils.getNativeVideo(getContentResolver(), 0)){
            LogUtils.d("cjh", ""+v) ;
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

    private void fileUpload(final int seNum, final ServerFileUploadMsg msg){
        //  ?????????????????????????????????????????????????????????????????????
        final String uri =DbTools.queryVideoRecord(Long.parseLong(msg.getStartTime())).getName() ;    //  ????????????
        FTPUtils.getInstance().initFtpClient(msg.getHost(), msg.getPort(),
                msg.getUserName(), msg.getPassword());
        new Thread(new Runnable() {
            @Override
            public void run() {
//                        FTPUtils.getInstance().connectFtp() ;

                FTPUtils.getInstance().uploadFile(msg.getUploadPath(),
                        msg.getStartTime() + ".mp4", uri, new FTPUtils.FTPListener() {
                            @Override
                            public void Success() {
                                SocketClientSender.sendUploadStatus(seNum, 0, false, false) ;
                            }

                            @Override
                            public void Status(int code, String msg) {

                            }

                            @Override
                            public void Failer(String errorMsg) {
                                SocketClientSender.sendUploadStatus(seNum, 1, false, false) ;
                            }
                        });
            }
        }).start();

//        SocketClientSender.sendUploadStatus(seNum, 0, false, false) ;
    }


}
