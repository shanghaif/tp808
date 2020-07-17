package cn.com.erayton;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.rayton.netstatelib.NetWorkMonitorManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
        NetWorkMonitorManager.getInstance().init(this);
    }
}
