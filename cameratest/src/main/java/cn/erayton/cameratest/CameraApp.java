package cn.erayton.cameratest;

import android.app.Application;

public class CameraApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
    }
}
