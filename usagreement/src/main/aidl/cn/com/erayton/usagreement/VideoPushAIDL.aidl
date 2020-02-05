// VideoPushAIDL.aidl
package cn.com.erayton.usagreement;

// Declare any non-default types here with import statements

interface VideoPushAIDL {
    //  设置进程 IP 和 PORT
    void setServerAddress(String userName, String ip, int port, int channelNum) ;
//    void setServerAddress(String ip, int port, int channelNum) ;
//    void setServerAddress(String ip, int port) ;
//    void setServerAddress(int channelNum) ;
    //  打开视频录制，预留接口
    boolean openVideo() ;
    //  暂停视频发送
    boolean closeVideo() ;
    //  切换视频参数，预留接口(切换码流，切换音视频)
    void setVideoParameter(int streamType, boolean isVideo, int k) ;
    //  关闭摄像头，关闭视频，关闭服务
    void distoryVideo() ;

    //  拍照
    void tackPicture() ;

    //  录制
    void recordVideo(boolean isRecord) ;

}
