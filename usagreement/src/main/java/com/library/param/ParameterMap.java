package com.library.param;

import android.os.Environment;
import android.util.Size;

import com.library.data.Constants;
import com.library.live.stream.TcpSend;
import com.library.live.stream.UdpSend;
import com.library.live.vd.VDEncoder;
import com.library.live.view.PublishView;

import java.io.File;

public class ParameterMap {
    private PublishView publishView;
    private int screenshotsMode = Constants.CameraSettings.TAKEPHOTO;
    //编码参数
    private int frameRate = 15;
    private int publishBitrate = 600 * 1024;
    private int collectionBitrate = 600 * 1024;
    private int collectionbitrate_vc = 64 * 1024;
    private int publishbitrate_vc = 24 * 1024;
    //推流分辨率,仅控制推流编码
    private Size publishSize = new Size(480, 320);
    //预览分辨率，控制预览,录制编码分辨率，图片处理分辨率
    private Size previewSize = new Size(480, 320);
    //是否翻转，默认后置
    private boolean rotate = false;
    //设置是否需要显示预览,默认显示
    private boolean isPreview = true;
    private String codetype = VDEncoder.H264;
    //录制地址
    private String videodirpath = null;
    //拍照地址
    private String picturedirpath = Environment.getExternalStorageDirectory().getPath() + File.separator + "VideoPicture";
    private TcpSend tPush;
    private UdpSend uPush;

    public PublishView getPublishView() {
        return publishView;
    }

    public void setPublishView(PublishView publishView) {
        this.publishView = publishView;
    }

    public int getScreenshotsMode() {
        return screenshotsMode;
    }

    public void setScreenshotsMode(int screenshotsMode) {
        this.screenshotsMode = screenshotsMode;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getPublishBitrate() {
        return publishBitrate;
    }

    public void setPublishBitrate(int publishBitrate) {
        this.publishBitrate = publishBitrate;
    }

    public int getCollectionBitrate() {
        return collectionBitrate;
    }

    public void setCollectionBitrate(int collectionBitrate) {
        this.collectionBitrate = collectionBitrate;
    }

    public int getCollectionbitrate_vc() {
        return collectionbitrate_vc;
    }

    public void setCollectionbitrate_vc(int collectionbitrate_vc) {
        this.collectionbitrate_vc = collectionbitrate_vc;
    }

    public int getPublishbitrate_vc() {
        return publishbitrate_vc;
    }

    public void setPublishbitrate_vc(int publishbitrate_vc) {
        this.publishbitrate_vc = publishbitrate_vc;
    }

    public Size getPublishSize() {
        return publishSize;
    }

    public void setPublishSize(Size publishSize) {
        this.publishSize = publishSize;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(Size previewSize) {
        this.previewSize = previewSize;
    }

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public String getCodetype() {
        return codetype;
    }

    public void setCodetype(String codetype) {
        this.codetype = codetype;
    }

    public String getPicturedirpath() {
        return picturedirpath;
    }

    public void setPicturedirpath(String picturedirpath) {
        this.picturedirpath = picturedirpath;
    }

    //        public UdpSend getPushMode() {
//            return pushMode;
//        }
    public TcpSend getPushMode() {
        return tPush;
    }
    public UdpSend getUPushMode() {
        return uPush;
    }

    //        public void setPushMode(UdpSend pushMode) {
//            this.pushMode = pushMode;
//        }
    public void setPushMode(TcpSend tPush) {
        this.tPush = tPush;
    }
    public void setPushMode(UdpSend uPush) {
        this.uPush = uPush;
    }

    public String getVideodirpath() {
        return videodirpath;
    }

    public void setVideodirpath(String videodirpath) {
        this.videodirpath = videodirpath;
    }
}
