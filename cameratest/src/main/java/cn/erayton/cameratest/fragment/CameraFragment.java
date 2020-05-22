package cn.erayton.cameratest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.erayton.cameratest.R;
import cn.erayton.cameratest.event.BusManager;
import cn.erayton.cameratest.manager.CameraSettings;
import cn.erayton.cameratest.manager.CameraToolKit;
import cn.erayton.cameratest.manager.Controller;
import cn.erayton.cameratest.manager.ModuleManager;
import cn.erayton.cameratest.ui.AppBaseUI;

public class CameraFragment extends Fragment {

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
        mModuleManager = new ModuleManager(mAppContext, controller) ;
        BusManager.register(this);
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
            up
        }

    }

    public Controller getController() {
        return controller;
    }

    private Controller controller = new Controller() {
    } ;
}
