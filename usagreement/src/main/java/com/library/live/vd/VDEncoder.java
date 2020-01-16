package com.library.live.vd;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Size;

import com.library.live.stream.UdpSend;
import com.library.util.ByteUtil;
import com.library.util.ImagUtil;
import com.library.util.OtherUtil;
import com.library.util.SingleThreadExecutor;
import com.library.util.mLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;


public class VDEncoder {
    public static final String H264 = MediaFormat.MIMETYPE_VIDEO_AVC;
    public static final String H265 = MediaFormat.MIMETYPE_VIDEO_HEVC;
    private MediaCodec mediaCodec;
    private UdpSend udpSend;

    //编码参数
    private byte[] information;
    private boolean isRuning = false;
    private ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<>(OtherUtil.QueueNum);

    private int cWidth;
    private int cHeight;
    private int pWidth;
    private int pHeight;
    private int COLOR_FORMAT;
    private SingleThreadExecutor singleThreadExecutor;

    public VDEncoder(Size csize, Size psize, int framerate, int publishBitrate, String codetype, UdpSend udpSend) {
        //UPD实例
        this.udpSend = udpSend;
        //由于图片旋转过，所以高度宽度需要对调
        cWidth = csize.getHeight();
        cHeight = csize.getWidth();
        pWidth = psize.getHeight();
        pHeight = psize.getWidth();

        try {
            mediaCodec = MediaCodec.createEncoderByType(codetype);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(codetype, pWidth, pHeight);
        COLOR_FORMAT = ImagUtil.showSupportedColorFormat(mediaCodec, codetype);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_FORMAT);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, publishBitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
        singleThreadExecutor = new SingleThreadExecutor();
    }

    public void destroy() {
        isRuning = false;
        mediaCodec.release();
        mediaCodec = null;
        singleThreadExecutor.shutdownNow();
    }

    /*
    视频数据队列，等待编码，视频数据处理比较耗时，所以存放队列另起线程等待编码
     */
    public void addFrame(byte[] bytes) {
        if (isRuning) {
            OtherUtil.addQueue(YUVQueue, bytes);
        }
    }

    public void start() {
        YUVQueue.clear();
        isRuning = true;
        startEncoderThread();
    }

    private void startEncoderThread() {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[pWidth * pHeight * 3 / 2];
                byte[] input = new byte[pWidth * pHeight * 3 / 2];
                byte[] outData;
                byte[] take;
                //  是否需要缩放
                boolean isScale = (cWidth != pWidth) || (cHeight != pHeight);
                ByteBuffer outputBuffer;
                int outputBufferIndex;
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                while (isRuning) {
                    try {
                        take = YUVQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (isScale) {
                        ImagUtil.scaleI420(take, cWidth, cHeight, data, pWidth, pHeight, 0);
                    } else {
                        data = take;
                    }
                    if (COLOR_FORMAT == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                        input = data;
                    } else {
                        ImagUtil.yuvI420ToNV12(data, input, pWidth, pHeight);
                    }
                    try {
                        int inputBufferIndex = mediaCodec.dequeueInputBuffer(OtherUtil.waitTime);
                        if (inputBufferIndex >= 0) {
                            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                            inputBuffer.clear();
                            inputBuffer.put(input);
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, OtherUtil.getFPS(), 0);
                        }

                        outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, OtherUtil.waitTime);
                        while (outputBufferIndex >= 0) {
                            //  源流
                            outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);

                            if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                                //  sps pps 信息
                                outData = new byte[bufferInfo.size];
                                outputBuffer.get(outData);
                                information = outData;
                                mLog.log("publish_sps_pps", ByteUtil.byte_to_16(information));
                            } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                                //  关键帧
                                outData = new byte[bufferInfo.size + information.length];
                                System.arraycopy(information, 0, outData, 0, information.length);
                                outputBuffer.get(outData, information.length, bufferInfo.size);
                                //  交给发送器等待发送
                                udpSend.addVideo(outData, 0);
                            } else {
                                //  普通帧
                                //  添加将要发送的视频数据
                                outData = new byte[bufferInfo.size];
                                outputBuffer.get(outData);
                                udpSend.addVideo(outData, 1);
                            }
                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, OtherUtil.waitTime);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


//
//    /***
//     *视频流分包
//     */
//    private void videoPackage(byte[] videoBytes, String dataType, long time) {
//        int bytesLength = videoBytes.length;//数据长度
//        int length = bytesLength / MAXBODYLENGTH;//整包个数
//        int remainder = bytesLength % MAXBODYLENGTH;//半包
//        byte[] bytes;//包体
//        if (bytesLength <= MAXBODYLENGTH) {//小于等于包长
//            getPack(dataType, "0000", time, videoBytes.length, videoBytes);
//            return;
//        }
//        //长度大于1
//        if (remainder != 0) {//含有半包
//            //多个完整包
//            for (int i = 0; i < length; i++) {
//                bytes = ByteUtil.subByte(videoBytes, MAXBODYLENGTH * i, MAXBODYLENGTH);
//                if (i == 0) {
//                    getPack(dataType, "0001", time, MAXBODYLENGTH, bytes);
//                } else {
//                    getPack(dataType, "0011", time, MAXBODYLENGTH, bytes);
//                }
//            }
//            bytes = ByteUtil.subByte(videoBytes, length * MAXBODYLENGTH, remainder);
//            getPack(dataType, "0010", time, remainder, bytes);
//        } else {//全部整包
//            for (int i = 0; i < length; i++) {
//                bytes = ByteUtil.subByte(videoBytes, MAXBODYLENGTH * i, MAXBODYLENGTH);
//                if (i == 0) {//第一个包
//                    getPack(dataType, "0001", time, MAXBODYLENGTH, bytes);
//                } else if (i == length - 1) {//最后一个包
//                    getPack(dataType, "0010", time, MAXBODYLENGTH, bytes);
//                } else {
//                    getPack(dataType, "0011", time, MAXBODYLENGTH, bytes);
//                }
//            }
//        }
//    }
}