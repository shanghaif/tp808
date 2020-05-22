package cn.erayton.cameratest.manager;

import android.content.Context;

public class ModuleManager implements Indica{

    private CameraModule mCurrentModule;

    public ModuleManager(Context context, Controller controller) {
    }

    public CameraModule getCurrentModule() {
        return mCurrentModule;
    }

    public void setCurrentModule(CameraModule mCurrentModule) {
        this.mCurrentModule = mCurrentModule;
    }
}
