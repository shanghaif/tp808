package com.library.util;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;

import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * Created by android1 on 2017/9/25.
 */

public class OtherUtil {
    private static final double a = (double) 3 / 4;
    private static final double b = (double) 2 / 3;
    private static final double c = (double) 9 / 16;
    private static final double d = (double) 1 / 2;
    private static final double e = (double) 5 / 9;
    private static final double f = (double) 3 / 5;
    private static final double g = 1;
    private static final double h = (double) 9 / 11;

    public static final int QueueNum = 300;

    public static final int waitTime = 0;
    public static final int samplerate = 44100;

    public static byte setWeitht(double weitht) {
        if (weitht == a) {
            return (byte) 'a';
        } else if (weitht == b) {
            return (byte) 'b';
        } else if (weitht == c) {
            return (byte) 'c';
        } else if (weitht == d) {
            return (byte) 'd';
        } else if (weitht == e) {
            return (byte) 'e';
        } else if (weitht == f) {
            return (byte) 'f';
        } else if (weitht == g) {
            return (byte) 'g';
        } else if (weitht == h) {
            return (byte) 'h';
        }
        return (byte) 'a';
    }

    public static double getWeight(byte weitht) {
        double v;
        switch (weitht) {
            case (byte) 'a':
                v = a;
                break;
            case (byte) 'b':
                v = b;
                break;
            case (byte) 'c':
                v = c;
                break;
            case (byte) 'd':
                v = d;
                break;
            case (byte) 'e':
                v = e;
                break;
            case (byte) 'f':
                v = f;
                break;
            case (byte) 'g':
                v = g;
                break;
            case (byte) 'h':
                v = h;
                break;
            default:
                v = a;
                break;
        }
        return v;
    }

    public static int getTime(int i) {
        return (int) (System.currentTimeMillis() % (long) 1000000000);
    }
    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static long getFPS() {
        return System.nanoTime() / 1000;
    }

    public static <T> void addQueue(ArrayBlockingQueue<T> queue, T t) {
        if (queue.size() >= QueueNum) {
            queue.poll();
        }
        queue.offer(t);
    }

    public static <T extends Closeable> void close(T t) {
        if (t != null) {
            try {
                t.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * 检测是否支持H265硬编码
     * @return 检测结果
     */
    public static boolean isH265EncoderSupport(){
        int count = MediaCodecList.getCodecCount();
        for(int i=0;i<count;i++){
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            String name = info.getName();
            boolean b = info.isEncoder();
            Log.d("RecordEncoderVD", " ----------isH265EncoderSupport:"+name+",b:"+b);
            if(b && (name.contains("hevc")||name.contains("h265"))){
                return true;
            }
        }
        return false;
    }

//    /**
//     * 检测是否支持指定格式硬编码
//     * @return 检测结果
//     */
//    public static boolean isH265EncoderSupport(String type){
//        int count = MediaCodecList.getCodecCount();
//        for(int i=0;i<count;i++){
//            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
//            String name = info.getName();
//            boolean b = info.isEncoder();
//            Log.d("RecordEncoderVD", " ----------isH265EncoderSupport:"+name+",b:"+b+",type:"+type.split("/")[1]);
//            if(b && name.contains(type.split("/")[1])){
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 检测是否支持H265硬解码
     * @return 检测结果
     */
    public static boolean isH265DecoderSupport(){
        int count = MediaCodecList.getCodecCount();
        for(int i=0;i<count;i++){
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            String name = info.getName();
            Log.d("RecordEncoderVD", " ----------isH265DecoderSupport:"+name);
            if(name.contains("decoder") && (name.contains("hevc")||name.contains("h265"))){
                return true;
            }
        }
        return false;
    }

//    /**
//     * 检测是否支持指定格式硬解码
//     * @return 检测结果
//     */
//    public static boolean isH265DecoderSupport(String type){
//        int count = MediaCodecList.getCodecCount();
//        for(int i=0;i<count;i++){
//            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
//            String name = info.getName();
//            Log.d("RecordEncoderVD", " ----------isH265DecoderSupport:"+name+",type:"+type.split("/")[1]);
//            if(name.contains("decoder") && name.contains(type.split("/")[1])){
//                return true;
//            }
//        }
//        return false;
//    }


//    executeCmd("ping -c 1 -w 1 "+PublicConstants.ApiConstants.USLOGIN_IP, false);
    /**
     * 是否能 ping 通
     * */
    public static String executeCmd(String ip, boolean sudo){
        String cmdMsg = "ping -c 1 -w 1 %s" ;
        String cmd = String.format(cmdMsg, ip) ;
        try {
//            Log.e(TAG, "executeCmd --------------------------------------") ;
            Process p;
            if(!sudo)
                p= Runtime.getRuntime().exec(cmd);
            else{
                p= Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
            }
            p.destroy();
            LogUtils.e("executeCmd --------------------------------------res:"+res) ;
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
