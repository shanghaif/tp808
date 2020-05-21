package cn.erayton.cameratest.fragment;

import androidx.fragment.app.Fragment;

import cn.erayton.cameratest.manager.Controller;

public class CameraFragment extends Fragment {

    public Controller getController() {
        return controller;
    }

    private Controller controller = new Controller() {
    } ;
}
