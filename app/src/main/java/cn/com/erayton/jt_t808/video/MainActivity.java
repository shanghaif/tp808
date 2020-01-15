package cn.com.erayton.jt_t808.video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.library.constants.PublicConstants;
import com.library.util.OtherUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.jt_t808.video.eventBus.EventBusUtils;
import cn.com.erayton.jt_t808.video.eventBus.event.BroadCastMainEvent;
import cn.com.erayton.jt_t808.video.manager.USManager;
import cn.com.erayton.jt_t808.video.video.SendReady;
import cn.com.erayton.usagreement.model.ServerAVTranslateMsg;
import cn.com.erayton.usagreement.utils.LogUtils;

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
        USManager.getSingleton().ServerLogin(PublicConstants.ApiConstants.USER_NAME, PublicConstants.ApiConstants.HOST,
                PublicConstants.ApiConstants.PORT, PublicConstants.ApiConstants.PORT, false);

//        OtherUtil.executeCmd(PublicConstants.ApiConstants.HOST, false) ;
    }



    private void requestpermission() {
        //SD卡读写权限
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
        push = findViewById(R.id.push);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SendReady.class));
            }
        });
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
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BroadCastMainEvent event) {
        LogUtils.e("onEventMainThread ----------------");
        ServerAVTranslateMsg msg = (ServerAVTranslateMsg) event.getData();
        Bundle bundle = new Bundle() ;
        bundle.putString("ip", msg.getHost());
        bundle.putInt("port", msg.getTcpPort());
        toOtherPage(bundle);
    }


    private void toOtherPage(Bundle bundle){
        Intent intent = new Intent(this, SendReady.class) ;
        intent.putExtras(bundle) ;
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();
    }
}
