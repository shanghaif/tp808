package cn.erayton.cameratest.callback;

/**
 * Interface for get information need for show menu
 */
public interface MenuInfo {
    String[] getCameraIdList();

    String getCurrentCameraId();

    String getCurrentValue(String key);

}
