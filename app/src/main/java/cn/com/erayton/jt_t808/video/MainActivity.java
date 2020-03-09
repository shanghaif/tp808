package cn.com.erayton.jt_t808.video;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.jt_t808.constants.PublicConstants;
import cn.com.erayton.jt_t808.video.eventBus.EventBusUtils;
import cn.com.erayton.jt_t808.video.eventBus.event.BroadCastMainEvent;
import cn.com.erayton.jt_t808.video.manager.USManager;
import cn.com.erayton.jt_t808.video.video.Send;
import cn.com.erayton.usagreement.VideoPushAIDL;
import cn.com.erayton.usagreement.service.VideoPushService;
import cn.com.erayton.usagreement.utils.LogUtils;
import cn.erayton.voicelib.Mp3Lib;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA = 666;
    private Button push;
//    private Button pull;
//    private Button voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        EventBusUtils.register(this);
        requestpermission();
//        testVideoHex();
        USManager.getSingleton().ServerLogin("23803641388", "106.14.186.44",
                7000, 7000, false);


        bindServiceAidl() ;
//        OtherUtil.executeCmd(PublicConstants.ApiConstants.HOST, false) ;
    }



    private void requestpermission() {
        //  SD卡读写权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //权限已授权，功能操作
            gostart();
        } else {
            //未授权，提起权限申请
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                gostart();
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

    private void gostart() {
//        String s = Mp3Lib.getHello()+Mp3Lib.getLameVersion() ;
        push = findViewById(R.id.push);
        push.setText(Mp3Lib.getLameVersion());
//        push.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    videoPushAIDL.distoryVideo();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
////                startActivity(new Intent(MainActivity.this, Send.class));
//            }
//        });
    }

    boolean isRecord = true ;

    public void buttonClick(View view){
        switch (view.getId()){
            case R.id.push:
                try {
                    videoPushAIDL.distoryVideo();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tackPhoto:
                try {
                    videoPushAIDL.tackPicture();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.recordVideo:
                try {
                    videoPushAIDL.recordVideo(isRecord);
                    isRecord = !isRecord ;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
                default:
                    break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //判断请求码，确定当前申请的权限
        if (requestCode == REQUEST_CAMERA) {
            //判断权限是否申请通过
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //授权成功
                gostart();
            } else {
                //授权失败
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                gostart();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        LogUtils.e("onEventMainThread ----------------");
        switch (event.getCode()){
            case EventBusUtils.EventCode.OPEN_VIDEO:
//                ServerAVTranslateMsg msg = (ServerAVTranslateMsg) event.getData();
                setService(event.getHost(), event.getTcpPort(), event.getChannelNum());
                break;
            case EventBusUtils.EventCode.CLOSE_VIDEO:
                try {
                    videoPushAIDL.distoryVideo() ;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
                default:
                    break;
        }

    }


    private void toOtherPage(Bundle bundle){
        Intent intent = new Intent(this, Send.class) ;
        intent.putExtras(bundle) ;
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();
    }


    private VideoPushAIDL videoPushAIDL ;
    private void bindServiceAidl(){
        Intent intent = new Intent(this, VideoPushService.class) ;
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                videoPushAIDL = VideoPushAIDL.Stub.asInterface(service) ;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },BIND_AUTO_CREATE) ;

//        stopService(intent) ;
    }

    private void setService(String ip, int port, int channelNum){
        Log.d("cjh", "setService --------------------------------"+ip+port) ;
        if (videoPushAIDL != null){
            try {
                Log.d("cjh", "setService -----------------2---------------"+ip+port) ;
                videoPushAIDL.setServerAddress(PublicConstants.ApiConstants.USER_NAME, ip, port, channelNum);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
