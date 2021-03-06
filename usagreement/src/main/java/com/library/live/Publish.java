package com.library.live;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.library.common.WriteFileCallback;
import com.library.data.Constants;
import com.library.live.file.WriteMp4;
import com.library.live.stream.TcpSend;
import com.library.live.stream.UdpSend;
import com.library.live.vc.VoiceRecord;
import com.library.live.vd.RecordEncoderVD;
import com.library.live.vd.VDEncoder;
import com.library.live.view.PublishView;
import com.library.util.FileUtils;
import com.library.util.ImagUtil;
import com.library.util.OtherUtil;
import com.library.util.Rotate3dAnimation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import cn.com.erayton.usagreement.ApplicationProvider;
import cn.com.erayton.usagreement.utils.LogUtils;

public class Publish implements TextureView.SurfaceTextureListener {
    private Context context;
    private final int frameMax = 4;
    //帧率控制队列
    private ArrayBlockingQueue<Image> frameRateControlQueue = new ArrayBlockingQueue<>(frameMax);
    //视频编码
    private VDEncoder vdEncoder = null;
    //视频录制
    private RecordEncoderVD recordEncoderVD = null;
    //音频采集
    private VoiceRecord voiceRecord;
    //UDP发送类
    private TcpSend tcpSend;
    private UdpSend udpSend;
    private int rotateAngle = 90;   //  270 图片需要翻转角度
    private boolean isCameraBegin = false;
    private boolean useuvPicture = false;

    public static final int TAKEPHOTO = 0;
    //  截屏模式
    public static final int CONVERSION = 1;

    private ParameterMap map;
    //相机设备
    private CameraDevice cameraDevice;
    //捕获会话
    private CameraCaptureSession session;
    //拍照CaptureRequest
    private CaptureRequest captureRequest;
    //用于获取预览数据相关
    private ImageReader previewImageReader;
    //用于获取拍照数据相关
    private ImageReader pictureImageReader;
    //预览分辨率
    private Size previewSize;
    //推流分辨率
    private Size publishSize;
    //拍照回调
    private PictureCallback pictureCallback;

    //异步线程
    private HandlerThread controlFrameRateThread;
    private HandlerThread handlerCamearThread;
    private Handler camearHandler;
    private Handler frameHandler;

    private WriteMp4 writeMp4;

    private CameraManager manager;
    private String cameraId;
    private CameraCharacteristics characteristics ;


    //  此摄像头最大放大倍数
    protected float maximumZoomLevel;

