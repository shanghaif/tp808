package cn.erayton.cameratest.manager;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.view.OrientationEventListener;

import cn.erayton.cameratest.utils.FileSaver;
import cn.erayton.cameratest.utils.JobExecutor;

public class CameraToolKit {

    private Context mContext;
    private Handler mMainHandler;
    private MyOrientationListener mOrientationListener;
    private FileSaver mFileSaver;
    private int mRotation = 0;
    private JobExecutor mJobExecutor;


    public CameraToolKit(Context context) {
        mContext = context ;
        mMainHandler = new Handler(Looper.getMainLooper()) ;
        mFileSaver = new FileSaver(mContext, mMainHandler) ;
        setOrientationListener() ;
        mJobExecutor = new JobExecutor() ;
    }

    public void destroy(){
        if (mFileSaver != null){
            mFileSaver.release() ;
        }
        mOrientationListener.disable() ;
        mJobExecutor.destroy();
    }

    public FileSaver getFileSaver(){
        return mFileSaver ;
    }

    public int getOrientation(){
        return mRotation ;
    }

    public Handler getMainHandler(){
        return mMainHandler ;
    }

    public JobExecutor getExecutor(){
        return mJobExecutor ;
    }

    private class MyOrientationListener extends OrientationEventListener{

        public MyOrientationListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            mRotation = (orientation+45)/ 90 *90 ;
        }
    }

    private void setOrientationListener(){
        mOrientationListener = new MyOrientationListener(mContext, SensorManager.SENSOR_DELAY_UI) ;
        if (mOrientationListener.canDetectOrientation()){
            mOrientationListener.enable();
        }else {
            mOrientationListener.disable();
        }
    }
}
