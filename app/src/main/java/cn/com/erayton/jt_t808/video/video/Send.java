package cn.com.erayton.jt_t808.video.video;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.library.live.Publish;
import com.library.live.stream.UdpSend;
import com.library.live.view.PublishView;
import com.library.param.Buider;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.usagreement.data.Constants;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

public class Send extends AppCompatActivity {
    private Publish publish;
//    private Button tuistar;
//    private Button rot;
//    private Button record;
//    private Button takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

//        tuistar = findViewById(R.id.tuistar);
//        rot = findViewById(R.id.rot);
//        takePicture = findViewById(R.id.takePicture);
//        record = findViewById(R.id.record);

        publish = new Buider(this, (PublishView) findViewById(R.id.publishView))
                .setPushMode(new UdpSend(getIntent().getExtras().getString("ip"), getIntent().getExtras().getInt("port")))
//                .setPushMode(new TcpSend(getIntent().getExtras().getString("ip"), getIntent().getExtras().getInt("port")))
                //  帧率
                .setFrameRate(Constants.FRAME_RATE)
                //  编码方式
                .setVideoCode(Constants.VIDEO_ENCODING)
                //  是否预览
                .setIsPreview(Constants.PREVIEW)
                //  推流码率
                .setPublishBitrate(Constants.VIDEO_PUSH_RATE)
                //  采集码率
                .setCollectionBitrate(Constants.VIDEO_SAMPLING_RATE)
                //  音频采集码率
                .setCollectionBitrateVC(Constants.VOICE_SAMPLING_RATE)
                //  音频推流码率
                .setPublishBitrateVC(Constants.VOICE_PUSH_RATE)
                //  推流分辨率
                .setPublishSize(Constants.PUSHER_RESOLUTION_W, Constants.PUSHER_RESOLUTION_H)
                //  预览分辨率
                .setPreviewSize(Constants.PREVIEW_RESOLUTION_W, Constants.PREVIEW_RESOLUTION_H)
                //  摄像头选择
                .setRotate(Constants.CAMERA)
                .setVideoDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "VideoLive")
                .setPictureDirPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "VideoPicture")
                .setCenterScaleType(true)
                .setScreenshotsMode(com.library.data.Constants.CameraSettings.TAKEPHOTO)
                .build();

//        tuistar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (tuistar.getText().toString().equals("开始推流")) {
//                    publish.start();
//                    tuistar.setText("停止推流");
//                } else {
//                    publish.stop();
//                    tuistar.setText("开始推流");
//                }
//            }
//        });
//
//        record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (record.getText().toString().equals("开始录制")) {
//                    publish.startRecode();
//                    record.setText("停止录制");
//                } else {
//                    publish.stopRecode();
//                    record.setText("开始录制");
//                }
//            }
//        });
//
//        takePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                publish.takePicture();
//            }
//        });
//
//        rot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                publish.rotate();
//            }
//        });
        timeDisposable() ;
    }

    @Override
    protected void onDestroy() {
        publish.destroy();
        if (disposable != null){
            disposable.dispose();
        }
        publish.stop();
        super.onDestroy();
    }

    Disposable disposable ;
    private void timeDisposable(){
        disposable = Flowable.intervalRange(0, 3, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //  倒计时完毕，处理方式
//                        tuistar.performClick() ;
                        publish.start() ;
                    }
                })
                .subscribe() ;
    }

}