    private Publish(Context context, ParameterMap map) {
        this.context = context;
        this.map = map;
        this.publishSize = map.getPublishSize();
        this.previewSize = map.getPreviewSize();
        this.tcpSend = map.getPushMode();

        writeMp4 = new WriteMp4(map.getVideodirpath());

        handlerCamearThread = new HandlerThread("Camear2");
        handlerCamearThread.start();
        camearHandler = new Handler(handlerCamearThread.getLooper());

        starFrameControl();

        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        initCamera();
        if (map.isPreview()) {//如果需要显示预览
            map.getPublishView().setSurfaceTextureListener(this);
        } else {
            openCamera();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        releaseCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    /**
     * 初始化相机管理
     */
    private void initCamera() {
        try {
            //  遍历所有摄像头,查找符合当前选择的摄像头
            for (String cameraId : manager.getCameraIdList()) {
                characteristics = manager.getCameraCharacteristics(cameraId);
//                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
                        (map.isRotate() ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK)) {
                    //获取StreamConfigurationMap管理摄像头支持的所有输出格式和尺寸,根据TextureView的尺寸设置预览尺寸
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    //选取最佳分辨率初始化编码器（未必和设置的匹配，由于摄像头不支持设置的分辨率）
                    this.cameraId = cameraId;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //  在不打开相机的情况下，设置给定ID的相机闪光灯单元的手电筒模式
                        manager.setTorchMode(cameraId, false);
                    }

                    //  视频上传平台时, 比例不一样，需要旋转90°, 否则会出现绿条
                    rotateAngle = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                    initCode(map.getOutputSizes(SurfaceTexture.class));
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void initCode(Size[] outputSizes) {
        if (vdEncoder == null) {
//            publishSize = initSize(publishSize, outputSizes);
            previewSize = initSize(previewSize, outputSizes);

            LogUtils.d("pictureSize", "推流分辨率  =  " + publishSize.getWidth() + " * " + publishSize.getHeight());
            LogUtils.d("pictureSize", "预览分辨率  =  " + previewSize.getWidth() + " * " + previewSize.getHeight());

            //计算比例(需对调宽高)
//            tcpSend.setWeight((double) publishSize.getHeight() / publishSize.getWidth());
//            if (map.isPreview()) {
////              设置预览界面的大小
//                map.getPublishView().setWeight((double) previewSize.getHeight() / previewSize.getWidth());
//            }

            recordEncoderVD = new RecordEncoderVD(previewSize, map.getFrameRate(), map.getCollectionBitrate(), writeMp4, map.getCodetype());
//            vdEncoder = new VDEncoder(previewSize, publishSize, map.getFrameRate(), map.getPublishBitrate(), map.getCodetype());
            vdEncoder = new VDEncoder(previewSize, publishSize, map.getFrameRate(), map.getPublishBitrate(), map.getCodetype());
            //  初始化音频编码
//            voiceRecord = new VoiceRecord(map.getCollectionbitrate_vc(), map.getPublishbitrate_vc(), writeMp4);
//            voiceRecord = new VoiceRecord(map.getCollectionbitrate_vc(), map.getPublishbitrate_vc(), writeMp4, udpSend);
//            vdEncoder.start();
//            voiceRecord.start();
        }
    }

//    private void initCode(Size[] outputSizes) {
//        if (vdEncoder == null) {
//            publishSize = initSize(publishSize, outputSizes);
//            previewSize = initSize(previewSize, outputSizes);
//
//            Log.d("pictureSize", "推流分辨率  =  " + publishSize.getWidth() + " * " + publishSize.getHeight());
//            Log.d("pictureSize", "预览分辨率  =  " + previewSize.getWidth() + " * " + previewSize.getHeight());
//
//            //计算比例(需对调宽高)
////            tcpSend.setWeight((double) publishSize.getHeight() / publishSize.getWidth());
//            if (map.isPreview()) {
//                //  设置预览界面的大小
////                map.getPublishView().setWeight((double) previewSize.getHeight() / previewSize.getWidth());
//            }
//
//            recordEncoderVD = new RecordEncoderVD(previewSize, map.getFrameRate(), map.getCollectionBitrate(), writeMp4, map.getCodetype());
//            vdEncoder = new VDEncoder(previewSize, publishSize, map.getFrameRate(), map.getPublishBitrate(), map.getCodetype(), tcpSend);
//            //初始化音频编码
//            voiceRecord = new VoiceRecord(map.getCollectionbitrate_vc(), map.getPublishbitrate_vc(), writeMp4, udpSend);
//            vdEncoder.start();
//            voiceRecord.start();
//        }
//    }

    private Size initSize(Size publishSize, Size[] outputSizes) {
        int numw = 10000;
        int numh = 10000;
        int num = 0;
        for (int i = 0; i < outputSizes.length; i++) {
            Log.d("Size_app", outputSizes[i].getWidth() + "--" + outputSizes[i].getHeight());
            if (Math.abs(outputSizes[i].getWidth() - publishSize.getWidth()) <= numw) {
                numw = Math.abs(outputSizes[i].getWidth() - publishSize.getWidth());
                if (Math.abs(outputSizes[i].getHeight() - publishSize.getHeight()) <= numh) {
                    numh = Math.abs(outputSizes[i].getHeight() - publishSize.getHeight());
                    num = i;
                }
            }
        }
        return outputSizes[num];
    }


    /**
     * 打开相机
     */
    private void openCamera() {
        if (isCameraBegin) {
            return;
        }
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        try {
            //  打开相机
            LogUtils.e("cameraId -------------:" + cameraId);
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened( CameraDevice device) {
                    cameraDevice = device;
                    //开启预览
                    startPreview();
                    //  初始化画面大小调节
                    initCharacteristics() ;
                }

                @Override
                public void onDisconnected( CameraDevice cameraDevice) {
                }

                @Override
                public void onError( CameraDevice cameraDevice, int i) {
                }
            }, camearHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void startPreview() {
        try {
            List<Surface> surfaces = new ArrayList<>();
            //预览数据输出
            final CaptureRequest.Builder previewRequestBuilder = getPreviewBuilder();
//            final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
            //  固定曝光度
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,6);
            //  设置自动曝光帧率范围
            previewBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, getRange());
            //  自动对焦
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //  对焦触发器设置为空闲状态
            previewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            Surface previewSurface = getPreviewImageReaderSurface();
            previewRequestBuilder.addTarget(previewSurface);
            surfaces.add(previewSurface);

//              预览
            if (map.isPreview()) {
                Surface textureSurface = getTextureSurface();
                previewRequestBuilder.addTarget(textureSurface);
                surfaces.add(textureSurface);
            }

            //  拍照数据输出
            if (map.getScreenshotsMode() == TAKEPHOTO) {
                final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//                final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, captureRequestBuilder.get(CaptureRequest.CONTROL_AF_MODE));
//                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, captureRequestBuilder.get(CaptureRequest.CONTROL_AE_MODE));
//                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, captureRequestBuilder.get(CaptureRequest.FLASH_MODE));
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotateAngle);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    Surface pictureSurface = getPictureImageReaderSurface();
                    captureRequestBuilder.addTarget(pictureSurface);
                    surfaces.add(pictureSurface);
                }


                captureRequest = captureRequestBuilder.build();
            }


            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表(同时输出屏幕，输出预览，拍照)，
            // 第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，
            // 第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
//            cameraDevice.createCaptureSession(surfaces, sessionStateCb, camearHandler);
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured( CameraCaptureSession session) {
                    Publish.this.session = session;
                    //  设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                    try {
                        session.setRepeatingRequest(previewRequestBuilder.build(), null, camearHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    isCameraBegin = true;
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, camearHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Surface surface ;
    /**
     * 获取textureView的Surface
     */
    private Surface getTextureSurface() {
        if (surface == null) {
            SurfaceTexture mSurfaceTexture = map.getPublishView().getSurfaceTexture();
            mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());//设置TextureView的缓冲区大小
            surface = new Surface(mSurfaceTexture) ;
        }
        return surface ;
    }
//    private Surface getTextureSurface() {
//        SurfaceTexture mSurfaceTexture = map.getPublishView().getSurfaceTexture();
//        mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());//设置TextureView的缓冲区大小
//        return new Surface(mSurfaceTexture);
//    }

    /**
     * 创建预览ImageReader回调监听（在这里获取每一帧数据）并返回Surface
     */
    private Surface getPreviewImageReaderSurface() {
        //最后一个参数代表每次最多获取几帧数据
        previewImageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, frameMax);
        //监听ImageReader的事件，它的参数就是预览帧数据，可以对这帧数据进行处理,类似于Camera1的PreviewCallback回调的预览帧数据
        previewImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (isCameraBegin) {
                    if (frameRateControlQueue.size() >= (frameMax - 1)) {
                        //超出限制丢弃
                        frameRateControlQueue.poll().close();
                    }
                    frameRateControlQueue.offer(reader.acquireNextImage());
                } else {
                    reader.acquireNextImage().close();
                }
            }
        }, camearHandler);
        return previewImageReader.getSurface();
    }

