package cn.com.erayton.floatwindow;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cn.com.erayton.jt_t808.R;

public class FloatWindowMainActivity extends AppCompatActivity {

    private ActivityManager mActivityManager;
    private TextView mTextView;
    Handler mHandler;
    TrafficInfo speed;
    FloatWindowService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_window_main);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mTextView = (TextView) findViewById(R.id.tv);
        try {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        mTextView.setText(msg.obj + "kb/s");
                        if(service != null)
                            service.setSpeed(msg.obj+"kb/s"); // 设置网速
                    }
                    super.handleMessage(msg);
                }

            };
            speed = new TrafficInfo(this,mHandler,TrafficInfo.getUid(this));
            speed.startCalculateNetSpeed(); // 开启网速监测
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("test","总流量="+speed.getTrafficInfo());
        Intent intent = new Intent(FloatWindowMainActivity.this, FloatWindowService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((FloatWindowService.ServiceBinder) binder).getService();
        }
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speed.stopCalculateNetSpeed();
        unbindService(conn);
    }
}
