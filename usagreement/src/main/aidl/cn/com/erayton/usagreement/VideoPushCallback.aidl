// VideoPushCallback.aidl
package cn.com.erayton.usagreement;

// Declare any non-default types here with import statements

interface VideoPushCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void setPicturePath(String path);
    void Error(int code, String reason);
}