    private void starFrameControl() {
        controlFrameRateThread = new HandlerThread("FrameRateControl");
        controlFrameRateThread.start();
        frameHandler = new Handler(controlFrameRateThread.getLooper());
        frameHandler.post(new Runnable() {
            @Override
            public void run() {
                //  帧率控制时间
                frameHandler.postDelayed(this, 1000 / map.getFrameRate());
                if (!frameRateControlQueue.isEmpty()) {
                    //  耗时检测
//                    long time = System.currentTimeMillis();
                    Image image = frameRateControlQueue.poll();
                    //  YUV_420_888先转成I420
                    byte[] i420 = ImagUtil.YUV420888toI420(image);
                    image.close();
                    byte[] input = new byte[i420.length];
                    //  旋转I420(270需要镜像)然后交给编码器等待编码
                    ImagUtil.rotateI420(i420, previewSize.getWidth(), previewSize.getHeight(), input, rotateAngle, map.isRotate());
//                    LogUtils.e("getWidth:"+previewSize.getWidth()+
//                            ",previewSize.getHeight():"+previewSize.getHeight()+
//                            ",rotateAngle:"+rotateAngle+
//                            ",isRotate:"+map.isRotate()) ;
                    if (useuvPicture && yuvPicture == null) {
                        useuvPicture = false;
                        yuvPicture = Arrays.copyOf(input, input.length);
                        camearHandler.post(pictureRunnable);
                    }

                    if (recordEncoderVD !=null)
                        //  录制编码器
                        recordEncoderVD.addFrame(input);
                    if (vdEncoder != null )
                        //  推流编码器
                        vdEncoder.addFrame(input);


//                    //录制编码器
//                    recordEncoderVD.addFrame(input);
//                    //推流编码器
//                    vdEncoder.addFrame(input);


//                    if ((System.currentTimeMillis() - time) > (1000 / map.getFrameRate())) {
//                        Log.e("Frame_loss", "图像处理速度过慢");
////                        Log.d("Frame_slow", "图像处理速度过慢");
//                    }
                } else {
                    Log.d("Frame_loss", "图像采集速率不够");
                }
            }
        });
    }

