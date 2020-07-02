package cn.erayton.cameratest.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.erayton.cameratest.CameraActivity;
import cn.erayton.cameratest.Config;
import cn.erayton.cameratest.R;
import cn.erayton.cameratest.manager.CameraSettings;
import cn.erayton.cameratest.manager.CameraToolKit;
import cn.erayton.cameratest.manager.Controller;
import cn.erayton.cameratest.manager.ModuleManager;
import cn.erayton.cameratest.module.CameraModule;
import cn.erayton.cameratest.ui.AppBaseUI;
import cn.erayton.cameratest.utils.JobExecutor;

public class CameraFragment extends Fragment {
    private static final String TAG = Config.getTag(CameraFragment.class);

    private CameraToolKit mToolKit;
    private ModuleManager mModuleManager;
    private AppBaseUI mBaseUI;
    private CameraSettings mSettings;
    private Context mAppContext;
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppContext = getActivity().getApplicationContext() ;
        mToolKit = new CameraToolKit(mAppContext) ;
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.camera_fragment_layout, null) ;
        mBaseUI = new AppBaseUI(mAppContext, mRootView) ;
        mModuleManager = new ModuleManager(mAppContext, mController) ;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return mRootView ;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModuleManager.getCurrentModule() == null){
            Log.d(TAG, "init module");
            updateThumbnail(mAppContext) ;
            CameraModule cameraModule = mModuleManager.getNewModule() ;
            cameraModule.init(mAppContext,mController);
        }
        mModuleManager.getCurrentModule().startModule();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mModuleManager.getCurrentModule() != null) {
            mModuleManager.getCurrentModule().stopModule();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mToolKit.destroy();
    }

    public Controller getController() {
        return mController;
    }

    private Controller mController = new Controller() {
        @Override
        public void changeModule(int index) {
            if (mModuleManager.needChangeModule(index)){
                mModuleManager.getCurrentModule().stopModule();
                CameraModule module = mModuleManager.getNewModule();
                module.init(mAppContext, this);
                module.startModule();
            }
        }

        @Override
        public CameraToolKit getToolKit() {
            return mToolKit;
        }

        @Override
        public void showSetting() {
            getCameraActivity().addSettingFragment();
        }

        @Override
        public CameraSettings getCameraSettings(Context context) {
            if (mSettings == null){
                mSettings = new CameraSettings(context) ;
            }
            return mSettings;
        }

        @Override
        public AppBaseUI getBaseUI() {
            return mBaseUI;
        }
    } ;

    private CameraActivity getCameraActivity(){
        if (getActivity() instanceof CameraActivity){
            return (CameraActivity) getActivity();
        }else {
            throw new RuntimeException("CameraFragment must add to CameraActivity") ;
        }
    }

    private void updateThumbnail(final Context context){
        mToolKit.getExecutor().execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                mBaseUI.updateThumbnail(context, mToolKit.getMainHandler());
                return super.run();
            }
        });
    }
}
