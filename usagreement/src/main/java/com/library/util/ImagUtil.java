package com.library.util;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.com.erayton.usagreement.utils.LogUtils;

public class ImagUtil {

    static {
        System.loadLibrary("yuvutil");
    }

    /**
     * @param src      原始数据
     * @param width    原始的宽
     * @param height   原始的高
     * @param dst      输出数据
     * @param degree   旋转的角度，90，180和270三种
     * @param isMirror 是否镜像，一般只有270的时候才需要镜像
     */
    public static native void rotateI420(byte[] src, int width, int height, byte[] dst, int degree, boolean isMirror);

    /**
     * @param i420Src 原始I420数据
     * @param nv12Src 转化后的NV12数据
     * @param width   输出的宽
     * @param width   输出的高
     **/
    public static native void yuvI420ToNV12(byte[] i420Src, byte[] nv12Src, int width, int height);

    /**
     * @param i420Src 原始I420数据
     * @param nv21Src 转化后的NV21数据
     * @param width   输出的宽
     * @param width   输出的高
     **/
    public static native void yuvI420ToNV21(byte[] i420Src, byte[] nv21Src, int width, int height);

    /**
     * @param src        原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst        输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param mode       压缩模式。这里为0，1，2，3 速度由快到慢，质量由低到高，一般用0就好了，因为0的速度最快
     */
    public static native void scaleI420(byte[] src, int width, int height, byte[] dst, int dst_width, int dst_height, int mode);

    /**
     * yuv数据的裁剪操作
     *
     * @param src        原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst        输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param left       裁剪的x的开始位置，必须为偶数，否则显示会有问题
     * @param top        裁剪的y的开始位置，必须为偶数，否则显示会有问题
     **/
    public static native void cropYUV(byte[] src, int width, int height, byte[] dst, int dst_width, int dst_height, int left, int top);

    /**
     * YUV_420_888转换为I420
     */
    public static byte[] YUV420888toI420(Image image) {
        int width = image.getCropRect().width();
        int height = image.getCropRect().height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(image.getFormat()) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        int channelOffset = 0;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    break;
                case 1:
                    channelOffset = width * height;
                    break;
                case 2:
                    channelOffset = (int) (width * height * 1.25);
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (image.getCropRect().top >> shift) + pixelStride * (image.getCropRect().left >> shift));
            if (pixelStride == 1) {
                int length = w;
                for (int row = 0; row < h; row++) {
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                    if (row < h - 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                }
            } else {
                int length = (w - 1) * pixelStride + 1;
                for (int row = 0; row < h; row++) {
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset++;
                    }
                    if (row < h - 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                }
            }
        }
        return data;
    }

//    public static byte[] YUV420888toI420(Image image) {
//        int width = image.getCropRect().width();
//        int height = image.getCropRect().height();
//        Image.Plane[] planes = image.getPlanes();
//        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(image.getFormat()) / 8];
//        byte[] rowData = new byte[planes[0].getRowStride()];
//        int channelOffset = 0;
//        for (int i = 0; i < planes.length; i++) {
//            switch (i) {
//                case 0:
//                    channelOffset = 0;
//                    break;
//                case 1:
//                    channelOffset = width * height;
//                    break;
//                case 2:
//                    channelOffset = (int) (width * height * 1.25);
//                    break;
//            }
//            ByteBuffer buffer = planes[i].getBuffer();
//            int rowStride = planes[i].getRowStride();
//            int pixelStride = planes[i].getPixelStride();
//            int shift = (i == 0) ? 0 : 1;
//            int w = width >> shift;
//            int h = height >> shift;
//            buffer.position(rowStride * (image.getCropRect().top >> shift) + pixelStride * (image.getCropRect().left >> shift));
//            if (pixelStride == 1) {
//                int length = w;
//                for (int row = 0; row < h; row++) {
//                    buffer.get(data, channelOffset, length);
//                    channelOffset += length;
//                    if (row < h - 1) {
//                        buffer.position(buffer.position() + rowStride - length);
//                    }
//                }
//            } else {
//                int length = (w - 1) * pixelStride + 1;
//                for (int row = 0; row < h; row++) {
//                    buffer.get(rowData, 0, length);
//                    for (int col = 0; col < w; col++) {
//                        data[channelOffset] = rowData[col * pixelStride];
//                        channelOffset++;
//                    }
//                    if (row < h - 1) {
//                        buffer.position(buffer.position() + rowStride - length);
//                    }
//                }
//            }
//        }
//        return data;
//    }

    /**
     * 展示mediaCodec支持的YUV格式
     */
    public static int showSupportedColorFormat(MediaCodec mediaCodec, String codetype) {
        MediaCodecInfo.CodecCapabilities caps = mediaCodec.getCodecInfo().getCapabilitiesForType(codetype);

        //    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible; 2135033992  //一定支持
        //    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar; 19            //I420
        //    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar; 21        //NV12或者NV21

        ArrayList<Integer> list = new ArrayList<>();
        for (int colorFormat : caps.colorFormats) {
            list.add(colorFormat);
            LogUtils.d( "所有支持  " + colorFormat);
        }
        if (list.contains(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar)) {
            //I420
            LogUtils.d( "选择  COLOR_FormatYUV420Planar");
            return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
        } else if (list.contains(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)) {
            //NV12
            LogUtils.d( "选择  COLOR_FormatYUV420SemiPlanar");
            return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
        } else {
            //其他
            LogUtils.d( "选择  COLOR_FormatYUV420Flexible");
            return MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;
        }
    }


    public static byte[] I420ToNV21(byte[] input, int width, int height) {
        int frameSize = width * height;
        byte[] output = new byte[frameSize*3/2];
        int qFrameSize = frameSize / 4;
        int tempFrameSize = frameSize * 5 / 4;
        System.arraycopy(input, 0, output, 0, frameSize);

        for(int i = 0; i < qFrameSize; ++i) {
            output[frameSize + i * 2] = input[tempFrameSize + i];
            output[frameSize + i * 2 + 1] = input[frameSize + i];
        }

        return output;
    }

