package cn.erayton.cameratest.manager;

import android.content.Context;

import cn.erayton.cameratest.R;
import cn.erayton.cameratest.data.PreferenceGroup;
import cn.erayton.cameratest.module.CameraModule;
import cn.erayton.cameratest.module.DualCameraModule;
import cn.erayton.cameratest.ui.IndicatorView;
import cn.erayton.cameratest.utils.XmlInflater;

public class ModuleManager implements IndicatorView.IndicatorListener {
    private static int sModuleNum = 1;
    private static int mCurrentIndex = 1;
    private CameraModule mCurrentModule;
    private IndicatorView mIndicatorView;
    private Class<?>[] mModulesClass;
    private Controller mController;

    /**
     * Manage all module, use reflection to create module instance
     * @param context used to init ModuleIndicator
     * @param controller interface for change module
     */
    public ModuleManager(Context context, Controller controller) {
        mController = controller ;
        mIndicatorView = mController.getBaseUI().getIndicatorView() ;
        XmlInflater inflater = new XmlInflater(context) ;
        PreferenceGroup group = inflater.inflate(R.xml.module_preference) ;
        mIndicatorView.setIndicatorListener(this);
        boolean loadDualCamera = mController.getCameraSettings(context).isDualCameraEnable() ;
        mModulesClass = getModuleClass(group, loadDualCamera) ;
        sModuleNum = mModulesClass.length ;
        // init default position
        mIndicatorView.select(mCurrentIndex);

    }

    private Class<?>[] getModuleClass(PreferenceGroup group, boolean loadDualCamera){
        if (!loadDualCamera){
            group.remove(DualCameraModule.class.getName()) ;
        }
        Class<?>[] moduleCls = new Class[group.size()] ;
        for (int i = 0; i < group.size(); i++) {
            // use reflection to get module class
            try {
                moduleCls[i] = Class.forName(group.get(i).getKey()) ;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // add indicator item
            mIndicatorView.addItem(group.get(i).getTitle());
        }
        group.clear();
        return moduleCls ;
    }

    public boolean needChangeModule(int index){
        if (index<0 || index >= sModuleNum || mCurrentIndex == index){
            return false ;
        }else {
            mCurrentIndex = index ;
            return true ;
        }
    }

    public CameraModule getNewModule(){
        try {
            mCurrentModule = (CameraModule) mModulesClass[mCurrentIndex].newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return mCurrentModule ;
    }

    public CameraModule getCurrentModule() {
        return mCurrentModule;
    }

    public static int getCurrentIndex() {
        return mCurrentIndex;
    }

    public static boolean isValidIndex(int index) {
        return (index >= 0 && index < sModuleNum);
    }

    public static int getModuleCount() {
        return sModuleNum;
    }



    @Override
    public void onPositionChanged(int index) {
        mController.changeModule(index);
    }
}
