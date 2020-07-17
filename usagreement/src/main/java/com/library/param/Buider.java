package com.library.param;

import android.content.Context;
import android.util.Size;

import com.library.live.Publish;
import com.library.live.stream.TcpSend;
import com.library.live.stream.UdpSend;
import com.library.live.view.PublishView;

public class Buider {
    private Context context;
    private ParameterMap map;

//        @IntDef({CONVERSION, TAKEPHOTO})
//        private @interface ScreenshotsMode {
//        }

    public Buider(Context context, PublishView publishView) {
        map = new ParameterMap();
        this.context = context;
        map.setPublishView(publishView);
    }

    public Buider(Context context) {
        map = new ParameterMap();
        this.context = context;
    }

    //编码分辨率
    public Buider setPublishSize(int publishWidth, int publishHeight) {
        map.setPublishSize(new Size(publishWidth, publishHeight));
        return this;
    }

    public Buider setPreviewSize(int previewWidth, int previewHeight) {
        map.setPreviewSize(new Size(previewWidth, previewHeight));
        return this;
    }

    public Buider setFrameRate(int frameRate) {
        //限制最小8帧
        map.setFrameRate(Math.max(8, frameRate));
        return this;
    }

    public Buider setIsPreview(boolean isPreview) {
        map.setPreview(isPreview);
        return this;
    }

    public Buider setPublishBitrate(int publishBitrate) {
        map.setPublishBitrate(publishBitrate);
        return this;
    }

    public Buider setCollectionBitrate(int collectionBitrate) {
        map.setCollectionBitrate(collectionBitrate);
        return this;
    }

    public Buider setPublishBitrateVC(int publishbitrate_vc) {
        //  限制最大48，因为发送会合并5个包，过大会导致溢出
        map.setPublishbitrate_vc(Math.min(48 * 1024, publishbitrate_vc));
        return this;
    }

    public Buider setCollectionBitrateVC(int collectionbitrate_vc) {
        map.setCollectionbitrate_vc(collectionbitrate_vc);
        return this;
    }

    public Buider setVideoCode(String codetype) {
        map.setCodetype(codetype);
        return this;
    }

    //        public Buider setScreenshotsMode(@ScreenshotsMode int screenshotsMode) {
//            map.setScreenshotsMode(screenshotsMode);
//            return this;
//        }
    public Buider setScreenshotsMode(int screenshotsMode) {
        map.setScreenshotsMode(screenshotsMode);
        return this;
    }


    public Buider setVideoDirPath(String videodirpath) {
        map.setVideodirpath(videodirpath);
        return this;
    }

    public Buider setPictureDirPath(String picturedirpath) {
        map.setPicturedirpath(picturedirpath);
        return this;
    }

    public Buider setRotate(boolean rotate) {
        map.setRotate(rotate);
        return this;
    }

    //        public Buider setPushMode(UdpSend pushMode) {
//            map.setPushMode(pushMode);
//            return this;
//        }

    public Buider setPushMode(TcpSend pushMode) {
        map.setPushMode(pushMode);
        return this;
    }
    public Buider setPushMode(UdpSend pushMode) {
        map.setPushMode(pushMode);
        return this;
    }

    public Buider setCenterScaleType(boolean isCenterScaleType) {
        if (!map.isPreview()) {
            return this;
        }
        map.getPublishView().setCenterScaleType(isCenterScaleType);
        return this;
    }

    //        public Buider setUdpControl(UdpControlInterface udpControl) {
//            map.getPushMode().setUdpControl(udpControl);
//            return this;
//        }
    public Publish build() {
        return new Publish(context, map);
    }
}