//    NV21转nv12
    private void swapNV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i = 0,j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j + 1] = nv21[j + framesize];
        }

        for (j = 0; j < framesize/2; j += 2)
        {
            nv12[framesize + j] = nv21[j + framesize + 1];
        }
    }

//    YV12转I420
    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height)
    {
        System.arraycopy(yv12bytes, 0, i420bytes, 0,width*height);
        System.arraycopy(yv12bytes, width*height+width*height/4, i420bytes, width*height,width*height/4);
        System.arraycopy(yv12bytes, width*height, i420bytes, width*height+width*height/4,width*height/4);
    }

//    yv12转nv12
    void swapYV12toNV12(byte[] yv12bytes, byte[] nv12bytes, int width,int height)
    {
        int nLenY = width * height;
        int nLenU = nLenY / 4;

        System.arraycopy(yv12bytes, 0, nv12bytes, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            nv12bytes[nLenY + 2 * i + 1] = yv12bytes[nLenY + i];
            nv12bytes[nLenY + 2 * i] = yv12bytes[nLenY + nLenU + i];
        }
    }

    //  nv12转I420
    void swapNV12toI420(byte[] nv12bytes, byte[] i420bytes, int width,int height) {
        int nLenY = width * height;
        int nLenU = nLenY / 4;

        System.arraycopy(nv12bytes, 0, i420bytes, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            i420bytes[nLenY + i] = nv12bytes[nLenY + 2 * i + 1];
            i420bytes[nLenY + nLenU + i] = nv12bytes[nLenY + 2 * i];
        }
    }

    /**
     * I420转nv21
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static byte[] I420Tonv21(byte[] data, int width, int height) {
        byte[] ret = new byte[data.length];
        int total = width * height;

        ByteBuffer bufferY = ByteBuffer.wrap(ret, 0, total);
        ByteBuffer bufferV = ByteBuffer.wrap(ret, total, total /4);
        ByteBuffer bufferU = ByteBuffer.wrap(ret, total + total / 4, total /4);

        bufferY.put(data, 0, total);
        for (int i=0; i<total/4; i+=1) {
            bufferV.put(data[total+i]);
            bufferU.put(data[i+total+total/4]);
        }

        return ret;
    }

    public static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    public static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
//        if (!isImageFormatSupported(image)) {
//            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
//        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        LogUtils.v("get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            LogUtils.v("pixelStride " + pixelStride);
            LogUtils.v("rowStride " + rowStride);
            LogUtils.v("width " + width);
            LogUtils.v("height " + height);
            LogUtils.v("buffer size " + buffer.remaining());
//            if (VERBOSE) {
//                Log.v(TAG, "pixelStride " + pixelStride);
//                Log.v(TAG, "rowStride " + rowStride);
//                Log.v(TAG, "width " + width);
//                Log.v(TAG, "height " + height);
//                Log.v(TAG, "buffer size " + buffer.remaining());
//            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            LogUtils.v( "Finished reading data from plane " + i);
        }
        return data;
    }
}
