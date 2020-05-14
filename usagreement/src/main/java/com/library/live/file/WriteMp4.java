package com.library.live.file;

import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.library.common.WriteFileCallback;
import com.library.util.BitmapUtils;
import com.library.util.FileUtils;
import com.library.util.MediaFunc;
import com.library.util.OtherUtil;
import com.library.util.RegularUtils;
import com.library.util.Storage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import cn.com.erayton.usagreement.ApplicationProvider;
import cn.com.erayton.usagreement.data.db.DbTools;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 视频录制与保存
 */

public class WriteMp4 {
    //  录制开始时间
    private static String startTime ;
    //  录制结束时间
    private static String endTime ;

    private final String format="%s_%s_%d_%d_%d" ;
    private int channel = 0;


    public static final int RECODE_STATUS_START = 0;
    public static final int RECODE_STATUS_STOP = 1;
    public static final int RECODE_STATUS_READY = 2;
    private int RECODE_STATUS = RECODE_STATUS_STOP;

    private MediaMuxer mMediaMuxer = null;
    public static final int video = 0;
    public static final int voice = 1;

    private String dirpath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Video";
    private String path = null;
    private MediaFormat videoFormat = null;
    private MediaFormat voiceFormat = null;

    private int videoTrackIndex;
    private int voiceTrackIndex;
    private long presentationTimeUsVD = 0;
    private long presentationTimeUsVE = 0;

    private boolean isShouldStart = false;
    private int frameNum = 0;
    private final Object lock = new Object();
    private WriteFileCallback writeFileCallback = new WriteFileCallback() {
        @Override
        public void success(String name) {
            Log.e("cjh", "fileName"+name) ;
            //  成功之后返回文件名
            String fileName = RegularUtils.getOneResult(name, RegularUtils.videoRex);
            //  处理文件名
            String[] names = fileName.split("_") ;
            File file = new File(name) ;

            BitmapUtils.getVideoThumbnail(name) ;

            DbTools.insertVideoRecord(name, Long.parseLong(names[0]),  Long.parseLong(names[1]),
                    Integer.parseInt(names[2]), file.length());
            Storage.addVideoToDB(ApplicationProvider.context.getContentResolver(), file.getName(),
                    System.currentTimeMillis(), null, file.length(), name,
                    200, 200, FileUtils.getMimeType(MediaFunc.MEDIA_TYPE_VIDEO));
        }

        @Override
        public void failure(String err) {
            LogUtils.e("cjh", "failure"+err) ;
        }
    };

    public WriteMp4(String dirpath) {
        if (!TextUtils.isEmpty(dirpath) && !dirpath.equals("")) {
            this.dirpath = dirpath;
        }
    }

    public void addTrack(MediaFormat mediaFormat, int flag) {
        if (flag == video) {
            videoFormat = mediaFormat;
        } else if (flag == voice) {
            voiceFormat = mediaFormat;
        }
        if (videoFormat != null && voiceFormat != null) {
            if (isShouldStart) {
                start();
            }
        }
    }


