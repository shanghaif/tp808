package cn.com.erayton.floatwindow;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.erayton.jt_t808.R;

public class FloatWindowService extends Service {

    private static final String TAG = "ManagerService";
    public LinearLayout mFloatLayout;
    public WindowManager.LayoutParams wmParams;
    public WindowManager mWindowManager;
    public TextView mFloatView;
    private ServiceBinder binder = new ServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onTrimMemory(int level) {
        Log.i(TAG, " onTrimMemory...");

    }
    /** 创建悬浮窗 */
    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;// 设置window
//        wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;// 设置window
        // type为TYPE_SYSTEM_ALERT
        wmParams.format = PixelFormat.RGBA_8888;// 设置图片格式，效果为背景透明
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;// 默认位置：左上角
        wmParams.width = 100;
//        wmParams.width = WidgetUtils.dpToPx(getApplicationContext(), 65);
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x = 50;// 设置x、y初始值，相对于gravity
//        wmParams.x = (WidgetUtils.getScreenWidth(getApplicationContext()) - wmParams.width) / 2;// 设置x、y初始值，相对于gravity
        wmParams.y = 10;
        // 获取浮动窗口视图所在布局
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);// 添加mFloatLayout
        mFloatView = (TextView) mFloatLayout.findViewById(R.id.speed);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;// 减25为状态栏的高度
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);// 刷新
                return false; // 此处必须返回false，否则OnClickListener获取不到监听
            }
        });
        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // do something... 跳转到应用
            }
        });
    }

    public void setSpeed(String str) {
        mFloatView.setText(str.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null && mWindowManager != null) {
            mWindowManager.removeView(mFloatLayout);// 移除悬浮窗口
        }
        startService(new Intent(this, FloatWindowService.class));
    }

    class ServiceBinder extends Binder {
        public FloatWindowService getService() {
            return FloatWindowService.this;
        }
    }
}