    /**
     * 创建拍照ImageReader回调监听（在这里获取拍照数据）并返回Surface
     */
    private Surface getPictureImageReaderSurface() {
        pictureImageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, frameMax);
        pictureImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                image.close();
                saveImage(bytes);
            }
        }, camearHandler);
        return pictureImageReader.getSurface();
    }

    private void saveImage(byte[] bytes) {
        FileUtils.createDirFile(map.getPicturedirpath());
        BufferedOutputStream output;
        String path = map.getPicturedirpath() + File.separator + System.currentTimeMillis() + ".jpg";
        try {
            output = new BufferedOutputStream(new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (map.getScreenshotsMode() == CONVERSION) {
            byte[] picture = new byte[bytes.length];
            ImagUtil.yuvI420ToNV21(bytes, picture, previewSize.getHeight(), previewSize.getWidth());
//            ImagUtil.yuvI420ToNV21(bytes, picture, previewSize.getWidth(), previewSize.getHeight());
            YuvImage yuvImage = new YuvImage(picture, ImageFormat.NV21, previewSize.getHeight(), previewSize.getWidth(), null);
//            YuvImage yuvImage = new YuvImage(picture, ImageFormat.NV21, previewSize.getWidth(), previewSize.getHeight(), null);
            yuvImage.compressToJpeg(new Rect(0, 0, previewSize.getHeight(), previewSize.getWidth()), 100, output);
//            yuvImage.compressToJpeg(new Rect(0, 0, previewSize.getWidth(), previewSize.getHeight()), 100, output);

        } else if (map.getScreenshotsMode() == TAKEPHOTO) {
            if (map.isRotate()) {
                Bitmap newBitmap = mirror(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                newBitmap.recycle();
            } else {
                try {
                    output.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        OtherUtil.close(output);
        if (pictureCallback != null) {
            pictureCallback.Success(path);
        }
    }

    private Bitmap mirror(Bitmap temp) {
        Matrix m = new Matrix();
        m.postScale(-1, 1);   //镜像水平翻转
//        m.postScale(1, -1);   //镜像垂直翻转
//        m.postRotate(-90);  //旋转-90度
        Bitmap newBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), m, true);
        temp.recycle();
        return newBitmap;
    }

    private void releaseCamera() {
        isCameraBegin = false;
        while (!frameRateControlQueue.isEmpty()) {
            frameRateControlQueue.poll().close();
        }
        //释放相机各种资源
        if (session != null) {
            session.close();
            cameraDevice.close();
            previewImageReader.close();
            session = null;
            cameraDevice = null;
            previewImageReader = null;

        }
        if (pictureImageReader != null) {
            pictureImageReader.close();
            pictureImageReader = null;
        }
    }

    public void startRecode() {
        if (voiceRecord == null){
            voiceRecord = new VoiceRecord(map.getCollectionbitrate_vc(), map.getPublishbitrate_vc(), writeMp4, udpSend);

        }
        voiceRecord.start();
        voiceRecord.startRecode();
        if (recordEncoderVD == null || writeMp4 == null){
            LogUtils.d("recordEncoderVD == null || writeMp4 == null");
            return;
        }
        recordEncoderVD.start();
        writeMp4.start();
    }

    public void stopRecode() {
        if (voiceRecord == null || recordEncoderVD == null|| writeMp4 == null){
            LogUtils.d("voiceRecord == null || recordEncoderVD == null|| writeMp4 == null");
            return;
        }
        voiceRecord.stopRecode();
        recordEncoderVD.stop();
        writeMp4.stop();
    }

    //  旋转
    public void rotate() {
        if (isCameraBegin) {
            releaseCamera();
            map.setRotate(!map.isRotate());
            initCamera();
            openCamera();
            Rotate3dAnimation.rotate3dDegrees180(map.getPublishView(), 700, 500, Rotate3dAnimation.ROTATE_Y_AXIS);
        }
    }

    //  不能随意释放
    public void release(){
        if (session != null) {
            session.close();
            session = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (previewImageReader != null) {
            previewImageReader.close();
            previewImageReader = null;
        }

    }

    public void open(){
        openCamera();
    }


    public void takePicture() {
        if (map.getScreenshotsMode() == CONVERSION) {
            useuvPicture = true;

        } else if (map.getScreenshotsMode() == TAKEPHOTO) {
            if (session != null) {
                try {
                    session.capture(captureRequest, null, camearHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] yuvPicture;

    private Runnable pictureRunnable = new Runnable() {
        @Override
        public void run() {
            saveImage(yuvPicture);
            yuvPicture = null;
        }
    };

    public void initTcp(String phone, String ip, int port, int channelNum, boolean isVoice){
//        Integer.toBinaryString()
        this.tcpSend = new TcpSend(phone, ip, port, channelNum) ;
        vdEncoder.setTcpSend(tcpSend);
//        recordEncoderVD = new RecordEncoderVD(previewSize, map.getFrameRate(), map.getCollectionBitrate(), writeMp4, map.getCodetype());
//        vdEncoder = new VDEncoder(previewSize, publishSize, map.getFrameRate(), map.getPublishBitrate(), map.getCodetype(), tcpSend);
//        //初始化音频编码
//        voiceRecord = new VoiceRecord(map.getCollectionbitrate_vc(), map.getPublishbitrate_vc(), writeMp4, udpSend);
        vdEncoder.start();
        if (isVoice){
            if (voiceRecord == null){
                voiceRecord = new VoiceRecord(map.getCollectionbitrate_vc(), map.getPublishbitrate_vc(), writeMp4, udpSend);
            }
            voiceRecord.start();
        }
    }

    public void start() {
        tcpSend.startsend();
        LogUtils.d("startsend -------------------------------");
//        getMatchingSize2() ;
    }

    public void stop() {
        try {
            tcpSend.stopsend();
            tcpSend.closeTCPSocket();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void destroy() {
        releaseCamera();
        if (recordEncoderVD != null)
            recordEncoderVD.destroy();
        if (vdEncoder != null)
            vdEncoder.destroy();
        if (voiceRecord != null)
            voiceRecord.destroy();
        if (tcpSend != null)
            tcpSend.destroy();
        if (frameHandler != null)
            frameHandler.removeCallbacksAndMessages(null);
        if (controlFrameRateThread != null)
            controlFrameRateThread.quitSafely();
        if (camearHandler != null)
            camearHandler.removeCallbacksAndMessages(null);
        if (handlerCamearThread != null)
            handlerCamearThread.quitSafely();
        if (writeMp4 != null)
            writeMp4.destroy();
        pictureCallback = null;
    }

// -----------------------------------------------------------------------------------

    private void initCharacteristics() {
//        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
//        assert manager != null;
//            characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        assert characteristics != null ;
        maximumZoomLevel = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
    }



    //  Zooming 当前数值
    protected float fingerSpacing = 0;
    //  每次放大的等级
    protected float zoomLevel = 1f;
    //  缩放点
    protected Rect zoom;
    /** 放大缩小
     * @param currentFingerSpacing  控制放大缩小的数值,随意大小,只需要保证放大时数值比缩小数值大
     */
    public void updateZoom(float currentFingerSpacing){

        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (rect == null) return ;
        CaptureRequest.Builder builder = null;
//        try {
            builder = getPreviewBuilder();
            float delta = 0.05f; // Control this value to control the zooming sensibility
            if (fingerSpacing != 0) {
                if (currentFingerSpacing > fingerSpacing) { //  Don't over zoom-in
                    if ((maximumZoomLevel - zoomLevel) <= delta) {
                        delta = maximumZoomLevel - zoomLevel;
                    }
                    zoomLevel = zoomLevel + delta;
                } else if (currentFingerSpacing < fingerSpacing) { //   Don't over zoom-out
                    if ((zoomLevel - delta) < 1f) {
                        delta = zoomLevel - 1f;
                    }
                    zoomLevel = zoomLevel - delta;
                }
                float ratio = (float) 1 / zoomLevel; // This ratio is the ratio of cropped Rect to Camera's original(Maximum) Rect
                //  croppedWidth and croppedHeight are the pixels cropped away, not pixels after cropped
                int croppedWidth = rect.width() - Math.round((float) rect.width() * ratio);
                int croppedHeight = rect.height() - Math.round((float) rect.height() * ratio);
                //  Finally, zoom represents the zoomed visible area
                zoom = new Rect(croppedWidth / 2, croppedHeight / 2,
                        rect.width() - croppedWidth / 2, rect.height() - croppedHeight / 2);
                builder.set(CaptureRequest.SCALER_CROP_REGION, zoom);

                LogUtils.d( "zoom:"+zoom+",currentFingerSpacing:"+currentFingerSpacing+",max:"+maximumZoomLevel) ;
            }
//        }
//        catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
        fingerSpacing = currentFingerSpacing;
//        sendRepeatingRequest(builder.build(), mPreviewCallback, mMainHandler);
        try {
            assert  session != null;
            session.setRepeatingRequest(builder.build(), null, camearHandler);
        } catch (CameraAccessException | IllegalArgumentException e) {
            LogUtils.d("CameraAccessException ------------------"+e.getMessage());
            e.printStackTrace();
        }
//        }
    }

    /**
     * 开启闪光灯
     * */
    public void flashMode(String value){
        CaptureRequest.Builder builder = getPreviewBuilder() ;
        switch (value) {
            case Constants.CameraSettings.FLASH_VALUE_ON:
                builder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                break;
            case Constants.CameraSettings.FLASH_VALUE_OFF:
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                break;
            case Constants.CameraSettings.FLASH_VALUE_AUTO:
                builder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                break;
            case Constants.CameraSettings.FLASH_VALUE_TORCH:
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                break;
            default:
                LogUtils.e("error value for flash mode");
                break;
        }

        builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
//        builder.build();
        try {
            assert  session != null;
            session.setRepeatingRequest(builder.build(), null, camearHandler);
        } catch (CameraAccessException | IllegalArgumentException e) {
            LogUtils.d("CameraAccessException ---------2---------"+e.getMessage());
            e.printStackTrace();
        }
    }

    private CaptureRequest.Builder previewBuilder ;
//    private CaptureRequest.Builder getPreviewBuilder() throws CameraAccessException {
//        if (previewBuilder == null && map.isPreview()){
////            previewBuilder = createBuilder(CameraDevice.TEMPLATE_PREVIEW, getTextureSurface()) ;
//            previewBuilder = createBuilder(CameraDevice.TEMPLATE_RECORD, getTextureSurface()) ;
//        }else {
//            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//        }
//        return previewBuilder;
//    }

    private CaptureRequest.Builder getPreviewBuilder() {
        if (previewBuilder ==null && map.isPreview()){
            previewBuilder = createBuilder(CameraDevice.TEMPLATE_STILL_CAPTURE, getTextureSurface()) ;
//            previewBuilder = createBuilder(CameraDevice.TEMPLATE_RECORD, getTextureSurface()) ;
        }else {
            try {
                previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE) ;
//                previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD) ;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return previewBuilder;
    }


    CaptureRequest.Builder createBuilder(int type, Surface surface) {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(type);
            builder.addTarget(surface);
            return builder;
        } catch (CameraAccessException | IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Range<Integer> getRange() {
////        CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
//        CameraCharacteristics chars = null;
//        try {
//            chars = manager.getCameraCharacteristics(cameraId);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
        Range<Integer>[] ranges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

        Range<Integer> result = null;

        for (Range<Integer> range : ranges) {
            //帧率不能太低，大于10
            if (range.getLower()<10)
                continue;
            if (result==null)
                result = range;
                //FPS下限小于15，弱光时能保证足够曝光时间，提高亮度。range范围跨度越大越好，光源足够时FPS较高，预览更流畅，光源不够时FPS较低，亮度更好。
            else if (range.getLower()<=15 && (range.getUpper()-range.getLower())>(result.getUpper()-result.getLower()))
                result = range;
        }
        return result;
    }










    public int getPublishStatus() {
        return tcpSend.getPublishStatus();
    }

    public int getRecodeStatus() {
        return writeMp4.getRecodeStatus();
    }

    public void setPictureCallback(PictureCallback pictureCallback) {
        this.pictureCallback = pictureCallback;
    }

    public void setWriteFileCallback(WriteFileCallback writeFileCallback) {
        writeMp4.setWriteFileCallback(writeFileCallback);
    }

    public static class Buider {
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

    private static class ParameterMap {
        private PublishView publishView;
        private int screenshotsMode = TAKEPHOTO;
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

        private PublishView getPublishView() {
            return publishView;
        }

        private void setPublishView(PublishView publishView) {
            this.publishView = publishView;
        }

        private int getScreenshotsMode() {
            return screenshotsMode;
        }

        private void setScreenshotsMode(int screenshotsMode) {
            this.screenshotsMode = screenshotsMode;
        }

        private int getFrameRate() {
            return frameRate;
        }

        private void setFrameRate(int frameRate) {
            this.frameRate = frameRate;
        }

        private int getPublishBitrate() {
            return publishBitrate;
        }

        private void setPublishBitrate(int publishBitrate) {
            this.publishBitrate = publishBitrate;
        }

        private int getCollectionBitrate() {
            return collectionBitrate;
        }

        private void setCollectionBitrate(int collectionBitrate) {
            this.collectionBitrate = collectionBitrate;
        }

        private int getCollectionbitrate_vc() {
            return collectionbitrate_vc;
        }

        private void setCollectionbitrate_vc(int collectionbitrate_vc) {
            this.collectionbitrate_vc = collectionbitrate_vc;
        }

        private int getPublishbitrate_vc() {
            return publishbitrate_vc;
        }

        private void setPublishbitrate_vc(int publishbitrate_vc) {
            this.publishbitrate_vc = publishbitrate_vc;
        }

        private Size getPublishSize() {
            return publishSize;
        }

        private void setPublishSize(Size publishSize) {
            this.publishSize = publishSize;
        }

        private Size getPreviewSize() {
            return previewSize;
        }

        private void setPreviewSize(Size previewSize) {
            this.previewSize = previewSize;
        }

        private boolean isRotate() {
            return rotate;
        }

        private void setRotate(boolean rotate) {
            this.rotate = rotate;
        }

        private boolean isPreview() {
            return isPreview;
        }

        private void setPreview(boolean preview) {
            isPreview = preview;
        }

        private String getCodetype() {
            return codetype;
        }

        private void setCodetype(String codetype) {
            this.codetype = codetype;
        }

        private String getPicturedirpath() {
            return picturedirpath;
        }

        private void setPicturedirpath(String picturedirpath) {
            this.picturedirpath = picturedirpath;
        }

        //        private UdpSend getPushMode() {
//            return pushMode;
//        }
        private TcpSend getPushMode() {
            return tPush;
        }
        private UdpSend getUPushMode() {
            return uPush;
        }

        //        private void setPushMode(UdpSend pushMode) {
//            this.pushMode = pushMode;
//        }
        private void setPushMode(TcpSend tPush) {
            this.tPush = tPush;
        }
        private void setPushMode(UdpSend uPush) {
            this.uPush = uPush;
        }

        private String getVideodirpath() {
            return videodirpath;
        }

        private void setVideodirpath(String videodirpath) {
            this.videodirpath = videodirpath;
        }
    }













    private Size getMatchingSize2(){
        Size selectSize = null;

        LogUtils.d("getMatchingSize2 -------------------------------");
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
            DisplayMetrics displayMetrics = ApplicationProvider.context.getResources().getDisplayMetrics(); //因为我这里是将预览铺满屏幕,所以直接获取屏幕分辨率
            int deviceWidth = displayMetrics.widthPixels; //屏幕分辨率宽
            int deviceHeigh = displayMetrics.heightPixels; //屏幕分辨率高
            LogUtils.e("getMatchingSize2: 屏幕密度宽度="+deviceWidth);
            LogUtils.e("getMatchingSize2: 屏幕密度高度="+deviceHeigh );
            /**
             * 循环40次,让宽度范围从最小逐步增加,找到最符合屏幕宽度的分辨率,
             * 你要是不放心那就增加循环,肯定会找到一个分辨率,不会出现此方法返回一个null的Size的情况
             * ,但是循环越大后获取的分辨率就越不匹配
             */
            for (int j = 1; j < 41; j++) {
                for (int i = 0; i < sizes.length; i++) { //遍历所有Size
                    Size itemSize = sizes[i];
                    LogUtils.e("当前itemSize 宽="+itemSize.getWidth()+"高="+itemSize.getHeight());
                    //判断当前Size高度小于屏幕宽度+j*5  &&  判断当前Size高度大于屏幕宽度-j*5
                    if (itemSize.getHeight() < (deviceWidth + j*5) && itemSize.getHeight() > (deviceWidth - j*5)) {
                        if (selectSize != null){ //如果之前已经找到一个匹配的宽度
                            if (Math.abs(deviceHeigh-itemSize.getWidth()) < Math.abs(deviceHeigh - selectSize.getWidth())){ //求绝对值算出最接近设备高度的尺寸
                                selectSize = itemSize;
                                continue;
                            }
                        }else {
                            selectSize = itemSize;
                        }

                    }
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        LogUtils.e("getMatchingSize2: 选择的分辨率宽度="+selectSize.getWidth());
        LogUtils.e("getMatchingSize2: 选择的分辨率高度="+selectSize.getHeight());
        return selectSize;
    }
}
