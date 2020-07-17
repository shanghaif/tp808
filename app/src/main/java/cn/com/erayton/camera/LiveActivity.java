package cn.com.erayton.camera;

import android.media.AudioFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.frank.live.LivePusherNew;
import com.frank.live.camera2.Camera2Helper;
import com.frank.live.listener.LiveStateChangeListener;
import com.frank.live.param.AudioParam;
import com.frank.live.param.VideoParam;

import cn.com.erayton.jt_t808.R;

public class LiveActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, LiveStateChangeListener, View.OnClickListener {
    private SurfaceView textureView;
//    private TextureView textureView;
    private LivePusherNew livePusher;
    private Button buttonSwap ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        textureView = findViewById(R.id.surface_camera);
        buttonSwap = findViewById(R.id.btn_swap) ;
        buttonSwap.setOnClickListener(this);
        initPusher();
//        Intent intent = new Intent(this, PushActivity.class) ;
//        startActivity(intent);



    }



    private void initPusher() {
        int width = 640;//resolution
        int height = 480;
        int videoBitRate = 800_000;//kb/s
        int videoFrameRate = 10;//fps
        int sampleRate = 44100;//sample rate: Hz
        VideoParam videoParam = new VideoParam(width, height,
                Integer.valueOf(Camera2Helper.CAMERA_ID_BACK), videoBitRate, videoFrameRate);
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int numChannels = 2;//channel number
        AudioParam audioParam = new AudioParam(sampleRate, channelConfig, audioFormat, numChannels);
        livePusher = new LivePusherNew(this, videoParam, audioParam);
//        livePusher = new LivePusherNew(this, videoParam, audioParam, textureView);
        livePusher.setPreviewDisplay(textureView.getHolder());
    }



    @Override
    public void onError(String msg) {
//        Log.e(TAG, "errMsg=" + msg);
//        mHandler.obtainMessage(MSG_ERROR, msg).sendToTarget();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (livePusher != null) {
            livePusher.release();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_swap:
                livePusher.switchCamera();
                break;
            case R.id.btn_mute:
//                livePusher.setMute(false);
                break;
            case R.id.btn_live:
                break;
                default:
                    break;
        }
    }

//    @Override
//    void onViewClick(View view) {
//        if (view.getId() == R.id.btn_swap) {//switch camera
//            livePusher.switchCamera();
//        }
//    }

}