    public void start() {
        LogUtils.d("start ----------------------------");
        startTime = BitOperator.getInstance().getNowBCDTimeString() ;
        RECODE_STATUS = RECODE_STATUS_READY;
        synchronized (lock) {

            LogUtils.d("start -----------lock-----------------");
            LogUtils.d("start -----------lock------------(voiceFormat != null)-----"+(voiceFormat != null));
            LogUtils.d("start -----------lock------------(videoFormat != null)-----"+(videoFormat != null));
            LogUtils.d("start -----------lock------------(mMediaMuxer == null)-----"+(mMediaMuxer == null));
            if (voiceFormat != null && videoFormat != null && mMediaMuxer == null) {

                LogUtils.d("start -------lock------------voiceFormat != null && videoFormat != null && mMediaMuxer == null---------");
                isShouldStart = false;
                setPath();
                try {
                    mMediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    videoTrackIndex = mMediaMuxer.addTrack(videoFormat);
                    voiceTrackIndex = mMediaMuxer.addTrack(voiceFormat);
                    mMediaMuxer.start();
                    presentationTimeUsVE = 0;
                    presentationTimeUsVD = 0;
                    frameNum = 0;
                    RECODE_STATUS = RECODE_STATUS_START;
                    Log.d("app_WriteMp4", "文件录制启动");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isShouldStart = true;
            }
        }
    }

    public void write(int flag, ByteBuffer outputBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (RECODE_STATUS == RECODE_STATUS_START) {
            if (flag == video) {
                if (bufferInfo.presentationTimeUs > presentationTimeUsVD) {//容错
                    presentationTimeUsVD = bufferInfo.presentationTimeUs;
                    if (frameNum == 0) {//视频帧第一帧必须为关键帧
                        if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                            mMediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo);
                            frameNum++;
                        }
                    } else {
                        mMediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo);
                        frameNum++;
                    }
                }
            } else if (flag == voice) {
                if (bufferInfo.presentationTimeUs > presentationTimeUsVE) {//容错
                    presentationTimeUsVE = bufferInfo.presentationTimeUs;
                    mMediaMuxer.writeSampleData(voiceTrackIndex, outputBuffer, bufferInfo);
                }
            }
        }
    }

    private void setPath() {
        LogUtils.d("setPath ----------------------------");
        FileUtils.createDirFile(dirpath);
//        //  文件名规则， 开始时间(YYMMDDHHmmss)_通道号_资源类型(音视频,音频,视频,)_码流类型(主,子码流)
//        String format="%s_%d_%d_%d" ;
        //  文件名规则， 开始时间(YYMMDDHHmmss)_结束时间(YYMMDDHHmmss)_通道号_资源类型(音视频,音频,视频,)_码流类型(主,子码流)
//        String format="%s_%s_%d_%d_%d" ;
//        int channel = 0;
//        path = dirpath + File.separator + String.format(format, startTime, channel, 0, 0) + ".mp4";
//        path = dirpath + File.separator + String.format(format, startTime, "%s", channel, 0, 0) + ".mp4";
        path = dirpath + File.separator + startTime+".mp4";
    }

    public void stop() {
        LogUtils.d("stop ----------------------------");
        synchronized (lock) {
            LogUtils.d("stop ----------------lock------------");
            if (RECODE_STATUS == RECODE_STATUS_START) {
                LogUtils.d("stop ------------RECODE_STATUS == RECODE_STATUS_START----------------"+(RECODE_STATUS == RECODE_STATUS_START));
                endTime = BitOperator.getInstance().getNowBCDTimeString() ;
                boolean iscatch = false;
                try {
                    mMediaMuxer.release();
                    if (frameNum >= 20) {
                        path = FileUtils.FixFileName(path, String.format(format, startTime, endTime, channel, 0, 0)) ;
                        writeFileCallback.success(path);
                    }
                    LogUtils.d("app_WriteMp4", "文件录制关闭");
                } catch (Exception e) {
                    iscatch = true;
                } finally {
                    mMediaMuxer = null;
                    voiceFormat = null;
                    videoFormat = null;
                    //文件过短或异常，删除文件
                    File file = new File(path);
                    if (file.exists()) {
                        if (iscatch) {
                            file.delete();
                            writeFileCallback.failure("文件录制失败");
                        } else if (frameNum < 20) {
                            file.delete();
                            writeFileCallback.failure("文件过短");
                        }
                    } else {
                        writeFileCallback.failure("文件录制失败");
                    }
                }
            } else if (RECODE_STATUS == RECODE_STATUS_READY) {
                isShouldStart = false;
                writeFileCallback.failure("文件录制被取消");
            }
            RECODE_STATUS = RECODE_STATUS_STOP;
        }
    }

    public void destroy() {
        stop();
        writeFileCallback = null;
    }

    public int getRecodeStatus() {
        return RECODE_STATUS;
    }

    public void setWriteFileCallback(WriteFileCallback writeFileCallback) {
        this.writeFileCallback = writeFileCallback;
    }
}
